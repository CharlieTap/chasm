package io.github.charlietap.chasm.runtime.operations.ext

inline fun Int.divu(
    other: Int,
): Int = this.toUInt().div(other.toUInt()).toInt()

inline fun Int.eq(
    other: Int,
): Boolean = this == other

inline fun Int.eqz(): Boolean = this == 0

inline fun Int.ge(
    other: Int,
): Boolean = this >= other

inline fun Int.gt(
    other: Int,
): Boolean = this > other

inline fun Int.le(
    other: Int,
): Boolean = this <= other

inline fun Int.lt(
    other: Int,
): Boolean = this < other

inline fun Int.geu(
    other: Int,
): Boolean = this.toUInt() >= other.toUInt()

inline fun Int.gtu(
    other: Int,
): Boolean = this.toUInt() > other.toUInt()

inline fun Int.leu(
    other: Int,
): Boolean = this.toUInt() <= other.toUInt()

inline fun Int.ltu(
    other: Int,
): Boolean = this.toUInt() < other.toUInt()

inline fun Int.ne(
    other: Int,
): Boolean = this != other

inline fun Int.remu(
    other: Int,
): Int = this.toUInt().rem(other.toUInt()).toInt()

inline fun Int.shru(
    other: Int,
): Int = this.toUInt().shr(other).toInt()

inline fun Int.extend8s(): Int = (this and 0xFF).toByte().toInt()

inline fun Int.extend16s(): Int = (this and 0xFFFF).toShort().toInt()

inline fun Int.extendI64s(): Long = this.toLong()

inline fun Int.extendI64u(): Long = this.toUInt().toLong()

inline fun Int.convertF32s(): Float = this.toFloat()

inline fun Int.convertF32u(): Float = this.toUInt().toFloat()

inline fun Int.convertF64s(): Double = this.toDouble()

inline fun Int.convertF64u(): Double = this.toUInt().toDouble()

inline fun Int.promote(): Long = this.toLong()
