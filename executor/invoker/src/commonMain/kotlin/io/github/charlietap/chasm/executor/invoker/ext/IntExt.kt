@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.ext

internal inline fun Int.eq(other: Int): Boolean = this == other

internal inline fun Int.eqz(): Boolean = this == 0

internal inline fun Int.extend(): Long = this.toLong()

internal inline fun Int.convert(): Long = this.toLong()

internal inline fun Int.promote(): Long = this.toLong()
