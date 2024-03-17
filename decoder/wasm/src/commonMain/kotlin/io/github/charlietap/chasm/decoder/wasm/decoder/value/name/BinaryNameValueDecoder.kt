package io.github.charlietap.chasm.decoder.wasm.decoder.value.name

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.BinaryByteVectorDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.ByteVectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryNameValueDecoder(
    reader: WasmBinaryReader,
): Result<NameValue, WasmDecodeError> = BinaryNameValueDecoder(reader, ::BinaryByteVectorDecoder)

internal fun BinaryNameValueDecoder(
    reader: WasmBinaryReader,
    vectorDecoder: ByteVectorDecoder,
): Result<NameValue, WasmDecodeError> = binding {

    val vector = vectorDecoder(reader).bind()

    NameValue(vector.bytes.asByteArray().decodeToString())
}
