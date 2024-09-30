package io.github.charlietap.chasm.decoder.decoder.section.import

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.fixture.type.functionType
import kotlin.test.Test
import kotlin.test.assertEquals

class ImportDecoderTest {

    @Test
    fun `can decode an import`() {

        val moduleName = NameValue("module")
        val entityName = NameValue("entity")

        val nameIter = sequenceOf(moduleName, entityName).iterator()
        val nameValueDecoder: Decoder<NameValue> = { _ ->
            Ok(nameIter.next())
        }

        val descriptor = Import.Descriptor.Function(functionType())
        val importDescriptorDecoder: Decoder<Import.Descriptor> = { _ ->
            Ok(descriptor)
        }

        val expected = Ok(Import(moduleName, entityName, descriptor))

        val actual = ImportDecoder(
            context = decoderContext(),
            nameValueDecoder = nameValueDecoder,
            importDescriptorDecoder = importDescriptorDecoder,
        )

        assertEquals(expected, actual)
    }
}
