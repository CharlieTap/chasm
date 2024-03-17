package io.github.charlietap.chasm.decoder.wasm.decoder.vector

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias SubDecoder<T> = (WasmBinaryReader) -> Result<T, WasmDecodeError>
internal typealias VectorDecoder<T> = (WasmBinaryReader, SubDecoder<T>) -> Result<Vector<T>, WasmDecodeError>
