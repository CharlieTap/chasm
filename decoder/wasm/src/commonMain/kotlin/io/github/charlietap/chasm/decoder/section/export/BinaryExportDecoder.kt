package io.github.charlietap.chasm.decoder.section.export

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.decoder.value.name.BinaryNameValueDecoder
import io.github.charlietap.chasm.decoder.value.name.NameValueDecoder
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryExportDecoder(
    reader: WasmBinaryReader,
): Result<Export, WasmDecodeError> =
    BinaryExportDecoder(
        reader = reader,
        nameValueDecoder = ::BinaryNameValueDecoder,
        exportDescriptorDecoder = ::BinaryExportDescriptorDecoder,
    )

fun BinaryExportDecoder(
    reader: WasmBinaryReader,
    nameValueDecoder: NameValueDecoder,
    exportDescriptorDecoder: ExportDescriptorDecoder,
): Result<Export, WasmDecodeError> = binding {

    val name = nameValueDecoder(reader).bind()
    val descriptor = exportDescriptorDecoder(reader).bind()

    Export(name, descriptor)
}
