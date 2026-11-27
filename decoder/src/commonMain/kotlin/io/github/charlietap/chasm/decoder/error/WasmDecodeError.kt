package io.github.charlietap.chasm.decoder.error

import kotlin.jvm.JvmInline

internal sealed interface WasmDecodeError : ModuleDecoderError, ComponentDecoderError {
    @JvmInline
    value class InvalidWasmFile(val number: UByteArray) : WasmDecodeError

    @JvmInline
    value class UnsupportedVersion(val version: UByteArray) : WasmDecodeError

    @JvmInline
    value class IOError(val throwable: Throwable) : WasmDecodeError
}
