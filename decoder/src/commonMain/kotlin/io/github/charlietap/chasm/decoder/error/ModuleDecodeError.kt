package io.github.charlietap.chasm.decoder.error

internal sealed interface ModuleDecodeError : WasmDecodeError {
    data object ModuleMalformed : ModuleDecodeError
}
