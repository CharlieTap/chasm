package io.github.charlietap.chasm.type.component.canonical

import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.component.ComponentDefinedType
import io.github.charlietap.chasm.type.component.ComponentDefinedValueType
import io.github.charlietap.chasm.type.component.ComponentFunctionType
import io.github.charlietap.chasm.type.component.ComponentPrimitiveType
import io.github.charlietap.chasm.type.component.ComponentValueType
import io.github.charlietap.chasm.type.ext.definedType

internal const val MAX_FLAT_PARAMS = 16
internal const val MAX_FLAT_ASYNC_PARAMS = 4
internal const val MAX_FLAT_RESULTS = 1

enum class CanonicalAbiContext {
    Lift,
    Lower,
}

data class CanonicalAbiSignatureOptions(
    val addressType: AddressType = AddressType.I32,
    val async: Boolean = false,
    val hasCallback: Boolean = false,
)

data class CanonicalAbiLowering(
    val type: DefinedType,
    val requiresMemory: Boolean,
    val requiresRealloc: Boolean,
)

data class CanonicalAbiDescriptor(
    val type: DefinedType,
    val requiresMemory: Boolean = false,
    val requiresRealloc: Boolean = false,
)

fun CanonicalCoreFunctionType(
    params: List<ValueType> = emptyList(),
    results: List<ValueType> = emptyList(),
): DefinedType = FunctionType(
    params = ResultType(params),
    results = ResultType(results),
).definedType()

fun CanonicalFunctionType(
    type: ComponentFunctionType,
    options: CanonicalAbiSignatureOptions,
    context: CanonicalAbiContext,
): DefinedType? = CanonicalFunctionTypeLowering(type, options, context)?.type

fun CanonicalFunctionTypeLowering(
    type: ComponentFunctionType,
    options: CanonicalAbiSignatureOptions,
    context: CanonicalAbiContext,
): CanonicalAbiLowering? {
    val flattener = CanonicalAbiFlattener(options.addressType)
    val flatParams = flattener.flatten(type.params) { parameter -> parameter.type } ?: return null
    val flatResults = type.result?.let(flattener::flatten) ?: FlatResult(emptyList(), false)
    val pointer = options.addressType.coreValueType()

    val parameterLimit = when {
        !options.async -> MAX_FLAT_PARAMS
        context == CanonicalAbiContext.Lift -> MAX_FLAT_PARAMS
        else -> MAX_FLAT_ASYNC_PARAMS
    }
    val resultLimit = when {
        !options.async -> MAX_FLAT_RESULTS
        context == CanonicalAbiContext.Lift -> MAX_FLAT_PARAMS
        else -> 0
    }
    val requiresMemory = when (context) {
        CanonicalAbiContext.Lift -> flatResults.containsListOrString || flatResults.types.size > resultLimit
        CanonicalAbiContext.Lower ->
            options.async && type.result != null ||
                flatParams.containsListOrString ||
                flatParams.types.size > parameterLimit ||
                flatResults.types.size > resultLimit
    }
    val requiresRealloc = when (context) {
        CanonicalAbiContext.Lift -> flatParams.containsListOrString || flatParams.types.size > parameterLimit
        CanonicalAbiContext.Lower -> flatResults.containsListOrString
    }

    val params: List<ValueType>
    val results: List<ValueType>
    if (!options.async) {
        params = if (flatParams.types.size > parameterLimit) listOf(pointer) else flatParams.types
        when {
            flatResults.types.size <= resultLimit -> results = flatResults.types
            context == CanonicalAbiContext.Lift -> results = listOf(pointer)
            else -> return CanonicalAbiLowering(
                type = CanonicalCoreFunctionType(params = params + pointer),
                requiresMemory = requiresMemory,
                requiresRealloc = requiresRealloc,
            )
        }
    } else {
        when (context) {
            CanonicalAbiContext.Lift -> {
                params = if (flatParams.types.size > parameterLimit) listOf(pointer) else flatParams.types
                results = if (options.hasCallback) listOf(I32) else emptyList()
            }
            CanonicalAbiContext.Lower -> {
                val directParams = if (flatParams.types.size > parameterLimit) listOf(pointer) else flatParams.types
                params = if (flatResults.types.isEmpty()) directParams else directParams + pointer
                results = listOf(I32)
            }
        }
    }

    return CanonicalAbiLowering(
        type = CanonicalCoreFunctionType(params, results),
        requiresMemory = requiresMemory,
        requiresRealloc = requiresRealloc,
    )
}

internal fun FlattenComponentTypes(
    types: List<ComponentValueType>,
    addressType: AddressType,
): List<ValueType>? = CanonicalAbiFlattener(addressType).flatten(types) { type -> type }?.types

