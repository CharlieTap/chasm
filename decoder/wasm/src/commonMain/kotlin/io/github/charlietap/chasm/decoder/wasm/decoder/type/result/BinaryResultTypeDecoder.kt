package io.github.charlietap.chasm.decoder.wasm.decoder.type.result

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.BinaryValueTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.type.value.ValueTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.BinaryVectorDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.VectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryResultTypeDecoder(
    reader: WasmBinaryReader,
): Result<ResultType, WasmDecodeError> = BinaryResultTypeDecoder(
    reader,
    ::BinaryVectorDecoder,
    ::BinaryValueTypeDecoder,
)

internal fun BinaryResultTypeDecoder(
    reader: WasmBinaryReader,
    vectorDecoder: VectorDecoder<ValueType>,
    valueTypeDecoder: ValueTypeDecoder,
): Result<ResultType, WasmDecodeError> = binding {
    val valueTypes = vectorDecoder(reader, valueTypeDecoder).bind()
    ResultType(valueTypes.vector)
}
