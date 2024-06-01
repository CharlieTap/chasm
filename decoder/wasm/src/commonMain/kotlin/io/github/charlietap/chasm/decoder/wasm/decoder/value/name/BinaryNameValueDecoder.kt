package io.github.charlietap.chasm.decoder.wasm.decoder.value.name

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.BinaryByteVectorDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.vector.ByteVectorDecoder
import io.github.charlietap.chasm.decoder.wasm.error.ValueDecodeError
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
    val name = try {
        Ok(vector.bytes.asByteArray().decodeToString(throwOnInvalidSequence = true))
    } catch (e: Exception) {
        Err(ValueDecodeError.InvalidName(vector.bytes))
    }.bind()

    NameValue(name)
}
