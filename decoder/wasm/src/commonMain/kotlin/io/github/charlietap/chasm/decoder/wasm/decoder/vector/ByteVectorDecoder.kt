package io.github.charlietap.chasm.decoder.wasm.decoder.vector

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias ByteVectorDecoder = (WasmBinaryReader) -> Result<ByteVector, WasmDecodeError>
