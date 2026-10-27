package io.github.charlietap.chasm.embedding.error

import kotlin.jvm.JvmInline

sealed interface ChasmError {

    val error: String

    /** A module could not be created from its binary. */
    sealed interface ModuleError : ChasmError

    @JvmInline
    value class DecodeError(override val error: String) : ModuleError

    @JvmInline
    value class ValidationError(override val error: String) : ModuleError

    class ExecutionError(
        override val error: String,
        /** Present only for traps raised with `RuntimeConfig.debugInfo` enabled. */
        val trap: WasmTrap?,
    ) : ChasmError {

        constructor(error: String) : this(error, null)

        override fun equals(other: Any?): Boolean = other is ExecutionError && other.error == error

        override fun hashCode(): Int = error.hashCode()

        override fun toString(): String = "ExecutionError(error=$error)"
    }
}