internal fun FlattenComponentType(
    type: ComponentValueType,
    addressType: AddressType,
): List<ValueType>? = CanonicalAbiFlattener(addressType).flatten(type)?.types

fun ComponentValueType.containsListOrString(): Boolean = when (this) {
    is ComponentValueType.Primitive -> type == ComponentPrimitiveType.String
    is ComponentValueType.Defined -> {
        val value = definition.type as? ComponentDefinedType.Value ?: return false
        value.type.containsListOrString()
    }
}

fun AddressType.coreValueType(): ValueType = when (this) {
    AddressType.I32 -> I32
    AddressType.I64 -> I64
}

private fun ComponentDefinedValueType.containsListOrString(): Boolean = when (this) {
    is ComponentDefinedValueType.Primitive -> type == ComponentPrimitiveType.String
    is ComponentDefinedValueType.Record -> fields.any { field -> field.type.containsListOrString() }
    is ComponentDefinedValueType.Variant -> cases.any { case -> case.type?.containsListOrString() == true }
    is ComponentDefinedValueType.ListValue,
    is ComponentDefinedValueType.Map,
    -> true
    is ComponentDefinedValueType.FixedLengthList -> element.containsListOrString()
    is ComponentDefinedValueType.Tuple -> elements.any(ComponentValueType::containsListOrString)
    is ComponentDefinedValueType.Option -> value.containsListOrString()
    is ComponentDefinedValueType.Result ->
        ok?.containsListOrString() == true || error?.containsListOrString() == true
    is ComponentDefinedValueType.Own,
    is ComponentDefinedValueType.Borrow,
    is ComponentDefinedValueType.Stream,
    is ComponentDefinedValueType.Future,
    is ComponentDefinedValueType.Flags,
    is ComponentDefinedValueType.Enum,
    -> false
}

private data class FlatResult(
    val types: List<ValueType>,
    val containsListOrString: Boolean,
)

