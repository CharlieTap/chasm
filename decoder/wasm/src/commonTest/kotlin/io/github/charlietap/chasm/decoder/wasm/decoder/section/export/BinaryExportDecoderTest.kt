package io.github.charlietap.chasm.decoder.wasm.decoder.section.export

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.wasm.decoder.value.name.NameValueDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryExportDecoderTest {

    @Test
    fun `can decode an export`() {

        val name = NameValue("name")
        val nameValueDecoder: NameValueDecoder = { _ ->
            Ok(name)
        }

        val descriptor = Export.Descriptor.Function(Index.FunctionIndex(117u))
        val exportDescriptorDecoder: ExportDescriptorDecoder = { _ ->
            Ok(descriptor)
        }

        val expected = Ok(Export(name, descriptor))

        val actual = BinaryExportDecoder(
            reader = FakeWasmBinaryReader(),
            nameValueDecoder = nameValueDecoder,
            exportDescriptorDecoder = exportDescriptorDecoder,
        )

        assertEquals(expected, actual)
    }
}
