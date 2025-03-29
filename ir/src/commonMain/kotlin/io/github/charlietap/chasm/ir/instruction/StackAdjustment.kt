package io.github.charlietap.chasm.ir.instruction

import kotlin.jvm.JvmInline

fun StackAdjustment(
    depth: Int,
    keep: Int,
): StackAdjustment {
    val encoded = (depth.toLong() shl 32) or (keep.toLong() and 0xFFFFFFFFL)
    return StackAdjustment(encoded)
}

@JvmInline
value class StackAdjustment(
    @PublishedApi internal val encoded: Long,
) {
    inline val depth: Int
        get() = (encoded ushr 32).toInt()

    inline val keep: Int
        get() = (encoded and 0xFFFFFFFF).toInt()

    override fun toString(): String = "StackAdjustment(depth=$depth, keep=$keep)"
}
