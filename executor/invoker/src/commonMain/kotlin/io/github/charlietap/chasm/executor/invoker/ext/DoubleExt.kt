@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.ext

import kotlin.math.round
import kotlin.math.sqrt
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

internal inline fun Double.sqrt(): Double = sqrt(this)

internal inline fun Double.trunc(): Double = kotlin.math.truncate(this)

internal inline fun Double.truncI32s(): Int = toInt()

internal inline fun Double.truncI32u(): Int = toUInt().toInt()

internal inline fun Double.truncI64s(): Long = toLong()

internal inline fun Double.truncI64u(): Long = toULong().toLong()

internal inline fun Double.truncI32sTrapping(): Int = when {
    this.isNaN() -> throw IllegalArgumentException()
    this.toULong() > UInt.MAX_VALUE -> throw IllegalArgumentException()
    this.toLong() < Int.MIN_VALUE -> throw IllegalArgumentException()
    else -> toInt()
}

internal inline fun Double.truncI32uTrapping(): Int = (this to this.toUInt()).let { (float, uint) ->
    when {
        float.isNaN() -> throw IllegalArgumentException()
        uint > UInt.MAX_VALUE -> throw IllegalArgumentException()
        uint < UInt.MIN_VALUE -> throw IllegalArgumentException()
        else -> uint.toInt()
    }
}

internal inline fun Double.truncI64sTrapping(): Long = when {
    this.isNaN() -> throw IllegalArgumentException()
    this > Long.MAX_VALUE -> throw IllegalArgumentException()
    this < Long.MIN_VALUE -> throw IllegalArgumentException()
    else -> toLong()
}

internal inline fun Double.truncI64uTrapping(): Long = (this to this.toULong()).let { (float, ulong) ->
    when {
        float.isNaN() -> throw IllegalArgumentException()
        ulong > ULong.MAX_VALUE -> throw IllegalArgumentException()
        ulong < ULong.MIN_VALUE -> throw IllegalArgumentException()
        else -> ulong.toLong()
    }
}
