package io.github.charlietap.chasm.executor.instantiator.component.canonical

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.unwrap
import io.github.charlietap.chasm.fixture.runtime.component.canonical.borrowComponentValueType
import io.github.charlietap.chasm.fixture.runtime.component.canonical.canonicalLayoutProperties
import io.github.charlietap.chasm.fixture.runtime.component.canonical.enumComponentValueType
import io.github.charlietap.chasm.fixture.runtime.component.canonical.flagsComponentValueType
import io.github.charlietap.chasm.fixture.runtime.component.canonical.listComponentValueType
import io.github.charlietap.chasm.fixture.runtime.component.canonical.nullaryVariantComponentValueType
import io.github.charlietap.chasm.fixture.runtime.component.canonical.optionComponentValueType
import io.github.charlietap.chasm.fixture.runtime.component.canonical.ownComponentValueType
import io.github.charlietap.chasm.fixture.runtime.component.canonical.recordComponentValueType
import io.github.charlietap.chasm.fixture.runtime.component.canonical.resultComponentValueType
import io.github.charlietap.chasm.fixture.runtime.component.canonical.tupleComponentValueType
import io.github.charlietap.chasm.fixture.runtime.component.canonical.variantComponentValueType
import io.github.charlietap.chasm.fixture.runtime.component.error.unsupportedFeatureComponentPreparationError
import io.github.charlietap.chasm.fixture.runtime.component.index.runtimeResourceTypeIndex
import io.github.charlietap.chasm.fixture.type.component.componentVariantCase
import io.github.charlietap.chasm.fixture.type.component.fixedLengthListComponentValueType
import io.github.charlietap.chasm.fixture.type.component.futureComponentValueType
import io.github.charlietap.chasm.fixture.type.component.labeledComponentValueType
import io.github.charlietap.chasm.fixture.type.component.mapComponentValueType
import io.github.charlietap.chasm.fixture.type.component.primitiveComponentValueType
import io.github.charlietap.chasm.fixture.type.component.streamComponentValueType
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalLayoutKind
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalLayoutProperties
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLayout
import io.github.charlietap.chasm.runtime.component.error.UnsupportedComponentFeature
import io.github.charlietap.chasm.runtime.component.index.RuntimeResourceTypeIndex
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.component.ComponentPrimitiveType
import io.github.charlietap.chasm.type.component.ComponentResourceTypeId
import io.github.charlietap.chasm.type.component.ComponentValueType
import kotlin.test.Test
import kotlin.test.assertEquals

class Memory32LayoutCompilerTest {

