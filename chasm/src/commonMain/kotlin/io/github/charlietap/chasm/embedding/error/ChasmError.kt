package io.github.charlietap.chasm.embedding.error

import kotlin.jvm.JvmInline

sealed interface ChasmError {
    @JvmInline
    value class DecodeError(val error: String) : ChasmError

    @JvmInline
    value class ValidationError(val error: String) : ChasmError

    @JvmInline
    value class ExecutionError(val error: String) : ChasmError
}
