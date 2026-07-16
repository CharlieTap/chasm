package io.github.charlietap.chasm.type.component.canonical

import io.github.charlietap.chasm.fixture.type.component.borrowComponentValueType
import io.github.charlietap.chasm.fixture.type.component.canonical.canonicalAbiProperties
import io.github.charlietap.chasm.fixture.type.component.canonical.canonicalAbiShape
import io.github.charlietap.chasm.fixture.type.component.canonical.canonicalAbiSignatureOptions
import io.github.charlietap.chasm.fixture.type.component.componentFunctionType
import io.github.charlietap.chasm.fixture.type.component.componentVariantCase
import io.github.charlietap.chasm.fixture.type.component.fixedLengthListComponentValueType
import io.github.charlietap.chasm.fixture.type.component.futureComponentValueType
import io.github.charlietap.chasm.fixture.type.component.labeledComponentValueType
import io.github.charlietap.chasm.fixture.type.component.listComponentValueType
import io.github.charlietap.chasm.fixture.type.component.mapComponentValueType
import io.github.charlietap.chasm.fixture.type.component.optionComponentValueType
import io.github.charlietap.chasm.fixture.type.component.ownComponentValueType
import io.github.charlietap.chasm.fixture.type.component.primitiveComponentValueType
import io.github.charlietap.chasm.fixture.type.component.recordComponentValueType
import io.github.charlietap.chasm.fixture.type.component.resultComponentValueType
import io.github.charlietap.chasm.fixture.type.component.streamComponentValueType
import io.github.charlietap.chasm.fixture.type.component.tupleComponentValueType
import io.github.charlietap.chasm.fixture.type.component.variantComponentValueType
import io.github.charlietap.chasm.fixture.type.f32ValueType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.component.ComponentPrimitiveType
import kotlin.test.Test
import kotlin.test.assertEquals

class CanonicalAbiTest {

    @Test
    fun `variant payload flat types are joined across cases`() {
        val type = variantComponentValueType(
            cases = listOf(
                componentVariantCase("float32", primitiveComponentValueType(ComponentPrimitiveType.F32)),
                componentVariantCase("int32", primitiveComponentValueType(ComponentPrimitiveType.S32)),
                componentVariantCase("float64", primitiveComponentValueType(ComponentPrimitiveType.F64)),
            ),
        )
        val expected = canonicalAbiShape(
            flatTypes = listOf(i32ValueType(), i64ValueType()),
        )

        val actual = CanonicalAbiShape(type, AddressType.I32)

        assertEquals(expected, actual)
    }

    @Test
    fun `shape retains one overflow type beyond the flat parameter limit`() {
        val atLimit = List(MAX_FLAT_PARAMS) { primitiveComponentValueType(ComponentPrimitiveType.S32) }
        val overLimit = List(MAX_FLAT_PARAMS + 8) { primitiveComponentValueType(ComponentPrimitiveType.S32) }
        val expectedAtLimit = canonicalAbiShape(
            flatTypes = List(MAX_FLAT_PARAMS) { i32ValueType() },
        )
        val expectedOverLimit = canonicalAbiShape(
            flatTypes = List(MAX_FLAT_PARAMS + 1) { i32ValueType() },
        )

        val actual = listOf(
            CanonicalAbiShape(atLimit, AddressType.I32),
            CanonicalAbiShape(overLimit, AddressType.I32),
        )
        val expected = listOf(expectedAtLimit, expectedOverLimit)

        assertEquals(expected, actual)
    }