    @Test
    fun `compiles every supported primitive with its memory32 width`() {
        val expectations = listOf(
            PrimitiveExpectation(ComponentPrimitiveType.Bool, CanonicalLayoutKind.Bool, 1u),
            PrimitiveExpectation(ComponentPrimitiveType.S8, CanonicalLayoutKind.S8, 1u),
            PrimitiveExpectation(ComponentPrimitiveType.U8, CanonicalLayoutKind.U8, 1u),
            PrimitiveExpectation(ComponentPrimitiveType.S16, CanonicalLayoutKind.S16, 2u),
            PrimitiveExpectation(ComponentPrimitiveType.U16, CanonicalLayoutKind.U16, 2u),
            PrimitiveExpectation(ComponentPrimitiveType.S32, CanonicalLayoutKind.S32, 4u),
            PrimitiveExpectation(ComponentPrimitiveType.U32, CanonicalLayoutKind.U32, 4u),
            PrimitiveExpectation(ComponentPrimitiveType.S64, CanonicalLayoutKind.S64, 8u),
            PrimitiveExpectation(ComponentPrimitiveType.U64, CanonicalLayoutKind.U64, 8u),
            PrimitiveExpectation(ComponentPrimitiveType.F32, CanonicalLayoutKind.F32, 4u),
            PrimitiveExpectation(ComponentPrimitiveType.F64, CanonicalLayoutKind.F64, 8u),
            PrimitiveExpectation(ComponentPrimitiveType.Char, CanonicalLayoutKind.Char, 4u),
            PrimitiveExpectation(ComponentPrimitiveType.String, CanonicalLayoutKind.String, 8u, 4u),
        )
        val subject = Memory32LayoutCompiler()

        val layouts = expectations.map { expectation ->
            subject.layout(primitiveComponentValueType(expectation.type)).primitiveObservation()
        }
        val actual = PrimitiveCompilationObservation(
            layouts = layouts,
            firstUsesBulkMemory = subject.layouts.first().properties.canUseBulkMemory,
            lastUsesBulkMemory = subject.layouts.last().properties.canUseBulkMemory,
        )

        val expected = PrimitiveCompilationObservation(
            layouts = expectations.map { expectation ->
                PrimitiveObservation(
                    kind = expectation.kind,
                    size32 = expectation.size32,
                    alignment32 = expectation.alignment32,
                )
            },
            firstUsesBulkMemory = false,
            lastUsesBulkMemory = false,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `computes record and tuple offsets including padding`() {
        val record = recordComponentValueType(
            id = 1u,
            fields = listOf(
                labeledComponentValueType("byte", primitiveComponentValueType(ComponentPrimitiveType.U8)),
                labeledComponentValueType("word", primitiveComponentValueType(ComponentPrimitiveType.U32)),
                labeledComponentValueType("half", primitiveComponentValueType(ComponentPrimitiveType.U16)),
            ),
        )
        val tuple = tupleComponentValueType(
            id = 2u,
            elements = listOf(
                primitiveComponentValueType(ComponentPrimitiveType.U16),
                primitiveComponentValueType(ComponentPrimitiveType.U64),
                primitiveComponentValueType(ComponentPrimitiveType.U8),
            ),
        )
        val subject = Memory32LayoutCompiler()

        val actual = listOf(
            subject.layout(record).layoutObservation(),
            subject.layout(tuple).layoutObservation(),
        )

        val expected = listOf(
            LayoutObservation(CanonicalLayoutKind.Record, 12u, 4u, listOf(0u, 4u, 8u), false),
            LayoutObservation(CanonicalLayoutKind.Tuple, 24u, 8u, listOf(0u, 8u, 16u), false),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `computes variant discriminants payloads options and results`() {
        val variant = variantComponentValueType(
            id = 10u,
            cases = listOf(
                componentVariantCase("none"),
                componentVariantCase("small", primitiveComponentValueType(ComponentPrimitiveType.U16)),
                componentVariantCase("large", primitiveComponentValueType(ComponentPrimitiveType.U64)),
            ),
        )
        val mediumVariant = nullaryVariantComponentValueType(id = 11u, cases = 257)
        val largeVariant = nullaryVariantComponentValueType(id = 12u, cases = 65_537)
        val option = optionComponentValueType(
            id = 13u,
            value = primitiveComponentValueType(ComponentPrimitiveType.U64),
        )
        val result = resultComponentValueType(
            id = 14u,
            ok = primitiveComponentValueType(ComponentPrimitiveType.U16),
            error = primitiveComponentValueType(ComponentPrimitiveType.String),
        )
        val subject = Memory32LayoutCompiler()

        val variantLayout = subject.layout(variant)
        val optionLayout = subject.layout(option)
        val resultLayout = subject.layout(result)
        val actual = VariantFamilyObservation(
            discriminantWidths = listOf(
                variantLayout.discriminantSize32,
                subject.layout(mediumVariant).discriminantSize32,
                subject.layout(largeVariant).discriminantSize32,
            ),
            layouts = listOf(
                variantLayout.variantObservation(),
                optionLayout.variantObservation(),
                resultLayout.variantObservation(),
            ),
            resultProperties = resultLayout.properties,
        )

        val expected = VariantFamilyObservation(
            discriminantWidths = listOf(1u, 2u, 4u),
            layouts = listOf(
                VariantObservation(16u, 8u, 1u, 8u, listOf(-1, 0, 1)),
                VariantObservation(16u, 8u, 1u, 8u, listOf(-1, 1)),
                VariantObservation(12u, 4u, 1u, 4u, listOf(0, 4)),
            ),
            resultProperties = canonicalLayoutProperties(
                containsString = true,
                liftMayAllocate = true,
                lowerMayAllocate = true,
            ),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `uses the official packed widths for flags`() {
        val counts = listOf(1, 8, 9, 16, 17, 32)
        val subject = Memory32LayoutCompiler()

        val actual = counts.mapIndexed { index, count ->
            subject.layout(
                flagsComponentValueType(
                    id = (20 + index).toUInt(),
                    labels = List(count) { label -> "flag-$label" },
                ),
            ).flagsObservation()
        }

        val expected = listOf(
            FlagsObservation(1u, 1u, 1u),
            FlagsObservation(1u, 1u, 1u),
            FlagsObservation(2u, 2u, 1u),
            FlagsObservation(2u, 2u, 1u),
            FlagsObservation(4u, 4u, 1u),
            FlagsObservation(4u, 4u, 1u),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `uses one two and four byte discriminants for enums`() {
        val counts = listOf(1, 257, 65_537)
        val subject = Memory32LayoutCompiler()

        val actual = counts.mapIndexed { index, count ->
            subject.layout(
                enumComponentValueType(
                    id = (30 + index).toUInt(),
                    labels = List(count) { label -> "case-$label" },
                ),
            ).discriminantObservation()
        }

        val expected = listOf(
            DiscriminantObservation(1u, 1u, 1u),
            DiscriminantObservation(2u, 2u, 2u),
            DiscriminantObservation(4u, 4u, 4u),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `uses four byte layouts for owned and borrowed resources`() {
        val own = ownComponentValueType(id = 40u)
        val borrow = borrowComponentValueType(id = 41u)
        val subject = Memory32LayoutCompiler()
        val resourceType: (ComponentResourceTypeId) -> RuntimeResourceTypeIndex? = {
            runtimeResourceTypeIndex()
        }

        val actual = listOf(
            subject.layout(own, resourceType).resourceObservation(),
            subject.layout(borrow, resourceType).resourceObservation(),
        )

        val expected = listOf(
            ResourceLayoutObservation(CanonicalLayoutKind.Own, 4u, 4u),
            ResourceLayoutObservation(CanonicalLayoutKind.Borrow, 4u, 4u),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `propagates string list and resource movement properties`() {
        val borrow = borrowComponentValueType(
            id = 50u,
            typeId = 300u,
            resourceId = 1u,
        )
        val list = listComponentValueType(id = 51u, element = borrow)
        val own = ownComponentValueType(
            id = 52u,
            typeId = 301u,
            resourceId = 1u,
        )
        val record = recordComponentValueType(
            id = 53u,
            fields = listOf(
                labeledComponentValueType("name", primitiveComponentValueType(ComponentPrimitiveType.String)),
                labeledComponentValueType("items", list),
                labeledComponentValueType("owner", own),
            ),
        )
        val subject = Memory32LayoutCompiler()
        val resourceType: (ComponentResourceTypeId) -> RuntimeResourceTypeIndex? = {
            runtimeResourceTypeIndex()
        }

        val actual = subject.layout(record, resourceType).properties

        val expected = canonicalLayoutProperties(
            containsString = true,
            containsList = true,
            containsResource = true,
            containsBorrow = true,
            liftMayAllocate = true,
            lowerMayAllocate = true,
            canUseBulkMemory = false,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `memoizes primitives and defined values into dense indexes`() {
        val primitive = primitiveComponentValueType(ComponentPrimitiveType.Bool)
        val firstDefined = recordComponentValueType(
            id = 60u,
            fields = listOf(labeledComponentValueType("value", primitive)),
        )
        val equivalentDefinition = recordComponentValueType(
            id = 60u,
            fields = listOf(labeledComponentValueType("again", primitive)),
        )
        val subject = Memory32LayoutCompiler()

        val actual = MemoizationObservation(
            primitiveIndexes = listOf(subject.compile(primitive).unwrap(), subject.compile(primitive).unwrap())
                .map { index -> index.index },
            definedIndexes = listOf(
                subject.compile(firstDefined).unwrap(),
                subject.compile(equivalentDefinition).unwrap(),
            ).map { index -> index.index },
            layoutIndexes = subject.layouts.indices.toList(),
        )

        val expected = MemoizationObservation(
            primitiveIndexes = listOf(0, 0),
            definedIndexes = listOf(1, 1),
            layoutIndexes = listOf(0, 1),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `rejects deferred and memory64 layouts without placeholders`() {
        val cases = listOf(
            LayoutFailureCase(
                primitiveComponentValueType(ComponentPrimitiveType.ErrorContext),
                UnsupportedComponentFeature.ErrorContext,
            ),
            LayoutFailureCase(
                fixedLengthListComponentValueType(length = 2u),
                UnsupportedComponentFeature.FixedLengthList,
            ),
            LayoutFailureCase(mapComponentValueType(), UnsupportedComponentFeature.Map),
            LayoutFailureCase(streamComponentValueType(), UnsupportedComponentFeature.Stream),
            LayoutFailureCase(futureComponentValueType(), UnsupportedComponentFeature.Future),
            LayoutFailureCase(
                primitiveComponentValueType(),
                UnsupportedComponentFeature.Memory64,
                AddressType.I64,
            ),
        )

        val actual = cases.map { case ->
            val subject = Memory32LayoutCompiler(case.addressType)
            LayoutFailureObservation(
                result = subject.compile(case.type),
                layoutsEmpty = subject.layouts.isEmpty(),
            )
        }

        val expected = cases.map { case ->
            LayoutFailureObservation(
                result = Err(unsupportedFeatureComponentPreparationError(case.feature)),
                layoutsEmpty = true,
            )
        }
        assertEquals(expected, actual)
    }

    private fun Memory32LayoutCompiler.layout(
        type: ComponentValueType,
        resourceType: (ComponentResourceTypeId) -> RuntimeResourceTypeIndex? = { null },
    ): LinearMemoryLayout {
        val index = compile(type, resourceType).unwrap()
        return layouts[index.index]
    }
}

private fun LinearMemoryLayout.primitiveObservation() = PrimitiveObservation(kind, size32, alignment32)

private fun LinearMemoryLayout.layoutObservation() = LayoutObservation(
    kind = kind,
    size32 = size32,
    alignment32 = alignment32,
    offsets32 = offsets32.toList(),
    canUseBulkMemory = properties.canUseBulkMemory,
)

private fun LinearMemoryLayout.variantObservation() = VariantObservation(
    size32 = size32,
    alignment32 = alignment32,
    discriminantSize32 = discriminantSize32,
    payloadOffset32 = payloadOffset32,
    children = children.toList(),
)

private fun LinearMemoryLayout.flagsObservation() = FlagsObservation(size32, alignment32, flagsWords)

private fun LinearMemoryLayout.discriminantObservation() = DiscriminantObservation(
    size32 = size32,
    alignment32 = alignment32,
    discriminantSize32 = discriminantSize32,
)

private fun LinearMemoryLayout.resourceObservation() = ResourceLayoutObservation(kind, size32, alignment32)

private data class PrimitiveExpectation(
    val type: ComponentPrimitiveType,
    val kind: CanonicalLayoutKind,
    val size32: UInt,
    val alignment32: UInt = size32,
)

private data class PrimitiveCompilationObservation(
    val layouts: List<PrimitiveObservation>,
    val firstUsesBulkMemory: Boolean,
    val lastUsesBulkMemory: Boolean,
)

private data class PrimitiveObservation(
    val kind: CanonicalLayoutKind,
    val size32: UInt,
    val alignment32: UInt,
)

private data class LayoutObservation(
    val kind: CanonicalLayoutKind,
    val size32: UInt,
    val alignment32: UInt,
    val offsets32: List<UInt>,
    val canUseBulkMemory: Boolean,
)

private data class VariantFamilyObservation(
    val discriminantWidths: List<UInt>,
    val layouts: List<VariantObservation>,
    val resultProperties: CanonicalLayoutProperties,
)

private data class VariantObservation(
    val size32: UInt,
    val alignment32: UInt,
    val discriminantSize32: UInt,
    val payloadOffset32: UInt,
    val children: List<Int>,
)

private data class FlagsObservation(
    val size32: UInt,
    val alignment32: UInt,
    val flagsWords: UInt,
)

private data class DiscriminantObservation(
    val size32: UInt,
    val alignment32: UInt,
    val discriminantSize32: UInt,
)

private data class ResourceLayoutObservation(
    val kind: CanonicalLayoutKind,
    val size32: UInt,
    val alignment32: UInt,
)

private data class MemoizationObservation(
    val primitiveIndexes: List<Int>,
    val definedIndexes: List<Int>,
    val layoutIndexes: List<Int>,
)

private data class LayoutFailureCase(
    val type: ComponentValueType,
    val feature: UnsupportedComponentFeature,
    val addressType: AddressType = AddressType.I32,
)

private data class LayoutFailureObservation(
    val result: Any,
    val layoutsEmpty: Boolean,
)
