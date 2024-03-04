package io.github.charlietap.chasm.error

import kotlin.jvm.JvmInline

interface ValueDecodeError : WasmDecodeError {

    @JvmInline
    value class InvalidFloat(val bytes: ByteArray) : ValueDecodeError

    @JvmInline
    value class InvalidDouble(val bytes: ByteArray) : ValueDecodeError
}
