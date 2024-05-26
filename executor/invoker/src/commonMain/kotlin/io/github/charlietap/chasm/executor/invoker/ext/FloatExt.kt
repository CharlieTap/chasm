@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.ext

import kotlin.math.round
import kotlin.math.sqrt
import kotlin.math.truncate
import kotlin.math.withSign

internal inline fun Float.ceil(): Float = kotlin.math.ceil(this)

internal inline fun Float.copySign(other: Float): Float = this.withSign(other)

internal inline fun Float.eq(other: Float): Boolean = this == other

internal inline fun Float.floor(): Float = kotlin.math.floor(this)

internal inline fun Float.ge(other: Float): Boolean = this >= other

internal inline fun Float.gt(other: Float): Boolean = this > other

internal inline fun Float.le(other: Float): Boolean = this <= other

internal inline fun Float.lt(other: Float): Boolean = this < other

internal inline fun Float.ne(other: Float): Boolean = this != other

internal inline fun Float.nearest(): Float = round(this)

internal inline fun Float.max(other: Float): Float = kotlin.math.max(this, other)

internal inline fun Float.min(other: Float): Float = kotlin.math.min(this, other)

internal inline fun Float.sqrt(): Float = sqrt(this)

internal inline fun Float.trunc(): Float = kotlin.math.truncate(this)

internal inline fun Float.truncI32s(): Int = toInt()

internal inline fun Float.truncI32u(): Int = toUInt().toInt()

internal inline fun Float.truncI64s(): Long = toLong()

internal inline fun Float.truncI64u(): Long = toULong().toLong()

internal inline fun Float.truncI32sTrapping(): Int = when {
    this.isNaN() -> throw IllegalArgumentException()
    this.isInfinite() -> throw IllegalArgumentException()
    else -> {
        val truncated = truncate(this)
        if (truncated.toLong() > Int.MAX_VALUE || truncated.toLong() < Int.MIN_VALUE) {
            throw IllegalArgumentException()
        }
        truncated.toInt()
    }
}

internal inline fun Float.truncI32uTrapping(): Int = when {
    this.isNaN() -> throw IllegalArgumentException()
    this.isInfinite() -> throw IllegalArgumentException()
    else -> {
        val truncated = truncate(this)
        if (truncated < 0) {
            throw IllegalArgumentException()
        }
        if (truncated.toLong() > UInt.MAX_VALUE.toLong()) {
            throw IllegalArgumentException()
        }
        truncated.toUInt().toInt()
    }
}

internal inline fun Float.truncI64sTrapping(): Long = when {
    this.isNaN() -> throw IllegalArgumentException()
    this.isInfinite() -> throw IllegalArgumentException()
    else -> {
        val truncated = truncate(this)
        if (truncated.toULong() > Long.MAX_VALUE.toULong() || truncated < Long.MIN_VALUE) {
            throw IllegalArgumentException()
        }
        truncated.toLong()
    }
}

internal inline fun Float.truncI64uTrapping(): Long = when {
    this.isNaN() -> throw IllegalArgumentException()
    this.isInfinite() -> throw IllegalArgumentException()
    else -> {
        val truncated = truncate(this)
        if (truncated < 0) {
            throw IllegalArgumentException()
        }
        if (truncated >= ULong.MAX_VALUE.toDouble()) {
            throw IllegalArgumentException()
        }
        truncated.toULong().toLong()
    }
}
