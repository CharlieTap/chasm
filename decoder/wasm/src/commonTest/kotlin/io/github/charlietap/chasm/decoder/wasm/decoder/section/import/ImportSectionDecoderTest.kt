package io.github.charlietap.chasm.decoder.wasm.decoder.section.import

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.wasm.fixture.decoderContext
import io.github.charlietap.chasm.decoder.wasm.section.ImportSection
import kotlin.test.Test
import kotlin.test.assertEquals

class ImportSectionDecoderTest {

    @Test
    fun `can decode an import section`() {

        val subDecoder: Decoder<Import> = { _ ->
            Err(SectionDecodeError.UnknownImportDescriptor(117u))
        }

        val vectorDecoder: VectorDecoder<Import> = { _, _ ->
            Ok(Vector(emptyList()))
        }

        val expected = Ok(ImportSection(emptyList()))

        val context = decoderContext()
        val actual = ImportSectionDecoder(
            context = context,
            vectorDecoder = vectorDecoder,
            importDecoder = subDecoder,
        )

        assertEquals(expected, actual)
    }
}
