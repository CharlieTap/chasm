package io.github.charlietap.chasm.decoder.error

sealed interface ModuleDecodeError : WasmDecodeError {
    data object ModuleMalformed : ModuleDecoderError
}
