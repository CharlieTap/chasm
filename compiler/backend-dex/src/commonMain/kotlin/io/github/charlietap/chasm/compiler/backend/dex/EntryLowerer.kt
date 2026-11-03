package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.compiler.backend.abi.ARRAYCOPY
import io.github.charlietap.chasm.compiler.backend.abi.BRIDGE_DEPTH
import io.github.charlietap.chasm.compiler.backend.abi.CALL_STACK_EXHAUSTED
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.DOUBLE_TO_RAW_LONG_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FLOAT_TO_RAW_INT_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.abi.UNREACHABLE
import io.github.charlietap.chasm.compiler.backend.abi.descriptorWords
import io.github.charlietap.chasm.compiler.backend.abi.returnDescriptor
import io.github.charlietap.chasm.compiler.backend.abi.takesArgumentArray
import io.github.charlietap.chasm.compiler.backend.abi.valueDescriptor
import io.github.charlietap.chasm.compiler.backend.dex.abi.DEX_ENTRY
import io.github.charlietap.chasm.compiler.backend.dex.abi.ENTRY_BINDING_FIELD
import io.github.charlietap.chasm.compiler.backend.dex.abi.ENTRY_ID_FIELD
import io.github.charlietap.chasm.compiler.backend.dex.abi.entryClass
import io.github.charlietap.chasm.compiler.backend.dex.abi.method
import io.github.charlietap.chasm.compiler.backend.dex.abi.typedCall
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.ClassBuilder
import io.github.charlietap.dexter.ClassType
import io.github.charlietap.dexter.CodeEmitter
import io.github.charlietap.dexter.Cond
import io.github.charlietap.dexter.ConstructorAccess
import io.github.charlietap.dexter.Conversion
import io.github.charlietap.dexter.DexConstructor
import io.github.charlietap.dexter.DexField
import io.github.charlietap.dexter.DexFile
import io.github.charlietap.dexter.DexMethod
import io.github.charlietap.dexter.DexType
import io.github.charlietap.dexter.HandlerLabel
import io.github.charlietap.dexter.InstanceCodeEmitter
import io.github.charlietap.dexter.Label
import io.github.charlietap.dexter.LiteralOp
import io.github.charlietap.dexter.MemberAccess
import io.github.charlietap.dexter.Reg

internal class LoweredFunction(
    val index: Int,
    val method: DexMethod,
)

internal class EntryGroup(
    val number: Int,
    val type: FunctionType,
    val signature: String?,
    val members: List<LoweredFunction>,
)

internal typealias EntryLowerer = (DexFile, EntryGroup, String) -> String

internal fun EntryLowerer(
    file: DexFile,
    group: EntryGroup,
    packagePath: String,
): String {
    val entry = ClassType(entryClass(packagePath, group.number))
    val signature = group.signature
    val builder = if (signature == null) {
        file.addClass(entry, ENTRY)
    } else {
        file.addClass(entry, ENTRY, ClassType(signature))
    }
    val constructor = builder.beginConstructor(
        DexConstructor(entry, DexType.Object.array(), DexType.Int),
        registerCount = 3,
        access = ConstructorAccess.Public,
    )
    constructor.invokeConstructor(ENTRY_CONSTRUCTOR, constructor.self, constructor.parameter(0), constructor.parameter(1))
    constructor.returnVoid()
    constructor.finish()
    invokeMethod(builder, entry, group)
    callMethod(builder, entry, group)
    if (signature != null) typedMethod(builder, entry, group)
    return entry.descriptor
}

private val ENTRY = ClassType(DEX_ENTRY)
private val ENTRY_BINDING = DexField(ENTRY, ENTRY_BINDING_FIELD, DexType.Object.array())
private val ENTRY_ID = DexField(ENTRY, ENTRY_ID_FIELD, DexType.Int)
private val ENTRY_CONSTRUCTOR = DexConstructor(ENTRY, DexType.Object.array(), DexType.Int)
private val STACK_OVERFLOW = ClassType("Ljava/lang/StackOverflowError;")
private val CONTEXT = ClassType(CONTEXT_TYPE)

private val WIDE = Reg(0)
private val NARROW = Reg(2)
private val INDEX = Reg(3)
private val SELECTOR = Reg(4)
private const val WINDOW = 5

