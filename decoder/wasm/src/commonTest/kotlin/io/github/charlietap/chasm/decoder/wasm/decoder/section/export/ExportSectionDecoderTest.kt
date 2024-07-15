package io.github.charlietap.chasm.decoder.wasm.decoder.section.export

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.section.ExportSection
import kotlin.test.Test
import kotlin.test.assertEquals

class ExportSectionDecoderTest {

    @Test
    fun `can decode an export section`() {

        val exportDecoder: Decoder<Export> = { _ ->
            Err(SectionDecodeError.UnknownExportDescriptor(117u))
        }

        val vectorDecoder: VectorDecoder<Export> = { _, _ ->
            Ok(Vector(emptyList()))
        }

        val expected = Ok(ExportSection(emptyList()))

        val context = decoderContext()
        val actual = ExportSectionDecoder(
            context = context,
            vectorDecoder = vectorDecoder,
            exportDecoder = exportDecoder,
        )

        assertEquals(expected, actual)
    }
}
