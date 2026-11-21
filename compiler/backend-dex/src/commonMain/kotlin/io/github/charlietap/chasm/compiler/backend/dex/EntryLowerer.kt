package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.compiler.backend.abi.CALL_STACK_EXHAUSTED
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.DOUBLE_TO_RAW_LONG_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FLOAT_TO_RAW_INT_BITS
import io.github.charlietap.chasm.compiler.backend.abi.GUEST_HEAP_EXHAUSTED
import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.abi.RESULT_REFERENCES
import io.github.charlietap.chasm.compiler.backend.abi.UNREACHABLE
import io.github.charlietap.chasm.compiler.backend.abi.descriptorWords
import io.github.charlietap.chasm.compiler.backend.abi.returnDescriptor
import io.github.charlietap.chasm.compiler.backend.abi.takesArgumentArray
import io.github.charlietap.chasm.compiler.backend.abi.takesReferenceArray
import io.github.charlietap.chasm.compiler.backend.abi.valueDescriptor
import io.github.charlietap.chasm.compiler.backend.dex.abi.DEX_ENTRY
import io.github.charlietap.chasm.compiler.backend.dex.abi.ENTRY_BINDING_FIELD
import io.github.charlietap.chasm.compiler.backend.dex.abi.ENTRY_ID_FIELD
import io.github.charlietap.chasm.compiler.backend.dex.abi.entryClass
import io.github.charlietap.chasm.compiler.backend.dex.abi.method
import io.github.charlietap.chasm.compiler.backend.dex.abi.typedCall
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.dexter.ArrayKind
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
    val referenceDescriptor: String,
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
    callObjectsMethod(builder, entry, group)
    if (signature != null) typedMethod(builder, entry, group)
    return entry.descriptor
}

private val ENTRY = ClassType(DEX_ENTRY)
private val ENTRY_BINDING = DexField(ENTRY, ENTRY_BINDING_FIELD, DexType.Object.array())
private val ENTRY_ID = DexField(ENTRY, ENTRY_ID_FIELD, DexType.Int)
private val ENTRY_CONSTRUCTOR = DexConstructor(ENTRY, DexType.Object.array(), DexType.Int)
private val STACK_OVERFLOW = ClassType("Ljava/lang/StackOverflowError;")
private val OUT_OF_MEMORY = ClassType("Ljava/lang/OutOfMemoryError;")
private val CONTEXT = ClassType(CONTEXT_TYPE)

private val WIDE = Reg(0)
private val NARROW = Reg(2)
private val INDEX = Reg(3)
private val SELECTOR = Reg(4)
private const val WINDOW = 5
private const val OBJECT_ARRAY = "[Ljava/lang/Object;"

private fun typedMethod(
    builder: ClassBuilder,
    entry: ClassType,
    group: EntryGroup,
) {
    val typed = typedCall(entry.descriptor, group.type, group.referenceDescriptor)
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

private fun callObjectsMethod(
    builder: ClassBuilder,
    entry: ClassType,
    group: EntryGroup,
) {
    val type = group.type
    val arrayArguments = takesArgumentArray(type)
    val descriptors = entryDescriptors(group)
    val returns = returnDescriptor(type, group.referenceDescriptor)
    val signature = DexMethod(entry, "callObjects", DexType.Void, CONTEXT, DexType.Long.array(), DexType.Object.array(), DexType.Int)
    val registers = WINDOW + 3 + descriptors.sumOf(::descriptorWords) + 5
    val code = builder.beginInstanceMethod(signature, access = MemberAccess.Public, registerCount = registers)
    val context = code.parameter(0)
    val values = code.parameter(1)
    val references = code.parameter(2)
    val depth = code.parameter(3)
    val start = code.boundLabel()
    val end = code.label()
    val overflow = code.handlerLabel()
    loadBinding(code, Reg(WINDOW), group)
    code.moveObject(Reg(WINDOW + 1), context)
    code.move(Reg(WINDOW + 2), depth)
    var offset = WINDOW + 3
    if (arrayArguments) {
        code.moveObject(Reg(offset), values)
        if (takesReferenceArray(type, group.referenceDescriptor)) code.moveObject(Reg(offset + 1), references)
    } else {
        descriptors.forEachIndexed { parameter, descriptor ->
            code.const(INDEX, parameter)
            if (descriptor == group.referenceDescriptor) {
                code.arrayGet(ArrayKind.Object, Reg(offset), references, INDEX)
            } else {
                decodeArgument(code, values, descriptor, Reg(offset))
            }
            offset += descriptorWords(descriptor)
        }
    }
    dispatch(code, group, returns)
    when (returns) {
        "V" -> Unit
        "[J" -> {
            code.branchZero(Cond.Eq, NARROW, end)
            loadResultReferences(code, group)
            type.results.types.forEachIndexed { result, value ->
                code.const(INDEX, result)
                if (value is ValueType.Reference) {
                    code.arrayGet(ArrayKind.Object, Reg(WINDOW + 2), SELECTOR, INDEX)
                    code.arrayPut(ArrayKind.Object, Reg(WINDOW + 2), references, INDEX)
                } else {
                    code.arrayGet(ArrayKind.Wide, WIDE, NARROW, INDEX)
                    code.arrayPut(ArrayKind.Wide, WIDE, values, INDEX)
                }
            }
        }
        group.referenceDescriptor -> {
            code.const(INDEX, 0)
            code.arrayPut(ArrayKind.Object, WIDE, references, INDEX)
        }
        else -> {
            encodeResult(code, returns)
            code.const(INDEX, 0)
            code.arrayPut(ArrayKind.Wide, WIDE, values, INDEX)
        }
    }
    returnOrOverflow(code, start, end, overflow)
}

private fun entryDescriptors(
    group: EntryGroup,
): List<String> {
    val type = group.type
    return when {
        takesReferenceArray(type, group.referenceDescriptor) -> listOf("[J", OBJECT_ARRAY)
        takesArgumentArray(type) -> listOf("[J")
        else -> type.params.types.map { parameter -> valueDescriptor(parameter, group.referenceDescriptor) }
    }
}

private fun loadResultReferences(
    code: CodeEmitter,
    group: EntryGroup,
) {

    val results = group.type.results.types
    if (results.none { result -> result is ValueType.Reference }) {
        return
    }

    code.const(INDEX, results.size)
    code.invokeStatic(RESULT_REFERENCES.method, Reg(WINDOW + 1), INDEX)
    code.moveResult(SELECTOR)
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
    val exhausted = code.handlerLabel()
    code.bind(end)
    code.returnVoid()
    code.bindHandler(overflow)
    code.invokeStatic(CALL_STACK_EXHAUSTED.method)
    code.returnVoid()
    code.bindHandler(exhausted)
    code.invokeStatic(GUEST_HEAP_EXHAUSTED.method)
    code.returnVoid()
    code.catchRange(start, end, STACK_OVERFLOW, overflow)
    code.catchRange(start, end, OUT_OF_MEMORY, exhausted)
    code.finish()
}
