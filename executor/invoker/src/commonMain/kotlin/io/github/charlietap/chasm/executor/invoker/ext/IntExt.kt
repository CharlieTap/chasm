@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.ext

internal inline fun Int.divu(other: Int): Int = this.toUInt().div(other.toUInt()).toInt()

internal inline fun Int.eq(other: Int): Boolean = this == other

internal inline fun Int.eqz(): Boolean = this == 0

internal inline fun Int.ge(other: Int): Boolean = this >= other

internal inline fun Int.gt(other: Int): Boolean = this > other

internal inline fun Int.le(other: Int): Boolean = this <= other

internal inline fun Int.lt(other: Int): Boolean = this < other

internal inline fun Int.geu(other: Int): Boolean = this.toUInt() >= other.toUInt()

internal inline fun Int.gtu(other: Int): Boolean = this.toUInt() > other.toUInt()

internal inline fun Int.leu(other: Int): Boolean = this.toUInt() <= other.toUInt()

internal inline fun Int.ltu(other: Int): Boolean = this.toUInt() < other.toUInt()

internal inline fun Int.ne(other: Int): Boolean = this != other

internal inline fun Int.remu(other: Int): Int = this.toUInt().rem(other.toUInt()).toInt()

internal inline fun Int.shru(other: Int): Int = this.toUInt().shr(other).toInt()

internal inline fun Int.extend8s(): Int = (this and 0xFF).toByte().toInt()

internal inline fun Int.extend16s(): Int = (this and 0xFFFF).toShort().toInt()

internal inline fun Int.extendI64s(): Long = this.toLong()

internal inline fun Int.extendI64u(): Long = this.toUInt().toLong()

internal inline fun Int.convertF32s(): Float = this.toFloat()

internal inline fun Int.convertF32u(): Float = this.toUInt().toFloat()

internal inline fun Int.convertF64s(): Double = this.toDouble()

internal inline fun Int.convertF64u(): Double = this.toUInt().toDouble()

internal inline fun Int.promote(): Long = this.toLong()
