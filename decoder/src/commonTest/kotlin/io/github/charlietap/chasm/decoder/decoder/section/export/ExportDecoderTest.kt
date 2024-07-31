package io.github.charlietap.chasm.decoder.decoder.section.export

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import kotlin.test.Test
import kotlin.test.assertEquals

class ExportDecoderTest {

    @Test
    fun `can decode an export`() {

        val name = NameValue("name")
        val nameValueDecoder: Decoder<NameValue> = { _ ->
            Ok(name)
        }

        val descriptor = Export.Descriptor.Function(Index.FunctionIndex(117u))
        val exportDescriptorDecoder: Decoder<Export.Descriptor> = { _ ->
            Ok(descriptor)
        }

        val expected = Ok(Export(name, descriptor))

        val actual = ExportDecoder(
            context = decoderContext(),
            nameValueDecoder = nameValueDecoder,
            exportDescriptorDecoder = exportDescriptorDecoder,
        )

        assertEquals(expected, actual)
    }
}
