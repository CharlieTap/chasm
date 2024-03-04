package io.github.charlietap.chasm.decoder.vector

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryByteVectorDecoder(
    reader: WasmBinaryReader,
): Result<ByteVector, WasmDecodeError> = binding {

    val vecLength = reader.uint().bind()
    val vecBytes = reader.ubytes(vecLength).bind()

    ByteVector(vecBytes, vecLength)
}
