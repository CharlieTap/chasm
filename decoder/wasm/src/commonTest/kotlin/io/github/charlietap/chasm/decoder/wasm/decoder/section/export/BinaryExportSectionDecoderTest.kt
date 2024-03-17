package io.github.charlietap.chasm.decoder.wasm.decoder.section.export

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.ExportSection
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryExportSectionDecoderTest {

    @Test
    fun `can decode an export section`() {

        val subDecoder: ExportDecoder = { _ ->
            Err(SectionDecodeError.UnknownExportDescriptor(117u))
        }

        val vectorDecoder: VectorDecoder<Export> = { _, sub ->
            assertEquals(subDecoder, sub)
            Ok(Vector(emptyList()))
        }

        val expected = Ok(ExportSection(emptyList()))

        val decoder = BinaryExportSectionDecoder(
            vectorDecoder = vectorDecoder,
            importDecoder = subDecoder,
        )

        val actual = decoder(FakeWasmBinaryReader(), SectionSize(0u))

        assertEquals(expected, actual)
    }
}
