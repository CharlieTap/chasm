@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.ext

internal inline fun Long.divu(other: Long): Long = this.toULong().div(other.toULong()).toLong()

internal inline fun Long.eq(other: Long): Boolean = this == other

internal inline fun Long.eqz(): Boolean = this == 0L

internal inline fun Long.ge(other: Long): Boolean = this >= other

internal inline fun Long.gt(other: Long): Boolean = this > other

internal inline fun Long.le(other: Long): Boolean = this <= other

internal inline fun Long.lt(other: Long): Boolean = this < other

internal inline fun Long.geu(other: Long): Boolean = this.toULong() >= other.toULong()

internal inline fun Long.gtu(other: Long): Boolean = this.toULong() > other.toULong()

internal inline fun Long.leu(other: Long): Boolean = this.toULong() <= other.toULong()

internal inline fun Long.ltu(other: Long): Boolean = this.toULong() < other.toULong()

internal inline fun Long.ne(other: Long): Boolean = this != other

internal inline fun Long.remu(other: Long): Long = this.toULong().rem(other.toULong()).toLong()

internal inline fun Long.rotateLeft(other: Long) = this.rotateLeft(other.toInt())

internal inline fun Long.rotateRight(other: Long) = this.rotateRight(other.toInt())

internal inline fun Long.shl(other: Long): Long = this.shl(other.toInt())

internal inline fun Long.shr(other: Long): Long = this.shr(other.toInt())

internal inline fun Long.shru(other: Long): Long = this.toULong().shr(other.toInt()).toLong()

internal inline fun Long.wrap(): Int = this.toInt()

internal inline fun Long.countOnePopulation(): Long = countOneBits().toLong()

internal inline fun Long.countLeadingZero(): Long = countLeadingZeroBits().toLong()

internal inline fun Long.countTrailingZero(): Long = countTrailingZeroBits().toLong()

internal inline fun Long.convertF32s(): Float = this.toFloat()

internal inline fun Long.convertF32u(): Float = this.toULong().toFloat()

internal inline fun Long.convertF64s(): Double = this.toDouble()

internal inline fun Long.convertF64u(): Double = this.toULong().toDouble()

internal inline fun Long.extend8s(): Long = (this and 0xFF).toByte().toLong()

internal inline fun Long.extend16s(): Long = (this and 0xFFFF).toShort().toLong()

internal inline fun Long.extend32s(): Long = toInt().toLong()
