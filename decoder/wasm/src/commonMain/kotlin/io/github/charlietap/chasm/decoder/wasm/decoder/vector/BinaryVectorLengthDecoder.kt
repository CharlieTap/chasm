package io.github.charlietap.chasm.decoder.wasm.decoder.vector

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryVectorLengthDecoder(
    reader: WasmBinaryReader,
): Result<VectorLength, WasmDecodeError> = binding {
    VectorLength(reader.uint().bind())
}
