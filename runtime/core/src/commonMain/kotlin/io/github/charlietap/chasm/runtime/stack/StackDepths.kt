package io.github.charlietap.chasm.runtime.stack

import kotlin.jvm.JvmInline

inline fun StackDepths(
    handlers: Int,
    values: Int,
): StackDepths {
    val depths =
        ((handlers and 0xFFFF) shl 16) or
            (values and 0xFFFF)
    return StackDepths(depths)
}

@JvmInline
value class StackDepths
    @PublishedApi
    internal constructor(
        @PublishedApi internal val depths: Int,
    ) {
        inline val handlers: Int
            get() = (depths ushr 16 and 0xFFFF)

        inline val values: Int
            get() = (depths and 0xFFFF)

        override fun toString(): String =
            "StackDepths(handlers=$handlers, values=$values)"
    }
