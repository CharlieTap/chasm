package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

// Shared scalar semantics for frame executors and generated Kotlin values.
internal inline fun valueI64Clz(operand: Long): Long = operand.countLeadingZeroBits().toLong()

internal inline fun valueI64Ctz(operand: Long): Long = operand.countTrailingZeroBits().toLong()

internal inline fun valueI64Popcnt(operand: Long): Long = operand.countOneBits().toLong()

internal inline fun valueI64Extend8S(operand: Long): Long = (operand and 0xFF).toByte().toLong()

internal inline fun valueI64Extend16S(operand: Long): Long = (operand and 0xFFFF).toShort().toLong()

internal inline fun valueI64Extend32S(operand: Long): Long = ((operand and 0xFFFFFFFFL) shl 32) shr 32
