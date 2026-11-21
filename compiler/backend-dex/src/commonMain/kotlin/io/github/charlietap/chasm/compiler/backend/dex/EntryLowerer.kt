package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.compiler.backend.abi.ARRAYCOPY
import io.github.charlietap.chasm.compiler.backend.abi.BRIDGE_DEPTH
import io.github.charlietap.chasm.compiler.backend.abi.CALL_STACK_EXHAUSTED
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.DOUBLE_TO_RAW_LONG_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FLOAT_TO_RAW_INT_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.GUEST_HEAP_EXHAUSTED
import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_FROM_HOST
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_TO_HOST
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
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType
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
    val reference: String = "J",
) {
    val crossesWithObjects: Boolean
        get() = reference != "J" && (type.params.types + type.results.types).any { value -> value is ValueType.Reference }
}

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
    if (group.crossesWithObjects) callObjectsMethod(builder, entry, group)
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

private fun invokeMethod(
    builder: ClassBuilder,
    entry: ClassType,
    group: EntryGroup,
) {
    val type = group.type
    val arrayArguments = takesArgumentArray(type)
    val descriptors = entryDescriptors(group)
    val returns = returnDescriptor(type, group.reference)
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
        if (takesReferenceArray(type, group.reference)) {
            referenceArguments(code, group, Reg(offset + 1)) { parameter ->
                code.binary(LiteralOp.AddInt, INDEX, fp, parameter)
                code.arrayGet(ArrayKind.Wide, WIDE, frame, INDEX)
            }
        }
    } else {
        descriptors.forEachIndexed { parameter, descriptor ->
            code.binary(LiteralOp.AddInt, INDEX, fp, parameter)
            decodeArgument(code, frame, descriptor, Reg(offset), group.reference, context)
            offset += descriptorWords(descriptor)
        }
    }
    dispatch(code, group, returns)
    if (returns == "[J") {
        val stack = Reg(WINDOW)
        code.branchZero(Cond.Eq, NARROW, end)
        loadResultReferences(code, group)
        code.invokeStatic(FRAME.method, context)
        code.moveResult(stack)
        for (result in type.results.types.indices) {
            loadResult(code, group, result)
            code.const(INDEX, result)
            code.binary(BinaryOp.AddInt, INDEX, fp, INDEX)
            code.arrayPut(ArrayKind.Wide, WIDE, stack, INDEX)
        }
    } else if (returns != "V") {
        encodeResult(code, returns, group, context)
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
    val descriptors = entryDescriptors(group)
    val returns = returnDescriptor(type, group.reference)
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
        if (takesReferenceArray(type, group.reference)) {
            referenceArguments(code, group, Reg(offset + 1)) { parameter ->
                code.const(INDEX, parameter)
                code.arrayGet(ArrayKind.Wide, WIDE, values, INDEX)
            }
        }
    } else {
        descriptors.forEachIndexed { parameter, descriptor ->
            code.const(INDEX, parameter)
            decodeArgument(code, values, descriptor, Reg(offset), group.reference, context)
            offset += descriptorWords(descriptor)
        }
    }
    dispatch(code, group, returns)
    if (returns == "[J") {
        code.branchZero(Cond.Eq, NARROW, end)
        loadResultReferences(code, group)
        for (result in type.results.types.indices) {
            loadResult(code, group, result)
            code.arrayPut(ArrayKind.Wide, WIDE, values, INDEX)
        }
    } else if (returns != "V") {
        encodeResult(code, returns, group, context)
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
    val typed = typedCall(entry.descriptor, group.type, group.reference)
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
    val returns = returnDescriptor(type, group.reference)
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
        if (takesReferenceArray(type, group.reference)) code.moveObject(Reg(offset + 1), references)
    } else {
        descriptors.forEachIndexed { parameter, descriptor ->
            code.const(INDEX, parameter)
            if (descriptor == group.reference) {
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
        group.reference -> {
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
        takesReferenceArray(type, group.reference) -> listOf("[J", OBJECT_ARRAY)
        takesArgumentArray(type) -> listOf("[J")
        else -> type.params.types.map { parameter -> valueDescriptor(parameter, group.reference) }
    }
}

private inline fun referenceArguments(
    code: CodeEmitter,
    group: EntryGroup,
    target: Reg,
    loadEncoded: (Int) -> Unit,
) {

    val parameters = group.type.params.types
    code.const(INDEX, parameters.size)
    code.newArray(target, INDEX, DexType.Object.array())
    parameters.forEachIndexed { parameter, type ->
        if (type !is ValueType.Reference) return@forEachIndexed
        loadEncoded(parameter)
        code.invokeStatic(REFERENCE_FROM_HOST.method, Reg(WINDOW + 1), WIDE)
        code.moveResult(NARROW)
        code.const(INDEX, parameter)
        code.arrayPut(ArrayKind.Object, NARROW, target, INDEX)
    }
}

private fun loadResultReferences(
    code: CodeEmitter,
    group: EntryGroup,
) {

    val results = group.type.results.types
    if (group.reference == "J" || results.none { result -> result is ValueType.Reference }) {
        return
    }

    code.const(INDEX, results.size)
    code.invokeStatic(RESULT_REFERENCES.method, Reg(WINDOW + 1), INDEX)
    code.moveResult(SELECTOR)
}

private fun loadResult(
    code: CodeEmitter,
    group: EntryGroup,
    result: Int,
) {

    val type = group.type.results.types[result]
    code.const(INDEX, result)
    if (group.reference == "J" || type !is ValueType.Reference) {
        return code.arrayGet(ArrayKind.Wide, WIDE, NARROW, INDEX)
    }

    code.arrayGet(ArrayKind.Object, Reg(WINDOW + 2), SELECTOR, INDEX)
    code.const(Reg(WINDOW + 3), if (isExternReference(type)) 1 else 0)
    code.invokeStatic(REFERENCE_TO_HOST.method, Reg(WINDOW + 1), Reg(WINDOW + 2), Reg(WINDOW + 3))
    code.moveResult(WIDE)
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
    reference: String = "J",
    context: Reg? = null,
) {
    if (descriptor == "J") return code.arrayGet(ArrayKind.Wide, target, array, INDEX)
    if (descriptor == reference) {
        code.arrayGet(ArrayKind.Wide, WIDE, array, INDEX)
        code.moveObject(NARROW, checkNotNull(context))
        code.invokeStatic(REFERENCE_FROM_HOST.method, NARROW, WIDE)
        code.moveResult(target)
        return
    }
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

internal fun isExternReference(
    type: ValueType,
): Boolean {
    val heap = (type as? ValueType.Reference)?.referenceType?.heapType
    return heap == AbstractHeapType.Extern || heap == AbstractHeapType.NoExtern
}

private fun encodeResult(
    code: CodeEmitter,
    descriptor: String,
    group: EntryGroup? = null,
    context: Reg? = null,
) {
    if (group != null && descriptor == group.reference && descriptor != "J") {
        code.moveObject(NARROW, checkNotNull(context))
        code.const(INDEX, if (isExternReference(group.type.results.types.single())) 1 else 0)
        code.invokeStatic(REFERENCE_TO_HOST.method, NARROW, WIDE, INDEX)
        code.moveResult(WIDE)
        return
    }
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
