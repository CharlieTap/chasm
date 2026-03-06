package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun executeI32ToF32I(
    vstack: ValueStack,
    destinationSlot: Int,
    operand: Int,
    operation: (Int) -> Float,
) {
    vstack.setFrameSlot(destinationSlot, operation(operand).toRawBits().toLong())
}

internal inline fun executeI32ToF32S(
    vstack: ValueStack,
    destinationSlot: Int,
    operandSlot: Int,
    operation: (Int) -> Float,
) {
    val operand = vstack.getFrameSlot(operandSlot).toInt()
    vstack.setFrameSlot(destinationSlot, operation(operand).toRawBits().toLong())
}

internal inline fun executeI64ToF32I(
    vstack: ValueStack,
    destinationSlot: Int,
    operand: Long,
    operation: (Long) -> Float,
) {
    vstack.setFrameSlot(destinationSlot, operation(operand).toRawBits().toLong())
}

internal inline fun executeI64ToF32S(
    vstack: ValueStack,
    destinationSlot: Int,
    operandSlot: Int,
    operation: (Long) -> Float,
) {
    val operand = vstack.getFrameSlot(operandSlot)
    vstack.setFrameSlot(destinationSlot, operation(operand).toRawBits().toLong())
}

internal inline fun executeF64ToF32I(
    vstack: ValueStack,
    destinationSlot: Int,
    operand: Double,
    operation: (Double) -> Float,
) {
    vstack.setFrameSlot(destinationSlot, operation(operand).toRawBits().toLong())
}

internal inline fun executeF64ToF32S(
    vstack: ValueStack,
    destinationSlot: Int,
    operandSlot: Int,
    operation: (Double) -> Float,
) {
    val operand = Double.fromBits(vstack.getFrameSlot(operandSlot))
    vstack.setFrameSlot(destinationSlot, operation(operand).toRawBits().toLong())
}

internal inline fun executeI32BitsToF32I(
    vstack: ValueStack,
    destinationSlot: Int,
    operand: Int,
) {
    vstack.setFrameSlot(destinationSlot, operand.toLong())
}

internal inline fun executeI32BitsToF32S(
    vstack: ValueStack,
    destinationSlot: Int,
    operandSlot: Int,
) {
    vstack.setFrameSlot(destinationSlot, vstack.getFrameSlot(operandSlot))
}
