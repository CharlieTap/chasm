package io.github.charlietap.chasm.decoder.wasm.error

import io.github.charlietap.chasm.decoder.ModuleDecoderError

sealed interface ModuleDecodeError : WasmDecodeError {
    data object ModuleMalformed : ModuleDecoderError
}
