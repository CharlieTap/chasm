package io.github.charlietap.chasm.embedding.error

import kotlin.jvm.JvmInline

sealed interface ExecutionError<out Exception> {

    @JvmInline
    value class Trapped(val trap: Trap) : InstantiationError<Nothing>, InvocationError<Nothing> {

        override fun toString(): String {
            return trap.toString()
        }
    }

    data class Thrown<out Exception>(val exception: Exception) : InstantiationError<Exception>, InvocationError<Exception> {

        override fun toString(): String {
            return "uncaught exception $exception"
        }
    }

    @JvmInline
    value class Unlinkable(val message: String) : InstantiationError<Nothing> {

        override fun toString(): String {
            return message
        }
    }

    @JvmInline
    value class Unsupported(val message: String) : InstantiationError<Nothing> {

        override fun toString(): String {
            return message
        }
    }

    @JvmInline
    value class Rejected(val message: String) : InvocationError<Nothing> {

        override fun toString(): String {
            return message
        }
    }
}

sealed interface InstantiationError<out Exception> : ExecutionError<Exception>

sealed interface InvocationError<out Exception> : ExecutionError<Exception>

sealed interface ModuleError {

    @JvmInline
    value class Malformed(val message: String) : ModuleError {

        override fun toString(): String {
            return message
        }
    }

    @JvmInline
    value class Invalid(val message: String) : ModuleError {

        override fun toString(): String {
            return message
        }
    }
}