private fun invokeMethod(
    builder: ClassBuilder,
    entry: ClassType,
    group: EntryGroup,
) {
    val type = group.type
    val arrayArguments = takesArgumentArray(type)
    val descriptors = if (arrayArguments) listOf("[J") else type.params.types.map(::valueDescriptor)
    val returns = returnDescriptor(type)
    val signature = DexMethod(entry, "invoke", DexType.Void, CONTEXT, DexType.Long.array(), DexType.Int)
    val registers = WINDOW + 3 + descriptors.sumOf(::descriptorWords) + 4
    val code = builder.beginInstanceMethod(signature, access = MemberAccess.Public, registerCount = registers)
    val context = code.parameter(0)
    val frame = code.parameter(1)
    val fp = code.parameter(2)
    val start = code.boundLabel()
    val end = code.label()
    val overflow = code.handlerLabel()
    loadBinding(code, Reg(WINDOW), group)
    code.moveObject(Reg(WINDOW + 1), context)
    code.invokeStatic(BRIDGE_DEPTH.method, Reg(WINDOW + 1))
    code.moveResult(Reg(WINDOW + 2))
    var offset = WINDOW + 3
    if (arrayArguments) {
        val parameters = type.params.types.size
        code.const(INDEX, parameters)
        code.newArray(Reg(offset), INDEX, DexType.Long.array())
        code.const(INDEX, parameters)
        code.const(WIDE, 0)
        code.invokeStatic(ARRAYCOPY.method, frame, fp, Reg(offset), WIDE, INDEX)
    } else {
        descriptors.forEachIndexed { parameter, descriptor ->
            code.binary(LiteralOp.AddInt, INDEX, fp, parameter)
            decodeArgument(code, frame, descriptor, Reg(offset))
            offset += descriptorWords(descriptor)
        }
    }
    dispatch(code, group, returns)
    if (returns == "[J") {
        val results = NARROW
        val stack = Reg(WINDOW)
        code.branchZero(Cond.Eq, results, end)
        code.invokeStatic(FRAME.method, context)
        code.moveResult(stack)
        for (result in type.results.types.indices) {
            code.const(INDEX, result)
            code.arrayGet(ArrayKind.Wide, WIDE, results, INDEX)
            code.const(INDEX, result)
            code.binary(BinaryOp.AddInt, INDEX, fp, INDEX)
            code.arrayPut(ArrayKind.Wide, WIDE, stack, INDEX)
        }
    } else if (returns != "V") {
        encodeResult(code, returns)
        code.invokeStatic(FRAME.method, context)
        code.moveResult(Reg(WINDOW))
        code.arrayPut(ArrayKind.Wide, WIDE, Reg(WINDOW), fp)
    }
    returnOrOverflow(code, start, end, overflow)
}

private fun callMethod(
    builder: ClassBuilder,
    entry: ClassType,
    group: EntryGroup,
) {
    val type = group.type
    val arrayArguments = takesArgumentArray(type)
    val descriptors = if (arrayArguments) listOf("[J") else type.params.types.map(::valueDescriptor)
    val returns = returnDescriptor(type)
    val signature = DexMethod(entry, "call", DexType.Void, CONTEXT, DexType.Long.array(), DexType.Int)
    val registers = WINDOW + 3 + descriptors.sumOf(::descriptorWords) + 4
    val code = builder.beginInstanceMethod(signature, access = MemberAccess.Public, registerCount = registers)
    val context = code.parameter(0)
    val values = code.parameter(1)
    val depth = code.parameter(2)
    val start = code.boundLabel()
    val end = code.label()
    val overflow = code.handlerLabel()
    loadBinding(code, Reg(WINDOW), group)
    code.moveObject(Reg(WINDOW + 1), context)
    code.move(Reg(WINDOW + 2), depth)
    var offset = WINDOW + 3
    if (arrayArguments) {
        code.moveObject(Reg(offset), values)
    } else {
        descriptors.forEachIndexed { parameter, descriptor ->
            code.const(INDEX, parameter)
            decodeArgument(code, values, descriptor, Reg(offset))
            offset += descriptorWords(descriptor)
        }
    }
    dispatch(code, group, returns)
    if (returns == "[J") {
        val results = NARROW
        code.branchZero(Cond.Eq, results, end)
        for (result in type.results.types.indices) {
            code.const(INDEX, result)
            code.arrayGet(ArrayKind.Wide, WIDE, results, INDEX)
            code.arrayPut(ArrayKind.Wide, WIDE, values, INDEX)
        }
    } else if (returns != "V") {
        encodeResult(code, returns)
        code.const(INDEX, 0)
        code.arrayPut(ArrayKind.Wide, WIDE, values, INDEX)
    }
    returnOrOverflow(code, start, end, overflow)
}

