package io.github.charlietap.chasm.executor.instantiator.component.canonical

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalLayoutKind
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalLayoutProperties
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalValueTupleLayout
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLayout
import io.github.charlietap.chasm.runtime.component.error.ComponentPreparationError
import io.github.charlietap.chasm.runtime.component.error.UnsupportedComponentFeature
import io.github.charlietap.chasm.runtime.component.index.LinearMemoryLayoutIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimeResourceTypeIndex
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.component.ComponentDefinedType
import io.github.charlietap.chasm.type.component.ComponentDefinedValueType
import io.github.charlietap.chasm.type.component.ComponentPrimitiveType
import io.github.charlietap.chasm.type.component.ComponentResourceTypeId
import io.github.charlietap.chasm.type.component.ComponentTypeId
import io.github.charlietap.chasm.type.component.ComponentValueType
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiDeferredType
import io.github.charlietap.chasm.type.component.canonical.CanonicalAbiShape

class Memory32LayoutCompiler(
    val addressType: AddressType = AddressType.I32,
) {
    private val compiledLayouts = mutableListOf<LinearMemoryLayout>()
    private val primitiveLayouts = mutableMapOf<ComponentPrimitiveType, LinearMemoryLayoutIndex>()
    private val definedLayouts = mutableMapOf<ComponentTypeId, LinearMemoryLayoutIndex>()

    val layouts: List<LinearMemoryLayout>
        get() = compiledLayouts.toList()

    operator fun get(index: Int): LinearMemoryLayout = compiledLayouts[index]

    fun compile(
        type: ComponentValueType,
        resourceType: (ComponentResourceTypeId) -> RuntimeResourceTypeIndex? = { null },
    ): Result<LinearMemoryLayoutIndex, ComponentPreparationError> {
        if (addressType == AddressType.I64) return unsupported(UnsupportedComponentFeature.Memory64)
        cached(type)?.let { index -> return Ok(index) }

        return binding {
            val shape = CanonicalAbiShape(type, AddressType.I32)
                ?: unavailable<CanonicalAbiShape>("canonical value type has no memory32 shape").bind()
            rejectDeferredTypes(shape).bind()

            val layout = compile(type, shape, resourceType).bind()
            val index = LinearMemoryLayoutIndex(compiledLayouts.size)
            compiledLayouts += layout
            cache(type, index)
            index
        }
    }

    fun tuple(layouts: IntArray): CanonicalValueTupleLayout {
        if (layouts.isEmpty()) return CanonicalValueTupleLayout.Empty
        val offsets = UIntArray(layouts.size)
        var size = 0u
        var alignment = 1u
        var flatCount = 0
        layouts.forEachIndexed { index, layoutIndex ->
            val layout = compiledLayouts[layoutIndex]
            size = alignTo(size, layout.alignment32)
            offsets[index] = size
            size += layout.size32
            alignment = maxOf(alignment, layout.alignment32)
            flatCount += layout.shape.flatTypes.size
        }
        return CanonicalValueTupleLayout(
            layouts = layouts,
            offsets32 = offsets,
            size32 = alignTo(size, alignment),
            alignment32 = alignment,
            flatCount = flatCount,
        )
    }

    private fun cached(type: ComponentValueType): LinearMemoryLayoutIndex? = when (type) {
        is ComponentValueType.Primitive -> primitiveLayouts[type.type]
        is ComponentValueType.Defined -> definedLayouts[type.definition.id]
    }

    private fun cache(
        type: ComponentValueType,
        index: LinearMemoryLayoutIndex,
    ) {
        when (type) {
            is ComponentValueType.Primitive -> primitiveLayouts[type.type] = index
            is ComponentValueType.Defined -> definedLayouts[type.definition.id] = index
        }
    }

    private fun compile(
        type: ComponentValueType,
        shape: io.github.charlietap.chasm.type.component.canonical.CanonicalAbiShape,
        resourceType: (ComponentResourceTypeId) -> RuntimeResourceTypeIndex?,
    ): Result<LinearMemoryLayout, ComponentPreparationError> = when (type) {
        is ComponentValueType.Primitive -> compilePrimitive(type.type, shape)
        is ComponentValueType.Defined -> {
            val defined = type.definition.type as? ComponentDefinedType.Value
                ?: return unavailable("component type ${type.definition.id.value} is not a value type")
            compileDefined(defined.type, shape, resourceType)
        }
    }

    private fun compilePrimitive(
        type: ComponentPrimitiveType,
        shape: io.github.charlietap.chasm.type.component.canonical.CanonicalAbiShape,
    ): Result<LinearMemoryLayout, ComponentPreparationError> = when (type) {
        ComponentPrimitiveType.Bool -> scalar(CanonicalLayoutKind.Bool, shape, 1u, canUseBulkMemory = false)
        ComponentPrimitiveType.S8 -> scalar(CanonicalLayoutKind.S8, shape, 1u)
        ComponentPrimitiveType.U8 -> scalar(CanonicalLayoutKind.U8, shape, 1u)
        ComponentPrimitiveType.S16 -> scalar(CanonicalLayoutKind.S16, shape, 2u)
        ComponentPrimitiveType.U16 -> scalar(CanonicalLayoutKind.U16, shape, 2u)
        ComponentPrimitiveType.S32 -> scalar(CanonicalLayoutKind.S32, shape, 4u)
        ComponentPrimitiveType.U32 -> scalar(CanonicalLayoutKind.U32, shape, 4u)
        ComponentPrimitiveType.S64 -> scalar(CanonicalLayoutKind.S64, shape, 8u)
        ComponentPrimitiveType.U64 -> scalar(CanonicalLayoutKind.U64, shape, 8u)
        ComponentPrimitiveType.F32 -> scalar(CanonicalLayoutKind.F32, shape, 4u)
        ComponentPrimitiveType.F64 -> scalar(CanonicalLayoutKind.F64, shape, 8u)
        ComponentPrimitiveType.Char -> scalar(CanonicalLayoutKind.Char, shape, 4u, canUseBulkMemory = false)
        ComponentPrimitiveType.String -> Ok(
            LinearMemoryLayout(
                kind = CanonicalLayoutKind.String,
                shape = shape,
                size32 = MEMORY32_PAIR_SIZE,
                alignment32 = MEMORY32_POINTER_SIZE,
                properties = CanonicalLayoutProperties(
                    containsString = true,
                    liftMayAllocate = true,
                    lowerMayAllocate = true,
                ),
            ),
        )
        ComponentPrimitiveType.ErrorContext -> unsupported(UnsupportedComponentFeature.ErrorContext)
    }

    private fun scalar(
        kind: CanonicalLayoutKind,
        shape: io.github.charlietap.chasm.type.component.canonical.CanonicalAbiShape,
        width: UInt,
        canUseBulkMemory: Boolean = true,
    ): Result<LinearMemoryLayout, ComponentPreparationError> = Ok(
        LinearMemoryLayout(
            kind = kind,
            shape = shape,
            size32 = width,
            alignment32 = width,
            properties = CanonicalLayoutProperties(canUseBulkMemory = canUseBulkMemory),
        ),
    )

    private fun compileDefined(
        type: ComponentDefinedValueType,
        shape: io.github.charlietap.chasm.type.component.canonical.CanonicalAbiShape,
        resourceType: (ComponentResourceTypeId) -> RuntimeResourceTypeIndex?,
    ): Result<LinearMemoryLayout, ComponentPreparationError> = when (type) {
        is ComponentDefinedValueType.Primitive -> compilePrimitive(type.type, shape)
        is ComponentDefinedValueType.Record -> compileRecord(
            kind = CanonicalLayoutKind.Record,
            shape = shape,
            fields = type.fields.map { field -> field.type },
            resourceType = resourceType,
        )
        is ComponentDefinedValueType.Variant -> compileVariant(
            kind = CanonicalLayoutKind.Variant,
            shape = shape,
            cases = type.cases.map { case -> case.type },
            resourceType = resourceType,
        )
        is ComponentDefinedValueType.ListValue -> compileList(type.element, shape, resourceType)
        is ComponentDefinedValueType.Tuple -> compileRecord(
            kind = CanonicalLayoutKind.Tuple,
            shape = shape,
            fields = type.elements,
            resourceType = resourceType,
        )
        is ComponentDefinedValueType.Flags -> compileFlags(type.labels.size, shape)
        is ComponentDefinedValueType.Enum -> compileEnum(type.labels.size, shape)
        is ComponentDefinedValueType.Option -> compileVariant(
            kind = CanonicalLayoutKind.Option,
            shape = shape,
            cases = listOf(null, type.value),
            resourceType = resourceType,
        )
        is ComponentDefinedValueType.Result -> compileVariant(
            kind = CanonicalLayoutKind.Result,
            shape = shape,
            cases = listOf(type.ok, type.error),
            resourceType = resourceType,
        )
        is ComponentDefinedValueType.Own -> resource(
            kind = CanonicalLayoutKind.Own,
            shape = shape,
            resourceType = resourceType(type.resource),
            containsBorrow = false,
        )
        is ComponentDefinedValueType.Borrow -> resource(
            kind = CanonicalLayoutKind.Borrow,
            shape = shape,
            resourceType = resourceType(type.resource),
            containsBorrow = true,
        )
        is ComponentDefinedValueType.FixedLengthList -> unsupported(UnsupportedComponentFeature.FixedLengthList)
        is ComponentDefinedValueType.Map -> unsupported(UnsupportedComponentFeature.Map)
        is ComponentDefinedValueType.Stream -> unsupported(UnsupportedComponentFeature.Stream)
        is ComponentDefinedValueType.Future -> unsupported(UnsupportedComponentFeature.Future)
    }

    private fun compileRecord(
        kind: CanonicalLayoutKind,
        shape: io.github.charlietap.chasm.type.component.canonical.CanonicalAbiShape,
        fields: List<ComponentValueType>,
        resourceType: (ComponentResourceTypeId) -> RuntimeResourceTypeIndex?,
    ): Result<LinearMemoryLayout, ComponentPreparationError> = binding {
        if (fields.isEmpty()) unavailable<Unit>("canonical records and tuples must not be empty").bind()

        val children = compileChildren(fields, resourceType).bind()
        val childLayouts = children.map { index -> layout(LinearMemoryLayoutIndex(index)) }
        val offsets = UIntArray(childLayouts.size)
        var alignment = 1u
        var size = 0u
        var canUseBulkMemory = true

        childLayouts.forEachIndexed { index, child ->
            val offset = alignTo(size, child.alignment32)
            offsets[index] = offset
            canUseBulkMemory = canUseBulkMemory && child.properties.canUseBulkMemory && offset == size
            size = offset + child.size32
            alignment = maxOf(alignment, child.alignment32)
        }

        val alignedSize = alignTo(size, alignment)
        canUseBulkMemory = canUseBulkMemory && alignedSize == size
        LinearMemoryLayout(
            kind = kind,
            shape = shape,
            size32 = alignedSize,
            alignment32 = alignment,
            children = children,
            offsets32 = offsets,
            properties = aggregateProperties(childLayouts, canUseBulkMemory = canUseBulkMemory),
        )
    }

    private fun compileVariant(
        kind: CanonicalLayoutKind,
        shape: io.github.charlietap.chasm.type.component.canonical.CanonicalAbiShape,
        cases: List<ComponentValueType?>,
        resourceType: (ComponentResourceTypeId) -> RuntimeResourceTypeIndex?,
    ): Result<LinearMemoryLayout, ComponentPreparationError> = binding {
        val discriminantSize = discriminantSize(cases.size).bind()
        val children = compileOptionalChildren(cases, resourceType).bind()
        val childLayouts = children
            .filter { index -> index != ABSENT_LAYOUT_INDEX }
            .map { index -> layout(LinearMemoryLayoutIndex(index)) }
        val payloadAlignment = childLayouts.maxOfOrNull { child -> child.alignment32 } ?: 1u
        val payloadSize = childLayouts.maxOfOrNull { child -> child.size32 } ?: 0u
        val alignment = maxOf(discriminantSize, payloadAlignment)
        val payloadOffset = alignTo(discriminantSize, payloadAlignment)

        LinearMemoryLayout(
            kind = kind,
            shape = shape,
            size32 = alignTo(payloadOffset + payloadSize, alignment),
            alignment32 = alignment,
            discriminantSize32 = discriminantSize,
            payloadOffset32 = payloadOffset,
            elementCount = cases.size,
            children = children,
            properties = aggregateProperties(childLayouts),
        )
    }

    private fun compileList(
        element: ComponentValueType,
        shape: io.github.charlietap.chasm.type.component.canonical.CanonicalAbiShape,
        resourceType: (ComponentResourceTypeId) -> RuntimeResourceTypeIndex?,
    ): Result<LinearMemoryLayout, ComponentPreparationError> = binding {
        val child = compile(element, resourceType).bind()
        val childLayout = layout(child)
        LinearMemoryLayout(
            kind = CanonicalLayoutKind.List,
            shape = shape,
            size32 = MEMORY32_PAIR_SIZE,
            alignment32 = MEMORY32_POINTER_SIZE,
            children = intArrayOf(child.index),
            properties = aggregateProperties(
                children = listOf(childLayout),
                containsList = true,
                liftMayAllocate = true,
                lowerMayAllocate = true,
            ),
        )
    }

    private fun compileFlags(
        count: Int,
        shape: io.github.charlietap.chasm.type.component.canonical.CanonicalAbiShape,
    ): Result<LinearMemoryLayout, ComponentPreparationError> {
        if (count !in 1..MAX_P2_FLAGS) return unavailable("canonical flags must contain between 1 and 32 labels")
        val width = when (count) {
            in 1..8 -> 1u
            in 9..16 -> 2u
            else -> 4u
        }
        return Ok(
            LinearMemoryLayout(
                kind = CanonicalLayoutKind.Flags,
                shape = shape,
                size32 = width,
                alignment32 = width,
                flagsWords = ((count + FLAGS_PER_WORD - 1) / FLAGS_PER_WORD).toUInt(),
                elementCount = count,
                properties = CanonicalLayoutProperties(canUseBulkMemory = false),
            ),
        )
    }

    private fun compileEnum(
        count: Int,
        shape: io.github.charlietap.chasm.type.component.canonical.CanonicalAbiShape,
    ): Result<LinearMemoryLayout, ComponentPreparationError> = binding {
        val discriminantSize = discriminantSize(count).bind()
        LinearMemoryLayout(
            kind = CanonicalLayoutKind.Enum,
            shape = shape,
            size32 = discriminantSize,
            alignment32 = discriminantSize,
            discriminantSize32 = discriminantSize,
            elementCount = count,
            properties = CanonicalLayoutProperties(canUseBulkMemory = false),
        )
    }

    private fun resource(
        kind: CanonicalLayoutKind,
        shape: io.github.charlietap.chasm.type.component.canonical.CanonicalAbiShape,
        resourceType: RuntimeResourceTypeIndex?,
        containsBorrow: Boolean,
    ): Result<LinearMemoryLayout, ComponentPreparationError> {
        if (resourceType == null) return unavailable("canonical resource type has no runtime identity")
        return Ok(
            LinearMemoryLayout(
                kind = kind,
                shape = shape,
                size32 = RESOURCE_HANDLE_SIZE,
                alignment32 = RESOURCE_HANDLE_SIZE,
                resourceType = resourceType,
                properties = CanonicalLayoutProperties(
                    containsResource = true,
                    containsBorrow = containsBorrow,
                    liftMayAllocate = containsBorrow,
                    lowerMayAllocate = true,
                ),
            ),
        )
    }

    private fun compileChildren(
        types: List<ComponentValueType>,
        resourceType: (ComponentResourceTypeId) -> RuntimeResourceTypeIndex?,
    ): Result<IntArray, ComponentPreparationError> = binding {
        IntArray(types.size) { index -> compile(types[index], resourceType).bind().index }
    }

    private fun compileOptionalChildren(
        types: List<ComponentValueType?>,
        resourceType: (ComponentResourceTypeId) -> RuntimeResourceTypeIndex?,
    ): Result<IntArray, ComponentPreparationError> = binding {
        IntArray(types.size) { index ->
            types[index]?.let { type -> compile(type, resourceType).bind().index } ?: ABSENT_LAYOUT_INDEX
        }
    }

    private fun discriminantSize(caseCount: Int): Result<UInt, ComponentPreparationError> = when (caseCount) {
        0 -> unavailable("canonical variants and enums must not be empty")
        in 1..U8_DISCRIMINANT_CASES -> Ok(1u)
        in (U8_DISCRIMINANT_CASES + 1)..U16_DISCRIMINANT_CASES -> Ok(2u)
        else -> Ok(4u)
    }

    private fun rejectDeferredTypes(
        shape: io.github.charlietap.chasm.type.component.canonical.CanonicalAbiShape,
    ): Result<Unit, ComponentPreparationError> {
        val deferred = shape.properties.deferredTypes
        val feature = when {
            CanonicalAbiDeferredType.ErrorContext in deferred -> UnsupportedComponentFeature.ErrorContext
            CanonicalAbiDeferredType.FixedLengthList in deferred -> UnsupportedComponentFeature.FixedLengthList
            CanonicalAbiDeferredType.Map in deferred -> UnsupportedComponentFeature.Map
            CanonicalAbiDeferredType.Stream in deferred -> UnsupportedComponentFeature.Stream
            CanonicalAbiDeferredType.Future in deferred -> UnsupportedComponentFeature.Future
            else -> return Ok(Unit)
        }
        return unsupported(feature)
    }

    private fun layout(index: LinearMemoryLayoutIndex): LinearMemoryLayout = compiledLayouts[index.index]
}

