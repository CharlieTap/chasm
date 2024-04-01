package io.github.charlietap.chasm.decoder.wasm.decoder.type.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.FieldType
import io.github.charlietap.chasm.ast.type.StructType
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryStructTypeDecoder(
    reader: WasmBinaryReader,
): Result<StructType, WasmDecodeError> =
    BinaryStructTypeDecoder(
        reader = reader,
        fieldTypeDecoder = ::BinaryFieldTypeDecoder,
        vectorDecoder = ::BinaryVectorDecoder,
    )

internal fun BinaryStructTypeDecoder(
    reader: WasmBinaryReader,
    fieldTypeDecoder: FieldTypeDecoder,
    vectorDecoder: VectorDecoder<FieldType>,
): Result<StructType, WasmDecodeError> = binding {

    val fieldTypes = vectorDecoder(reader, fieldTypeDecoder).bind()

    StructType(fieldTypes.vector)
}
