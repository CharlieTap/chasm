package io.github.charlietap.chasm.decoder.wasm.decoder.type.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.ArrayType
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryArrayTypeDecoder(
    reader: WasmBinaryReader,
): Result<ArrayType, WasmDecodeError> =
    BinaryArrayTypeDecoder(
        reader = reader,
        fieldTypeDecoder = ::BinaryFieldTypeDecoder,
    )

internal fun BinaryArrayTypeDecoder(
    reader: WasmBinaryReader,
    fieldTypeDecoder: FieldTypeDecoder,
): Result<ArrayType, WasmDecodeError> = binding {

    val fieldType = fieldTypeDecoder(reader).bind()

    ArrayType(fieldType)
}
