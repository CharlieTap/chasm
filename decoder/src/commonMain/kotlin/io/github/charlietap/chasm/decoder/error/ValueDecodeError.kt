package io.github.charlietap.chasm.decoder.error

import kotlin.jvm.JvmInline

interface ValueDecodeError : WasmDecodeError {

    @JvmInline
    value class InvalidFloat(val bytes: ByteArray) : ValueDecodeError

    @JvmInline
    value class InvalidDouble(val bytes: ByteArray) : ValueDecodeError

    @JvmInline
    value class InvalidName(val bytes: UByteArray) : ValueDecodeError
}
