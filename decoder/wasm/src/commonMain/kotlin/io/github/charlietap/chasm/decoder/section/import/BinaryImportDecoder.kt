package io.github.charlietap.chasm.decoder.section.import

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.decoder.value.name.BinaryNameValueDecoder
import io.github.charlietap.chasm.decoder.value.name.NameValueDecoder
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryImportDecoder(
    reader: WasmBinaryReader,
): Result<Import, WasmDecodeError> =
    BinaryImportDecoder(
        reader = reader,
        nameValueDecoder = ::BinaryNameValueDecoder,
        importDescriptorDecoder = ::BinaryImportDescriptorDecoder,
    )

fun BinaryImportDecoder(
    reader: WasmBinaryReader,
    nameValueDecoder: NameValueDecoder,
    importDescriptorDecoder: ImportDescriptorDecoder,
): Result<Import, WasmDecodeError> = binding {

    val moduleName = nameValueDecoder(reader).bind()
    val entityName = nameValueDecoder(reader).bind()
    val descriptor = importDescriptorDecoder(reader).bind()

    Import(moduleName, entityName, descriptor)
}
