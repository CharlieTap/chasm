package io.github.charlietap.chasm.embedding.error

import kotlin.jvm.JvmInline

sealed interface ChasmError {

    val error: String

    @JvmInline
    value class DecodeError(override val error: String) : ChasmError

    @JvmInline
    value class ValidationError(override val error: String) : ChasmError

    class ExecutionError(
        override val error: String,
        /** Present only for traps raised with [io.github.charlietap.chasm.config.RuntimeConfig.debugInfo] enabled. */
        val trap: WasmTrap?,
    ) : ChasmError {

        constructor(error: String) : this(error, null)

        override fun equals(other: Any?): Boolean = other is ExecutionError && other.error == error

        override fun hashCode(): Int = error.hashCode()

        override fun toString(): String = "ExecutionError(error=$error)"
    }
}