private fun typedMethod(
    builder: ClassBuilder,
    entry: ClassType,
    group: EntryGroup,
) {
    val typed = typedCall(entry.descriptor, group.type)
    val parameters = typed.parameters
    val returns = typed.returns
    val signature = typed.method
    val code = builder.beginInstanceMethod(signature, access = MemberAccess.Public, registerCount = 3 + parameters.sumOf(::descriptorWords))
    val selector = Reg(1)
    if (group.members.size > 1) code.getField(selector, code.self, ENTRY_ID)
    code.getField(Reg(0), code.self, ENTRY_BINDING)
    code.moveObject(code.self, Reg(0))
    cases(code, group, selector) { method ->
        code.invokeStaticRange(method, code.self)
        if (returns == "V") {
            code.returnVoid()
        } else {
            code.moveResult(Reg(0))
            code.returnValue(Reg(0))
        }
    }
    code.finish()
}

private fun dispatch(
    code: CodeEmitter,
    group: EntryGroup,
    returns: String,
) {
    val join = code.label()
    cases(code, group, SELECTOR) { method ->
        code.invokeStaticRange(method, Reg(WINDOW))
        when (returns) {
            "V" -> Unit
            "I", "F", "[J" -> code.moveResult(NARROW)
            else -> code.moveResult(WIDE)
        }
        if (group.members.size > 1) code.goto(join)
    }
    if (group.members.size > 1) code.bind(join)
}

private inline fun cases(
    code: CodeEmitter,
    group: EntryGroup,
    selector: Reg,
    body: (DexMethod) -> Unit,
) {
    val members = group.members
    if (members.size == 1) return body(members.single().method)
    val labels = members.map { code.label() }
    val table = code.labelTable(0, members.size, labels.first())
    for (case in 1 until members.size) table[case] = labels[case]
    code.packedSwitch(selector, table)
    code.invokeStatic(UNREACHABLE.method)
    code.goto(labels.first())
    members.forEachIndexed { case, member ->
        code.bind(labels[case])
        body(member.method)
    }
}

private fun decodeArgument(
    code: CodeEmitter,
    array: Reg,
    descriptor: String,
    target: Reg,
) {
    if (descriptor == "J") return code.arrayGet(ArrayKind.Wide, target, array, INDEX)
    code.arrayGet(ArrayKind.Wide, WIDE, array, INDEX)
    when (descriptor) {
        "I" -> {
            code.convert(Conversion.LongToInt, NARROW, WIDE)
            code.move(target, NARROW)
        }
        "F" -> {
            code.convert(Conversion.LongToInt, NARROW, WIDE)
            code.invokeStatic(INT_BITS_TO_FLOAT.method, NARROW)
            code.moveResult(NARROW)
            code.move(target, NARROW)
        }
        else -> {
            code.invokeStatic(LONG_BITS_TO_DOUBLE.method, WIDE)
            code.moveResult(WIDE)
            code.moveWide(target, WIDE)
        }
    }
}

private fun encodeResult(
    code: CodeEmitter,
    descriptor: String,
) {
    when (descriptor) {
        "I" -> code.convert(Conversion.IntToLong, WIDE, NARROW)
        "F" -> {
            code.invokeStatic(FLOAT_TO_RAW_INT_BITS.method, NARROW)
            code.moveResult(NARROW)
            code.convert(Conversion.IntToLong, WIDE, NARROW)
        }
        "D" -> {
            code.invokeStatic(DOUBLE_TO_RAW_LONG_BITS.method, WIDE)
            code.moveResult(WIDE)
        }
        else -> Unit
    }
}

private fun loadBinding(
    code: InstanceCodeEmitter,
    target: Reg,
    group: EntryGroup,
) {
    if (code.self != NARROW) code.moveObject(NARROW, code.self)
    if (group.members.size > 1) code.getField(SELECTOR, NARROW, ENTRY_ID)
    code.getField(target, NARROW, ENTRY_BINDING)
}

private fun returnOrOverflow(
    code: CodeEmitter,
    start: Label,
    end: Label,
    overflow: HandlerLabel,
) {
    code.bind(end)
    code.returnVoid()
    code.bindHandler(overflow)
    code.invokeStatic(CALL_STACK_EXHAUSTED.method)
    code.returnVoid()
    code.catchRange(start, end, STACK_OVERFLOW, overflow)
    code.finish()
}
