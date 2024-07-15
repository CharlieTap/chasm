package io.github.charlietap.chasm.decoder.wasm.decoder.section.data

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.section.DataSection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class DataSectionDecoderTest {

    @Test
    fun `can decode an encoded data section`() {

        val segment = DataSegment(Index.DataIndex(0u), ubyteArrayOf(), DataSegment.Mode.Passive)

        val dataSegmentDecoder: Decoder<DataSegment> = { _ ->
            fail("data segment decoder shouldn't be called directly")
        }
        val vectorDecoder: VectorDecoder<DataSegment> = { _, _ ->
            Ok(Vector(listOf(segment)))
        }
        val context = decoderContext()
        val actual = DataSectionDecoder(context, dataSegmentDecoder, vectorDecoder)

        val expected = Ok(DataSection(listOf(segment)))

        assertEquals(expected, actual)
    }
}
