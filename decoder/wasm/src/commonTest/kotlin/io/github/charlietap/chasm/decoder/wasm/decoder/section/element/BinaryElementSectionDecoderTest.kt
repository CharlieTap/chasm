package io.github.charlietap.chasm.decoder.wasm.decoder.section.element

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.ElementSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryElementSectionDecoderTest {

    @Test
    fun `can decode an encoded element section`() {

        val segment = ElementSegment(
            idx = Index.ElementIndex(0u),
            type = ReferenceType.RefNull(AbstractHeapType.Func),
            initExpressions = emptyList(),
            mode = ElementSegment.Mode.Passive,
        )

        val elementSegmentDecoder: ElementSegmentDecoder = { _, _ ->
            fail("data segment decoder shouldn't be called directly")
        }
        val vectorDecoder: VectorDecoder<ElementSegment> = { _, _ ->
            Ok(Vector(listOf(segment)))
        }
        val decoder = BinaryElementSectionDecoder(elementSegmentDecoder, vectorDecoder)

        val expected = Ok(ElementSection(listOf(segment)))
        val actual = decoder(FakeWasmBinaryReader(), SectionSize(0u))

        assertEquals(expected, actual)
    }
}
