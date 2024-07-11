@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.ext

import io.github.charlietap.chasm.sse2.ext.fsqrt
import kotlin.math.round
import kotlin.math.truncate
import kotlin.math.withSign

internal inline fun Double.ceil(): Double = kotlin.math.ceil(this)

internal inline fun Double.copySign(other: Double): Double = this.withSign(other)

internal inline fun Double.eq(other: Double): Boolean = this == other

internal inline fun Double.floor(): Double = kotlin.math.floor(this)

internal inline fun Double.ge(other: Double): Boolean = this >= other

internal inline fun Double.gt(other: Double): Boolean = this > other

internal inline fun Double.le(other: Double): Boolean = this <= other

internal inline fun Double.lt(other: Double): Boolean = this < other

internal inline fun Double.ne(other: Double): Boolean = this != other

internal inline fun Double.nearest(): Double = round(this)

internal inline fun Double.max(other: Double): Double = kotlin.math.max(this, other)

internal inline fun Double.min(other: Double): Double = kotlin.math.min(this, other)

internal inline fun Double.sqrt(): Double = fsqrt()

internal inline fun Double.trunc(): Double = kotlin.math.truncate(this)

internal inline fun Double.truncI32s(): Int = toInt()

internal inline fun Double.truncI32u(): Int = toUInt().toInt()

internal inline fun Double.truncI64s(): Long = toLong()

internal inline fun Double.truncI64u(): Long = toULong().toLong()

internal inline fun Double.truncI32sTrapping(): Int = when {
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

internal inline fun Double.truncI32uTrapping(): Int = when {
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

internal inline fun Double.truncI64sTrapping(): Long = when {
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

internal inline fun Double.truncI64uTrapping(): Long = when {
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
