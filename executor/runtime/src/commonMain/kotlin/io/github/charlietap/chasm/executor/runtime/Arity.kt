package io.github.charlietap.chasm.executor.runtime

import kotlin.jvm.JvmInline

@JvmInline
value class Arity(val value: Int) {
    companion object {
        val SIDE_EFFECT = Arity(0)
    }
}
