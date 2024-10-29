package io.github.charlietap.chasm.decoder.decoder.section.import

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.vector.Vector
import io.github.charlietap.chasm.decoder.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.section.ImportSection
import io.github.charlietap.chasm.fixture.module.import
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ImportSectionDecoderTest {

    @Test
    fun `can decode an import section`() {

        val subDecoder: Decoder<Import> = { _ ->
            fail("ImportDecoder should not be called directly")
        }

        val import = import()
        val vectorDecoder: VectorDecoder<Import> = { _, _ ->
            Ok(Vector(listOf(import)))
        }

        val expected = Ok(ImportSection(listOf(import)))

        val context = decoderContext()
        val actual = ImportSectionDecoder(
            context = context,
            vectorDecoder = vectorDecoder,
            importDecoder = subDecoder,
        )

        assertEquals(expected, actual)
        assertEquals(listOf(import), context.imports)
    }
}
