package io.github.charlietap.chasm.decoder.decoder.vector

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.reader.WasmBinaryReader

internal fun BinaryVectorLengthDecoder(
    reader: WasmBinaryReader,
): Result<VectorLength, WasmDecodeError> = binding {
    VectorLength(reader.uint().bind())
}
