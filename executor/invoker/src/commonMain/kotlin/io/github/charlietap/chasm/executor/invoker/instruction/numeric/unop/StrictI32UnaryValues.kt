package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

// Shared scalar semantics for frame executors and generated Kotlin values.
internal inline fun valueI32Clz(operand: Int): Int = operand.countLeadingZeroBits()

internal inline fun valueI32Ctz(operand: Int): Int = operand.countTrailingZeroBits()

internal inline fun valueI32Popcnt(operand: Int): Int = operand.countOneBits()

internal inline fun valueI32Extend8S(operand: Int): Int = (operand and 0xFF).toByte().toInt()

internal inline fun valueI32Extend16S(operand: Int): Int = (operand and 0xFFFF).toShort().toInt()
