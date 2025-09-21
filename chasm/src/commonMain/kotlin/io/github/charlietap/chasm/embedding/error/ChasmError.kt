package io.github.charlietap.chasm.embedding.error

import kotlin.jvm.JvmInline

sealed interface ChasmError {

    val error: String

    @JvmInline
    value class DecodeError(override val error: String) : ChasmError

    @JvmInline
    value class ValidationError(override val error: String) : ChasmError

    @JvmInline
    value class ExecutionError(override val error: String) : ChasmError
}
