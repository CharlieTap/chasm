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

const val MAX_FLAT_PARAMS = 16
const val MAX_FLAT_ASYNC_PARAMS = 4
const val MAX_FLAT_RESULTS = 1

enum class CanonicalAbiDeferredType {
    ErrorContext,
    FixedLengthList,
    Map,
    Stream,
    Future,
}

data class CanonicalAbiProperties(
    val containsString: Boolean = false,
    val containsDynamicList: Boolean = false,
    val containsResource: Boolean = false,
    val containsBorrow: Boolean = false,
    val deferredTypes: Set<CanonicalAbiDeferredType> = emptySet(),
) {
    val requiresAllocation: Boolean
        get() = containsString ||
            containsDynamicList ||
            CanonicalAbiDeferredType.Map in deferredTypes

    val requiresAllocationSensitiveHandling: Boolean
        get() = requiresAllocation || containsResource
}

data class CanonicalAbiShape(
    val flatTypes: List<ValueType>,
    val properties: CanonicalAbiProperties,
)

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
    val flatResults = type.result?.let(flattener::flatten) ?: CanonicalAbiShape(
        flatTypes = emptyList(),
        properties = CanonicalAbiProperties(),
    )
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
        CanonicalAbiContext.Lift ->
            flatResults.properties.requiresAllocation || flatResults.flatTypes.size > resultLimit
        CanonicalAbiContext.Lower ->
            options.async && type.result != null ||
                flatParams.properties.requiresAllocation ||
                flatParams.flatTypes.size > parameterLimit ||
                flatResults.flatTypes.size > resultLimit
    }
    val requiresRealloc = when (context) {
        CanonicalAbiContext.Lift ->
            flatParams.properties.requiresAllocation || flatParams.flatTypes.size > parameterLimit
        CanonicalAbiContext.Lower -> flatResults.properties.requiresAllocation
    }

    val params: List<ValueType>
    val results: List<ValueType>
    if (!options.async) {
        params = if (flatParams.flatTypes.size > parameterLimit) listOf(pointer) else flatParams.flatTypes
        when {
            flatResults.flatTypes.size <= resultLimit -> results = flatResults.flatTypes
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
                params = if (flatParams.flatTypes.size > parameterLimit) listOf(pointer) else flatParams.flatTypes
                results = if (options.hasCallback) listOf(I32) else emptyList()
            }
            CanonicalAbiContext.Lower -> {
                val directParams =
                    if (flatParams.flatTypes.size > parameterLimit) listOf(pointer) else flatParams.flatTypes
                params = if (flatResults.flatTypes.isEmpty()) directParams else directParams + pointer
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

fun CanonicalAbiShape(
    types: List<ComponentValueType>,
    addressType: AddressType,
): CanonicalAbiShape? = CanonicalAbiFlattener(addressType).flatten(types) { type -> type }

fun CanonicalAbiShape(
    type: ComponentValueType,
    addressType: AddressType,
): CanonicalAbiShape? = CanonicalAbiFlattener(addressType).flatten(type)

fun FlattenComponentTypes(
    types: List<ComponentValueType>,
    addressType: AddressType,
): List<ValueType>? = CanonicalAbiShape(types, addressType)?.flatTypes

fun FlattenComponentType(
    type: ComponentValueType,
    addressType: AddressType,
): List<ValueType>? = CanonicalAbiShape(type, addressType)?.flatTypes

fun ComponentValueType.containsListOrString(): Boolean =
    CanonicalAbiShape(this, AddressType.I32)?.properties?.requiresAllocation == true

fun AddressType.coreValueType(): ValueType = when (this) {
    AddressType.I32 -> I32
    AddressType.I64 -> I64
}

private class CanonicalAbiFlattener(
    private val addressType: AddressType,
) {
    private val buffers = mutableListOf(FlatBuffer())
    private var depth = 0

    fun flatten(type: ComponentValueType): CanonicalAbiShape? {
        val buffer = buffers[0]
        buffer.clear()
        if (!flatten(type, buffer)) return null
        return buffer.result()
    }

    inline fun <T> flatten(
        types: Iterable<T>,
        type: (T) -> ComponentValueType,
    ): CanonicalAbiShape? {
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
            collectProperties(type, buffer.properties)
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
        is ComponentDefinedValueType.ListValue -> {
            buffer.add(addressType.coreValueType())
            buffer.add(addressType.coreValueType())
            buffer.properties.containsDynamicList = true
            collectProperties(type.element, buffer.properties)
            true
        }
        is ComponentDefinedValueType.Map -> {
            buffer.add(addressType.coreValueType())
            buffer.add(addressType.coreValueType())
            buffer.properties.deferredTypes += CanonicalAbiDeferredType.Map
            collectProperties(type.key, buffer.properties)
            collectProperties(type.value, buffer.properties)
            true
        }
        is ComponentDefinedValueType.FixedLengthList -> flattenFixedLengthList(type, buffer)
        is ComponentDefinedValueType.Tuple -> type.elements.all { element -> flatten(element, buffer) }
        is ComponentDefinedValueType.Flags,
        is ComponentDefinedValueType.Enum,
        -> {
            buffer.add(I32)
            true
        }
        is ComponentDefinedValueType.Own -> {
            buffer.add(I32)
            buffer.properties.containsResource = true
            true
        }
        is ComponentDefinedValueType.Borrow -> {
            buffer.add(I32)
            buffer.properties.containsResource = true
            buffer.properties.containsBorrow = true
            true
        }
        is ComponentDefinedValueType.Stream -> {
            buffer.add(I32)
            buffer.properties.deferredTypes += CanonicalAbiDeferredType.Stream
            true
        }
        is ComponentDefinedValueType.Future -> {
            buffer.add(I32)
            buffer.properties.deferredTypes += CanonicalAbiDeferredType.Future
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
        -> buffer.add(I32).let { true }
        ComponentPrimitiveType.ErrorContext -> {
            buffer.add(I32)
            buffer.properties.deferredTypes += CanonicalAbiDeferredType.ErrorContext
            true
        }
        ComponentPrimitiveType.S64,
        ComponentPrimitiveType.U64,
        -> buffer.add(I64).let { true }
        ComponentPrimitiveType.F32 -> buffer.add(F32).let { true }
        ComponentPrimitiveType.F64 -> buffer.add(F64).let { true }
        ComponentPrimitiveType.String -> {
            buffer.add(addressType.coreValueType())
            buffer.add(addressType.coreValueType())
            buffer.properties.containsString = true
            true
        }
    }

    private fun flattenFixedLengthList(
        type: ComponentDefinedValueType.FixedLengthList,
        buffer: FlatBuffer,
    ): Boolean {
        buffer.properties.deferredTypes += CanonicalAbiDeferredType.FixedLengthList
        val scratch = acquireBuffer()
        val flattened = flatten(type.element, scratch)
        releaseBuffer()
        if (!flattened) return false

        buffer.properties.include(scratch.properties)
        if (scratch.size == 0) return true
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

        buffer.properties.include(scratch.properties)
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

private fun collectProperties(
    type: ComponentValueType,
    properties: MutableCanonicalAbiProperties,
) {
    when (type) {
        is ComponentValueType.Primitive -> collectProperties(type.type, properties)
        is ComponentValueType.Defined -> {
            val value = type.definition.type as? ComponentDefinedType.Value ?: return
            collectProperties(value.type, properties)
        }
    }
}

private fun collectProperties(
    type: ComponentDefinedValueType,
    properties: MutableCanonicalAbiProperties,
) {
    when (type) {
        is ComponentDefinedValueType.Primitive -> collectProperties(type.type, properties)
        is ComponentDefinedValueType.Record ->
            type.fields.forEach { field -> collectProperties(field.type, properties) }
        is ComponentDefinedValueType.Variant ->
            type.cases.forEach { case -> case.type?.let { collectProperties(it, properties) } }
        is ComponentDefinedValueType.ListValue -> {
            properties.containsDynamicList = true
            collectProperties(type.element, properties)
        }
        is ComponentDefinedValueType.Map -> {
            properties.deferredTypes += CanonicalAbiDeferredType.Map
            collectProperties(type.key, properties)
            collectProperties(type.value, properties)
        }
        is ComponentDefinedValueType.FixedLengthList -> {
            properties.deferredTypes += CanonicalAbiDeferredType.FixedLengthList
            collectProperties(type.element, properties)
        }
        is ComponentDefinedValueType.Tuple ->
            type.elements.forEach { element -> collectProperties(element, properties) }
        is ComponentDefinedValueType.Option -> collectProperties(type.value, properties)
        is ComponentDefinedValueType.Result -> {
            type.ok?.let { collectProperties(it, properties) }
            type.error?.let { collectProperties(it, properties) }
        }
        is ComponentDefinedValueType.Own -> properties.containsResource = true
        is ComponentDefinedValueType.Borrow -> {
            properties.containsResource = true
            properties.containsBorrow = true
        }
        is ComponentDefinedValueType.Stream -> properties.deferredTypes += CanonicalAbiDeferredType.Stream
        is ComponentDefinedValueType.Future -> properties.deferredTypes += CanonicalAbiDeferredType.Future
        is ComponentDefinedValueType.Flags,
        is ComponentDefinedValueType.Enum,
        -> Unit
    }
}

private fun collectProperties(
    type: ComponentPrimitiveType,
    properties: MutableCanonicalAbiProperties,
) {
    when (type) {
        ComponentPrimitiveType.String -> properties.containsString = true
        ComponentPrimitiveType.ErrorContext ->
            properties.deferredTypes += CanonicalAbiDeferredType.ErrorContext
        else -> Unit
    }
}

private class FlatBuffer {
    private val values = arrayOfNulls<ValueType>(FLAT_BUFFER_CAPACITY)
    val properties = MutableCanonicalAbiProperties()
    var size: Int = 0
        private set

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
        properties.clear()
    }

    fun result(): CanonicalAbiShape = CanonicalAbiShape(
        flatTypes = List(size) { index -> values[index]!! },
        properties = properties.result(),
    )
}

private class MutableCanonicalAbiProperties {
    var containsString: Boolean = false
    var containsDynamicList: Boolean = false
    var containsResource: Boolean = false
    var containsBorrow: Boolean = false
    val deferredTypes = mutableSetOf<CanonicalAbiDeferredType>()

    fun include(other: MutableCanonicalAbiProperties) {
        containsString = containsString || other.containsString
        containsDynamicList = containsDynamicList || other.containsDynamicList
        containsResource = containsResource || other.containsResource
        containsBorrow = containsBorrow || other.containsBorrow
        deferredTypes += other.deferredTypes
    }

    fun clear() {
        containsString = false
        containsDynamicList = false
        containsResource = false
        containsBorrow = false
        deferredTypes.clear()
    }

    fun result(): CanonicalAbiProperties = CanonicalAbiProperties(
        containsString = containsString,
        containsDynamicList = containsDynamicList,
        containsResource = containsResource,
        containsBorrow = containsBorrow,
        deferredTypes = deferredTypes.toSet(),
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
