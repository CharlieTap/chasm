package io.github.charlietap.chasm.runtime.operations.ext

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.sse2.ext.fsqrt
import kotlin.math.round
import kotlin.math.truncate
import kotlin.math.withSign

@InternalChasmApi
inline fun Double.ceil(): Double = kotlin.math.ceil(this)

@InternalChasmApi
inline fun Double.copySign(
    other: Double,
): Double = this.withSign(other)

@InternalChasmApi
inline fun Double.eq(
    other: Double,
): Boolean = this == other

@InternalChasmApi
inline fun Double.floor(): Double = kotlin.math.floor(this)

@InternalChasmApi
inline fun Double.ge(
    other: Double,
): Boolean = this >= other

@InternalChasmApi
inline fun Double.gt(
    other: Double,
): Boolean = this > other

@InternalChasmApi
inline fun Double.le(
    other: Double,
): Boolean = this <= other

@InternalChasmApi
inline fun Double.lt(
    other: Double,
): Boolean = this < other

@InternalChasmApi
inline fun Double.ne(
    other: Double,
): Boolean = this != other

@InternalChasmApi
inline fun Double.nearest(): Double = round(this)

@InternalChasmApi
inline fun Double.max(
    other: Double,
): Double = kotlin.math.max(this, other)

@InternalChasmApi
inline fun Double.min(
    other: Double,
): Double = kotlin.math.min(this, other)

@InternalChasmApi
inline fun Double.sqrt(): Double = fsqrt()

@InternalChasmApi
inline fun Double.trunc(): Double = truncate(this)

@InternalChasmApi
inline fun Double.truncI32s(): Int = toInt()

@InternalChasmApi
inline fun Double.truncI32u(): Int = toUInt().toInt()

@InternalChasmApi
inline fun Double.truncI64s(): Long = toLong()

@InternalChasmApi
inline fun Double.truncI64u(): Long = toULong().toLong()

@InternalChasmApi
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

@InternalChasmApi
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

@InternalChasmApi
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

@InternalChasmApi
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
