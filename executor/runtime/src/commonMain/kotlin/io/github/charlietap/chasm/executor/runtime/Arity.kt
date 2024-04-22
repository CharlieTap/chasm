package io.github.charlietap.chasm.executor.runtime

import kotlin.jvm.JvmInline

sealed interface Arity {
    val value: Int

    @JvmInline
    value class Argument(override val value: Int) : Arity {
        companion object {
            val NULLARY = Argument(0)
            val UNARY = Argument(1)
            val BINARY = Argument(2)
            val TERNARY = Argument(3)
        }
    }

    @JvmInline
    value class Return(override val value: Int) : Arity {
        companion object {
            val SIDE_EFFECT = Return(0)
        }
    }
}
