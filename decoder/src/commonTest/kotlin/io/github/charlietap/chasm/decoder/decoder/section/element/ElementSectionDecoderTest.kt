package io.github.charlietap.chasm.decoder.decoder.section.element

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.section.ElementSection
import io.github.charlietap.chasm.fixture.ast.module.elementIndex
import io.github.charlietap.chasm.fixture.ast.module.elementSegment
import io.github.charlietap.chasm.fixture.ast.type.refNullReferenceType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ElementSectionDecoderTest {

    @Test
    fun `can decode an encoded element section`() {

        val segment1 = elementSegment(
            idx = elementIndex(0u),
            type = refNullReferenceType(AbstractHeapType.Func),
            initExpressions = emptyList(),
            mode = ElementSegment.Mode.Passive,
        )

        val segment2 = elementSegment(
            idx = elementIndex(0u),
            type = refNullReferenceType(AbstractHeapType.Func),
            initExpressions = emptyList(),
            mode = ElementSegment.Mode.Passive,
        )

        val elementSegmentDecoder: Decoder<ElementSegment> = { _ ->
            fail("data segment decoder shouldn't be called directly")
        }
        val vectorDecoder: VectorDecoder<ElementSegment> = { _, _ ->
            Ok(Vector(listOf(segment1, segment2)))
        }
        val context = decoderContext()
        val actual = ElementSectionDecoder(context, elementSegmentDecoder, vectorDecoder)

        val expected = Ok(ElementSection(listOf(segment1, segment2.copy(idx = Index.ElementIndex(1u)))))

        assertEquals(expected, actual)
    }
}
