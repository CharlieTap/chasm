package io.github.charlietap.chasm.runtime.operations.ext

import kotlin.math.pow

inline fun Long.divu(
    other: Long,
): Long = this.toULong().div(other.toULong()).toLong()

inline fun Long.eq(
    other: Long,
): Boolean = this == other

inline fun Long.eqz(): Boolean = this == 0L

inline fun Long.ge(
    other: Long,
): Boolean = this >= other

inline fun Long.gt(
    other: Long,
): Boolean = this > other

inline fun Long.le(
    other: Long,
): Boolean = this <= other

inline fun Long.lt(
    other: Long,
): Boolean = this < other

inline fun Long.geu(
    other: Long,
): Boolean = this.toULong() >= other.toULong()

inline fun Long.gtu(
    other: Long,
): Boolean = this.toULong() > other.toULong()

inline fun Long.leu(
    other: Long,
): Boolean = this.toULong() <= other.toULong()

inline fun Long.ltu(
    other: Long,
): Boolean = this.toULong() < other.toULong()

inline fun Long.ne(
    other: Long,
): Boolean = this != other

inline fun Long.remu(
    other: Long,
): Long = this.toULong().rem(other.toULong()).toLong()

inline fun Long.rotateLeft(
    other: Long,
) = this.rotateLeft(other.toInt())

inline fun Long.rotateRight(
    other: Long,
) = this.rotateRight(other.toInt())

inline fun Long.shl(
    other: Long,
): Long = this.shl(other.toInt())

inline fun Long.shr(
    other: Long,
): Long = this.shr(other.toInt())

inline fun Long.shru(
    other: Long,
): Long = this.toULong().shr(other.toInt()).toLong()

inline fun Long.wrap(): Int = this.toInt()

inline fun Long.countOnePopulation(): Long = countOneBits().toLong()

inline fun Long.countLeadingZero(): Long = countLeadingZeroBits().toLong()

inline fun Long.countTrailingZero(): Long = countTrailingZeroBits().toLong()

inline fun Long.convertF32s(): Float = this.toFloat()

inline fun Long.convertF32u(): Float {
    return if (this.toULong() < 0x10_0000_0000_0000uL) {
        this.toULong().toFloat()
    } else {
        val r = if (this.toULong() and 0xfffuL == 0uL) 0uL else 1uL
        val result = (this.toULong() shr 12) or r
        (result.toFloat() * 2.0f.pow(12))
    }
}

inline fun Long.convertF64s(): Double = this.toDouble()

inline fun Long.convertF64u(): Double = this.toULong().toDouble()

inline fun Long.extend8s(): Long = (this and 0xFF).toByte().toLong()

inline fun Long.extend16s(): Long = (this and 0xFFFF).toShort().toLong()

inline fun Long.extend32s(): Long = toInt().toLong()
