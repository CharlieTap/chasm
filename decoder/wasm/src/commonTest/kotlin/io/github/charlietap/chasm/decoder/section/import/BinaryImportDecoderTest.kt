package io.github.charlietap.chasm.decoder.section.import

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.value.name.NameValueDecoder
import io.github.charlietap.chasm.reader.FakeWasmBinaryReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryImportDecoderTest {

    @Test
    fun `can decode an import`() {

        val moduleName = NameValue("module")
        val entityName = NameValue("entity")

        val nameIter = sequenceOf(moduleName, entityName).iterator()
        val nameValueDecoder: NameValueDecoder = { _ ->
            Ok(nameIter.next())
        }

        val descriptor = Import.Descriptor.Function(Index.TypeIndex(117u))
        val importDescriptorDecoder: ImportDescriptorDecoder = { _ ->
            Ok(descriptor)
        }

        val expected = Ok(Import(moduleName, entityName, descriptor))

        val actual = BinaryImportDecoder(
            reader = FakeWasmBinaryReader(),
            nameValueDecoder = nameValueDecoder,
            importDescriptorDecoder = importDescriptorDecoder,
        )

        assertEquals(expected, actual)
    }
}