    @Test
    fun `properties traverse nested inline component values`() {
        val type = recordComponentValueType(
            fields = listOf(
                labeledComponentValueType(
                    "value",
                    variantComponentValueType(
                        cases = listOf(
                            componentVariantCase(
                                "nested",
                                optionComponentValueType(
                                    tupleComponentValueType(
                                        elements = listOf(
                                            listComponentValueType(borrowComponentValueType()),
                                            resultComponentValueType(
                                                ok = primitiveComponentValueType(ComponentPrimitiveType.String),
                                                error = ownComponentValueType(),
                                            ),
                                        ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )
        val shape = requireNotNull(CanonicalAbiShape(type, AddressType.I32))
        val actual = PropertiesObservation(
            properties = shape.properties,
            requiresAllocation = shape.properties.requiresAllocation,
            requiresAllocationSensitiveHandling = shape.properties.requiresAllocationSensitiveHandling,
        )

        val expected = PropertiesObservation(
            properties = canonicalAbiProperties(
                containsString = true,
                containsDynamicList = true,
                containsResource = true,
                containsBorrow = true,
            ),
            requiresAllocation = true,
            requiresAllocationSensitiveHandling = true,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `deferred value kinds are marked distinctly`() {
        val type = tupleComponentValueType(
            elements = listOf(
                primitiveComponentValueType(ComponentPrimitiveType.ErrorContext),
                fixedLengthListComponentValueType(
                    element = primitiveComponentValueType(ComponentPrimitiveType.S32),
                    length = 2u,
                ),
                mapComponentValueType(
                    key = ComponentPrimitiveType.Bool,
                    value = primitiveComponentValueType(ComponentPrimitiveType.U32),
                ),
                streamComponentValueType(primitiveComponentValueType(ComponentPrimitiveType.String)),
                futureComponentValueType(listComponentValueType()),
            ),
        )
        val properties = requireNotNull(CanonicalAbiShape(type, AddressType.I32)).properties
        val actual = DeferredPropertiesObservation(
            deferredTypes = properties.deferredTypes,
            containsString = properties.containsString,
            containsDynamicList = properties.containsDynamicList,
            requiresAllocation = properties.requiresAllocation,
        )

        val expected = DeferredPropertiesObservation(
            deferredTypes = setOf(
                CanonicalAbiDeferredType.ErrorContext,
                CanonicalAbiDeferredType.FixedLengthList,
                CanonicalAbiDeferredType.Map,
                CanonicalAbiDeferredType.Stream,
                CanonicalAbiDeferredType.Future,
            ),
            containsString = false,
            containsDynamicList = false,
            requiresAllocation = true,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `legacy flattening APIs return the shape flat types`() {
        val types = listOf(
            primitiveComponentValueType(ComponentPrimitiveType.S64),
            variantComponentValueType(
                cases = listOf(
                    componentVariantCase("int32", primitiveComponentValueType(ComponentPrimitiveType.S32)),
                    componentVariantCase("float32", primitiveComponentValueType(ComponentPrimitiveType.F32)),
                ),
            ),
            listComponentValueType(ownComponentValueType()),
        )
        val actual = LegacyFlatteningObservation(
            flattenedTypes = FlattenComponentTypes(types, AddressType.I32),
            values = types.map { type ->
                val valueShape = requireNotNull(CanonicalAbiShape(type, AddressType.I32))
                LegacyValueObservation(
                    shape = valueShape.flatTypes,
                    flattened = FlattenComponentType(type, AddressType.I32),
                    shapeRequiresAllocation = valueShape.properties.requiresAllocation,
                    legacyRequiresAllocation = type.containsListOrString(),
                )
            },
        )

        val expected = LegacyFlatteningObservation(
            flattenedTypes = listOf(
                i64ValueType(),
                i32ValueType(),
                i32ValueType(),
                i32ValueType(),
                i32ValueType(),
            ),
            values = listOf(
                LegacyValueObservation(listOf(i64ValueType()), listOf(i64ValueType()), false, false),
                LegacyValueObservation(
                    listOf(i32ValueType(), i32ValueType()),
                    listOf(i32ValueType(), i32ValueType()),
                    false,
                    false,
                ),
                LegacyValueObservation(
                    listOf(i32ValueType(), i32ValueType()),
                    listOf(i32ValueType(), i32ValueType()),
                    true,
                    true,
                ),
            ),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `function lowering retains indirect parameter behavior`() {
        val type = componentFunctionType(
            params = List(MAX_FLAT_PARAMS + 1) { index ->
                labeledComponentValueType(
                    label = "param-$index",
                    type = primitiveComponentValueType(ComponentPrimitiveType.S32),
                )
            },
        )
        val options = canonicalAbiSignatureOptions()
        val expectedType = CanonicalCoreFunctionType(params = listOf(i32ValueType()))

        val lowering = requireNotNull(
            CanonicalFunctionTypeLowering(
                type = type,
                options = options,
                context = CanonicalAbiContext.Lower,
            ),
        )
        val actual = FunctionLoweringObservation(
            type = lowering.type,
            requiresMemory = lowering.requiresMemory,
            requiresRealloc = lowering.requiresRealloc,
            legacyType = CanonicalFunctionType(
                type = type,
                options = options,
                context = CanonicalAbiContext.Lower,
            ),
        )

        val expected = FunctionLoweringObservation(
            type = expectedType,
            requiresMemory = true,
            requiresRealloc = false,
            legacyType = expectedType,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `variant joining preserves identical float payload types`() {
        val type = variantComponentValueType(
            cases = listOf(
                componentVariantCase("first", primitiveComponentValueType(ComponentPrimitiveType.F32)),
                componentVariantCase("second", primitiveComponentValueType(ComponentPrimitiveType.F32)),
            ),
        )
        val expected = canonicalAbiShape(
            flatTypes = listOf(i32ValueType(), f32ValueType()),
        )

        assertEquals(expected, CanonicalAbiShape(type, AddressType.I32))
    }
}

private data class PropertiesObservation(
    val properties: CanonicalAbiProperties,
    val requiresAllocation: Boolean,
    val requiresAllocationSensitiveHandling: Boolean,
)

private data class DeferredPropertiesObservation(
    val deferredTypes: Set<CanonicalAbiDeferredType>,
    val containsString: Boolean,
    val containsDynamicList: Boolean,
    val requiresAllocation: Boolean,
)

private data class LegacyFlatteningObservation(
    val flattenedTypes: List<ValueType>?,
    val values: List<LegacyValueObservation>,
)

private data class LegacyValueObservation(
    val shape: List<ValueType>,
    val flattened: List<ValueType>?,
    val shapeRequiresAllocation: Boolean,
    val legacyRequiresAllocation: Boolean,
)

private data class FunctionLoweringObservation(
    val type: DefinedType,
    val requiresMemory: Boolean,
    val requiresRealloc: Boolean,
    val legacyType: DefinedType?,
)
