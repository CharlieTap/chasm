package io.github.charlietap.chasm.executor.runtime.stack

import kotlin.jvm.JvmInline

inline fun StackDepths(
    handlers: Int,
    instructions: Int,
    labels: Int,
    values: Int,
): StackDepths {
    val depths =
        ((handlers.toLong() and 0xFFFF) shl 48) or
            ((instructions.toLong() and 0xFFFF) shl 32) or
            ((labels.toLong() and 0xFFFF) shl 16) or
            (values.toLong() and 0xFFFF)
    return StackDepths(depths)
}

@JvmInline
value class StackDepths
    @PublishedApi
    internal constructor(
        @PublishedApi internal val depths: Long,
    ) {
        inline val handlers: Int
            get() = (depths ushr 48 and 0xFFFF).toInt()

        inline val instructions: Int
            get() = (depths ushr 32 and 0xFFFF).toInt()

        inline val labels: Int
            get() = (depths ushr 16 and 0xFFFF).toInt()

        inline val values: Int
            get() = (depths and 0xFFFF).toInt()

        override fun toString(): String =
            "StackDepths(handlers=$handlers, instructions=$instructions, labels=$labels, values=$values)"
    }
