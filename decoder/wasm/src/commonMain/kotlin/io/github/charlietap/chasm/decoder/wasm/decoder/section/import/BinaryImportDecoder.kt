package io.github.charlietap.chasm.decoder.wasm.decoder.section.import

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.decoder.wasm.decoder.value.name.BinaryNameValueDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.value.name.NameValueDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryImportDecoder(
    reader: WasmBinaryReader,
): Result<Import, WasmDecodeError> =
    BinaryImportDecoder(
        reader = reader,
        nameValueDecoder = ::BinaryNameValueDecoder,
        importDescriptorDecoder = ::BinaryImportDescriptorDecoder,
    )

internal fun BinaryImportDecoder(
    reader: WasmBinaryReader,
    nameValueDecoder: NameValueDecoder,
    importDescriptorDecoder: ImportDescriptorDecoder,
): Result<Import, WasmDecodeError> = binding {

    val moduleName = nameValueDecoder(reader).bind()
    val entityName = nameValueDecoder(reader).bind()
    val descriptor = importDescriptorDecoder(reader).bind()

    Import(moduleName, entityName, descriptor)
}
