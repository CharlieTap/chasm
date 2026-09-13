package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

// Shared scalar semantics for frame executors and generated Kotlin values.
internal inline fun valueI64Add(left: Long, right: Long): Long = left + right

internal inline fun valueI64Sub(left: Long, right: Long): Long = left - right

internal inline fun valueI64Mul(left: Long, right: Long): Long = left * right

internal inline fun valueI64And(left: Long, right: Long): Long = left and right

internal inline fun valueI64Or(left: Long, right: Long): Long = left or right

internal inline fun valueI64Xor(left: Long, right: Long): Long = left xor right

internal inline fun valueI64Shl(left: Long, right: Long): Long = left shl right.toInt()

internal inline fun valueI64ShrS(left: Long, right: Long): Long = left shr right.toInt()

internal inline fun valueI64ShrU(left: Long, right: Long): Long = left.toULong().shr(right.toInt()).toLong()

internal inline fun valueI64Rotl(left: Long, right: Long): Long = left.rotateLeft(right.toInt())

internal inline fun valueI64Rotr(left: Long, right: Long): Long = left.rotateRight(right.toInt())
