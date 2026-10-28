package io.github.charlietap.chasm.runtime.operations.ext

import io.github.charlietap.chasm.sse2.ext.fsqrt
import kotlin.math.round
import kotlin.math.truncate
import kotlin.math.withSign

inline fun Double.ceil(): Double = kotlin.math.ceil(this)

inline fun Double.copySign(
    other: Double,
): Double = this.withSign(other)

inline fun Double.eq(
    other: Double,
): Boolean = this == other

inline fun Double.floor(): Double = kotlin.math.floor(this)

inline fun Double.ge(
    other: Double,
): Boolean = this >= other

inline fun Double.gt(
    other: Double,
): Boolean = this > other

inline fun Double.le(
    other: Double,
): Boolean = this <= other

inline fun Double.lt(
    other: Double,
): Boolean = this < other

inline fun Double.ne(
    other: Double,
): Boolean = this != other

inline fun Double.nearest(): Double = round(this)

inline fun Double.max(
    other: Double,
): Double = kotlin.math.max(this, other)

inline fun Double.min(
    other: Double,
): Double = kotlin.math.min(this, other)

inline fun Double.sqrt(): Double = fsqrt()

inline fun Double.trunc(): Double = truncate(this)

inline fun Double.truncI32s(): Int = toInt()

inline fun Double.truncI32u(): Int = toUInt().toInt()

inline fun Double.truncI64s(): Long = toLong()

inline fun Double.truncI64u(): Long = toULong().toLong()

inline fun Double.truncI32sTrapping(): Int = when {
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

inline fun Double.truncI32uTrapping(): Int = when {
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

inline fun Double.truncI64sTrapping(): Long = when {
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

inline fun Double.truncI64uTrapping(): Long = when {
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
