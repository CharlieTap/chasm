package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun executeI32ToF64I(
    vstack: ValueStack,
    destinationSlot: Int,
    operand: Int,
    operation: (Int) -> Double,
) {
    vstack.setFrameSlot(destinationSlot, operation(operand).toRawBits())
}

internal inline fun executeI32ToF64S(
    vstack: ValueStack,
    destinationSlot: Int,
    operandSlot: Int,
    operation: (Int) -> Double,
) {
    val operand = vstack.getFrameSlot(operandSlot).toInt()
    vstack.setFrameSlot(destinationSlot, operation(operand).toRawBits())
}

internal inline fun executeI64ToF64I(
    vstack: ValueStack,
    destinationSlot: Int,
    operand: Long,
    operation: (Long) -> Double,
) {
    vstack.setFrameSlot(destinationSlot, operation(operand).toRawBits())
}

internal inline fun executeI64ToF64S(
    vstack: ValueStack,
    destinationSlot: Int,
    operandSlot: Int,
    operation: (Long) -> Double,
) {
    val operand = vstack.getFrameSlot(operandSlot)
    vstack.setFrameSlot(destinationSlot, operation(operand).toRawBits())
}

internal inline fun executeF32ToF64I(
    vstack: ValueStack,
    destinationSlot: Int,
    operand: Float,
    operation: (Float) -> Double,
) {
    vstack.setFrameSlot(destinationSlot, operation(operand).toRawBits())
}

internal inline fun executeF32ToF64S(
    vstack: ValueStack,
    destinationSlot: Int,
    operandSlot: Int,
    operation: (Float) -> Double,
) {
    val operand = Float.fromBits(vstack.getFrameSlot(operandSlot).toInt())
    vstack.setFrameSlot(destinationSlot, operation(operand).toRawBits())
}

internal inline fun executeI64BitsToF64I(
    vstack: ValueStack,
    destinationSlot: Int,
    operand: Long,
) {
    vstack.setFrameSlot(destinationSlot, operand)
}

internal inline fun executeI64BitsToF64S(
    vstack: ValueStack,
    destinationSlot: Int,
    operandSlot: Int,
) {
    vstack.setFrameSlot(destinationSlot, vstack.getFrameSlot(operandSlot))
}
