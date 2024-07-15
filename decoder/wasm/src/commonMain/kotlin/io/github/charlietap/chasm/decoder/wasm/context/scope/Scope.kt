package io.github.charlietap.chasm.decoder.wasm.context.scope

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal typealias Scope<T> = (DecoderContext, T) -> Result<DecoderContext, WasmDecodeError>
