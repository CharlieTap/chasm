package io.github.charlietap.chasm.decoder.wasm.decoder.section.data

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.DataSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BinaryDataSectionDecoderTest {

    @Test
    fun `can decode an encoded data section`() {

        val segment = DataSegment(Index.DataIndex(0u), ubyteArrayOf(), DataSegment.Mode.Passive)

        val dataSegmentDecoder: DataSegmentDecoder = { _, _ ->
            fail("data segment decoder shouldn't be called directly")
        }
        val vectorDecoder: VectorDecoder<DataSegment> = { _, _ ->
            Ok(Vector(listOf(segment)))
        }
        val decoder = BinaryDataSectionDecoder(dataSegmentDecoder, vectorDecoder)

        val expected = Ok(DataSection(listOf(segment)))
        val actual = decoder(FakeWasmBinaryReader(), SectionSize(0u))

        assertEquals(expected, actual)
    }
}
