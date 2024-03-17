package io.github.charlietap.chasm.decoder.wasm.decoder.vector

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias VectorLengthDecoder = (WasmBinaryReader) -> Result<VectorLength, WasmDecodeError>
