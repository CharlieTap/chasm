package io.github.charlietap.chasm.decoder.section.import

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.error.SectionDecodeError
import io.github.charlietap.chasm.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.section.ImportSection
import io.github.charlietap.chasm.section.SectionSize
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryImportSectionDecoderTest {

    @Test
    fun `can decode an import section`() {

        val subDecoder: ImportDecoder = { _ ->
            Err(SectionDecodeError.UnknownImportDescriptor(117u))
        }

        val vectorDecoder: VectorDecoder<Import> = { _, sub ->
            assertEquals(subDecoder, sub)
            Ok(Vector(emptyList()))
        }

        val expected = Ok(ImportSection(emptyList()))

        val decoder = BinaryImportSectionDecoder(
            vectorDecoder = vectorDecoder,
            importDecoder = subDecoder,
        )

        val actual = decoder(FakeWasmBinaryReader(), SectionSize(0u))

        assertEquals(expected, actual)
    }
}
