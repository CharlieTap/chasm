package io.github.charlietap.chasm.decoder.wasm.decoder

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal typealias Decoder<T> = (WasmBinaryReader) -> Result<T, WasmDecodeError>
