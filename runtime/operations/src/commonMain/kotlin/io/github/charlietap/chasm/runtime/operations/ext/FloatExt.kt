package io.github.charlietap.chasm.runtime.operations.ext

import io.github.charlietap.chasm.InternalChasmApi
import kotlin.math.round
import kotlin.math.sqrt
import kotlin.math.truncate
import kotlin.math.withSign

@InternalChasmApi
inline fun Float.ceil(): Float = kotlin.math.ceil(this)

@InternalChasmApi
inline fun Float.copySign(
    other: Float,
): Float = this.withSign(other)

@InternalChasmApi
inline fun Float.eq(
    other: Float,
): Boolean = this == other

@InternalChasmApi
inline fun Float.floor(): Float = kotlin.math.floor(this)

@InternalChasmApi
inline fun Float.ge(
    other: Float,
): Boolean = this >= other

@InternalChasmApi
inline fun Float.gt(
    other: Float,
): Boolean = this > other

@InternalChasmApi
inline fun Float.le(
    other: Float,
): Boolean = this <= other

@InternalChasmApi
inline fun Float.lt(
    other: Float,
): Boolean = this < other

@InternalChasmApi
inline fun Float.ne(
    other: Float,
): Boolean = this != other

@InternalChasmApi
inline fun Float.nearest(): Float = round(this)

@InternalChasmApi
inline fun Float.max(
    other: Float,
): Float = kotlin.math.max(this, other)

@InternalChasmApi
inline fun Float.min(
    other: Float,
): Float = kotlin.math.min(this, other)

@InternalChasmApi
inline fun Float.sqrt(): Float = sqrt(this)

@InternalChasmApi
inline fun Float.trunc(): Float = kotlin.math.truncate(this)

@InternalChasmApi
inline fun Float.truncI32s(): Int = toInt()

@InternalChasmApi
inline fun Float.truncI32u(): Int = toUInt().toInt()

@InternalChasmApi
inline fun Float.truncI64s(): Long = toLong()

@InternalChasmApi
inline fun Float.truncI64u(): Long = toULong().toLong()

@InternalChasmApi
inline fun Float.truncI32sTrapping(): Int = when {
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
inline fun Float.truncI32uTrapping(): Int = when {
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
inline fun Float.truncI64sTrapping(): Long = when {
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
inline fun Float.truncI64uTrapping(): Long = when {
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
