package io.github.charlietap.chasm.decoder.context.scope

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal typealias Scope<T> = (DecoderContext, T) -> Result<DecoderContext, WasmDecodeError>
