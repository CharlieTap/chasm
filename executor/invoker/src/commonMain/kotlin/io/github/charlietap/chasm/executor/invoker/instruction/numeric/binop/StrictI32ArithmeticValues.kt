package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

// Shared scalar semantics for frame executors and generated Kotlin values.
internal inline fun valueI32Add(left: Int, right: Int): Int = left + right

internal inline fun valueI32Sub(left: Int, right: Int): Int = left - right

internal inline fun valueI32Mul(left: Int, right: Int): Int = left * right

internal inline fun valueI32And(left: Int, right: Int): Int = left and right

internal inline fun valueI32Or(left: Int, right: Int): Int = left or right

internal inline fun valueI32Xor(left: Int, right: Int): Int = left xor right

internal inline fun valueI32Shl(left: Int, right: Int): Int = left shl right

internal inline fun valueI32ShrS(left: Int, right: Int): Int = left shr right

internal inline fun valueI32ShrU(left: Int, right: Int): Int = left.toUInt().shr(right).toInt()

internal inline fun valueI32Rotl(left: Int, right: Int): Int = left.rotateLeft(right)

internal inline fun valueI32Rotr(left: Int, right: Int): Int = left.rotateRight(right)
