package io.github.charlietap.chasm.decoder.vector

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryVectorLengthDecoder(
    reader: WasmBinaryReader,
): Result<VectorLength, WasmDecodeError> = binding {
    VectorLength(reader.uint().bind())
}
