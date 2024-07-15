package io.github.charlietap.chasm.decoder.wasm

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal typealias Decoder<T> = (DecoderContext) -> Result<T, WasmDecodeError>