private class CanonicalAbiFlattener(
    private val addressType: AddressType,
) {
    private val buffers = mutableListOf(FlatBuffer())
    private var depth = 0

    fun flatten(type: ComponentValueType): FlatResult? {
        val buffer = buffers[0]
        buffer.clear()
        if (!flatten(type, buffer)) return null
        return buffer.result()
    }

    inline fun <T> flatten(
        types: Iterable<T>,
        type: (T) -> ComponentValueType,
    ): FlatResult? {
        val buffer = buffers[0]
        buffer.clear()
        for (value in types) {
            if (!flatten(type(value), buffer)) return null
        }
        return buffer.result()
    }

    private fun flatten(
        type: ComponentValueType,
        buffer: FlatBuffer,
    ): Boolean {
        if (buffer.full) {
            buffer.containsListOrString = buffer.containsListOrString || type.containsListOrString()
            return true
        }
        return when (type) {
            is ComponentValueType.Primitive -> flattenPrimitive(type.type, buffer)
            is ComponentValueType.Defined -> {
                val value = type.definition.type as? ComponentDefinedType.Value ?: return false
                flatten(value.type, buffer)
            }
        }
    }

    private fun flatten(
        type: ComponentDefinedValueType,
        buffer: FlatBuffer,
    ): Boolean = when (type) {
        is ComponentDefinedValueType.Primitive -> flattenPrimitive(type.type, buffer)
        is ComponentDefinedValueType.Record -> type.fields.all { field -> flatten(field.type, buffer) }
        is ComponentDefinedValueType.Variant -> flattenVariant(type, buffer)
        is ComponentDefinedValueType.ListValue,
        is ComponentDefinedValueType.Map,
        -> {
            buffer.add(addressType.coreValueType())
            buffer.add(addressType.coreValueType())
            buffer.containsListOrString = true
            true
        }
        is ComponentDefinedValueType.FixedLengthList -> flattenFixedLengthList(type, buffer)
        is ComponentDefinedValueType.Tuple -> type.elements.all { element -> flatten(element, buffer) }
        is ComponentDefinedValueType.Flags,
        is ComponentDefinedValueType.Enum,
        is ComponentDefinedValueType.Own,
        is ComponentDefinedValueType.Borrow,
        is ComponentDefinedValueType.Stream,
        is ComponentDefinedValueType.Future,
        -> {
            buffer.add(I32)
            true
        }
        is ComponentDefinedValueType.Option -> flattenOption(type.value, buffer)
        is ComponentDefinedValueType.Result -> flattenResult(type, buffer)
    }

    private fun flattenPrimitive(
        type: ComponentPrimitiveType,
        buffer: FlatBuffer,
    ): Boolean = when (type) {
        ComponentPrimitiveType.Bool,
        ComponentPrimitiveType.S8,
        ComponentPrimitiveType.U8,
        ComponentPrimitiveType.S16,
        ComponentPrimitiveType.U16,
        ComponentPrimitiveType.S32,
        ComponentPrimitiveType.U32,
        ComponentPrimitiveType.Char,
        ComponentPrimitiveType.ErrorContext,
        -> buffer.add(I32).let { true }
        ComponentPrimitiveType.S64,
        ComponentPrimitiveType.U64,
        -> buffer.add(I64).let { true }
        ComponentPrimitiveType.F32 -> buffer.add(F32).let { true }
        ComponentPrimitiveType.F64 -> buffer.add(F64).let { true }
        ComponentPrimitiveType.String -> {
            buffer.add(addressType.coreValueType())
            buffer.add(addressType.coreValueType())
            buffer.containsListOrString = true
            true
        }
    }

    private fun flattenFixedLengthList(
        type: ComponentDefinedValueType.FixedLengthList,
        buffer: FlatBuffer,
    ): Boolean {
        val scratch = acquireBuffer()
        val flattened = flatten(type.element, scratch)
        releaseBuffer()
        if (!flattened) return false

        buffer.containsListOrString = buffer.containsListOrString || scratch.containsListOrString
        var index = 0u
        while (index < type.length && !buffer.full) {
            buffer.append(scratch)
            index += 1u
        }
        return true
    }

    private fun flattenVariant(
        type: ComponentDefinedValueType.Variant,
        buffer: FlatBuffer,
    ): Boolean {
        buffer.add(I32)
        val payloadStart = buffer.size
        for (case in type.cases) {
            if (!mergeVariantCase(case.type, buffer, payloadStart)) return false
        }
        return true
    }

    private fun flattenOption(
        value: ComponentValueType,
        buffer: FlatBuffer,
    ): Boolean {
        buffer.add(I32)
        return mergeVariantCase(value, buffer, buffer.size)
    }

    private fun flattenResult(
        type: ComponentDefinedValueType.Result,
        buffer: FlatBuffer,
    ): Boolean {
        buffer.add(I32)
        val payloadStart = buffer.size
        if (!mergeVariantCase(type.ok, buffer, payloadStart)) return false
        return mergeVariantCase(type.error, buffer, payloadStart)
    }

    private fun mergeVariantCase(
        type: ComponentValueType?,
        buffer: FlatBuffer,
        payloadStart: Int,
    ): Boolean {
        if (type == null) return true
        val scratch = acquireBuffer()
        val flattened = flatten(type, scratch)
        releaseBuffer()
        if (!flattened) return false

        buffer.containsListOrString = buffer.containsListOrString || scratch.containsListOrString
        for (index in 0 until scratch.size) buffer.join(payloadStart + index, scratch[index])
        return true
    }

    private fun acquireBuffer(): FlatBuffer {
        depth += 1
        val buffer = buffers.getOrNull(depth) ?: FlatBuffer().also(buffers::add)
        buffer.clear()
        return buffer
    }

    private fun releaseBuffer() {
        depth -= 1
    }
}

private class FlatBuffer {
    private val values = arrayOfNulls<ValueType>(FLAT_BUFFER_CAPACITY)
    var size: Int = 0
        private set
    var containsListOrString: Boolean = false

    val full: Boolean
        get() = size == FLAT_BUFFER_CAPACITY

    operator fun get(index: Int): ValueType = values[index]!!

    fun add(type: ValueType) {
        if (!full) values[size++] = type
    }

    fun append(other: FlatBuffer) {
        for (index in 0 until other.size) add(other[index])
    }

    fun join(
        index: Int,
        type: ValueType,
    ) {
        if (index >= FLAT_BUFFER_CAPACITY) return
        if (index == size) {
            add(type)
        } else {
            values[index] = join(values[index]!!, type)
        }
    }

    fun clear() {
        size = 0
        containsListOrString = false
    }

    fun result(): FlatResult = FlatResult(
        types = List(size) { index -> values[index]!! },
        containsListOrString = containsListOrString,
    )
}

private fun join(
    first: ValueType,
    second: ValueType,
): ValueType = when {
    first == second -> first
    (first == I32 && second == F32) || (first == F32 && second == I32) -> I32
    else -> I64
}

private const val FLAT_BUFFER_CAPACITY = MAX_FLAT_PARAMS + 1

private val I32 = ValueType.Number(NumberType.I32)
private val I64 = ValueType.Number(NumberType.I64)
private val F32 = ValueType.Number(NumberType.F32)
private val F64 = ValueType.Number(NumberType.F64)
