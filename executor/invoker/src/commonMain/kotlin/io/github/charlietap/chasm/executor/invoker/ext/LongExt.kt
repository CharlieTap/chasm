@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.ext

internal inline fun Long.eq(other: Long): Boolean = this == other

internal inline fun Long.eqz(): Boolean = this == 0L

internal inline fun Long.wrap(): Int = this.toInt()

internal inline fun Long.countLeadingZero(): Long = countLeadingZeroBits().toLong()

internal inline fun Long.countTrailingZero(): Long = countTrailingZeroBits().toLong()
