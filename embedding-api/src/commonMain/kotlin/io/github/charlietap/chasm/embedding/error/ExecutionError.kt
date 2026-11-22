package io.github.charlietap.chasm.embedding.error

import io.github.charlietap.chasm.reference.ExceptionReference
import kotlin.jvm.JvmInline

sealed interface ExecutionError {

    @JvmInline
    value class Trapped(val trap: Trap) : InstantiationError, InvocationError {

        override fun toString(): String {
            return trap.toString()
        }
    }

    data class Thrown(val exception: ExceptionReference) : InstantiationError, InvocationError {

        override fun toString(): String {
            return "uncaught exception $exception"
        }
    }

    @JvmInline
    value class Unlinkable(val message: String) : InstantiationError {

        override fun toString(): String {
            return message
        }
    }

    @JvmInline
    value class Unsupported(val message: String) : InstantiationError {

        override fun toString(): String {
            return message
        }
    }

    @JvmInline
    value class Rejected(val message: String) : InvocationError {

        override fun toString(): String {
            return message
        }
    }
}

sealed interface InstantiationError : ExecutionError

sealed interface InvocationError : ExecutionError

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
