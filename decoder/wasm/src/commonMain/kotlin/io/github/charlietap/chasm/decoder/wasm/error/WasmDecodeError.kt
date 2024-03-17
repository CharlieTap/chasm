package io.github.charlietap.chasm.decoder.wasm.error

import io.github.charlietap.chasm.decoder.ModuleDecoderError
import kotlin.jvm.JvmInline

sealed interface WasmDecodeError : ModuleDecoderError {
    @JvmInline
    value class InvalidWasmFile(val number: UByteArray) : WasmDecodeError

    @JvmInline
    value class UnsupportedVersion(val version: UByteArray) : WasmDecodeError

    @JvmInline
    value class IOError(val throwable: Throwable) : WasmDecodeError
}