private fun aggregateProperties(
    children: List<LinearMemoryLayout>,
    containsList: Boolean = false,
    liftMayAllocate: Boolean = false,
    lowerMayAllocate: Boolean = false,
    canUseBulkMemory: Boolean = false,
) = CanonicalLayoutProperties(
    containsString = children.any { child -> child.properties.containsString },
    containsList = containsList || children.any { child -> child.properties.containsList },
    containsResource = children.any { child -> child.properties.containsResource },
    containsBorrow = children.any { child -> child.properties.containsBorrow },
    liftMayAllocate = liftMayAllocate || children.any { child -> child.properties.liftMayAllocate },
    lowerMayAllocate = lowerMayAllocate || children.any { child -> child.properties.lowerMayAllocate },
    canUseBulkMemory = canUseBulkMemory,
)

private fun alignTo(
    value: UInt,
    alignment: UInt,
): UInt {
    val alignment64 = alignment.toULong()
    return (((value.toULong() + alignment64 - 1uL) / alignment64) * alignment64).toUInt()
}

private fun <T> unsupported(
    feature: UnsupportedComponentFeature,
): Result<T, ComponentPreparationError> = Err(ComponentPreparationError.UnsupportedFeature(feature))

private fun <T> unavailable(reason: String): Result<T, ComponentPreparationError> =
    Err(ComponentPreparationError.CanonicalLayoutUnavailable(reason))

private const val ABSENT_LAYOUT_INDEX = -1
private const val FLAGS_PER_WORD = 32
private const val MAX_P2_FLAGS = 32
private const val U8_DISCRIMINANT_CASES = 1 shl 8
private const val U16_DISCRIMINANT_CASES = 1 shl 16
private const val MEMORY32_POINTER_SIZE = 4u
private const val MEMORY32_PAIR_SIZE = 8u
private const val RESOURCE_HANDLE_SIZE = 4u
