package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun executeF32ToI32I(
    vstack: ValueStack,
    destinationSlot: Int,
    operand: Float,
    operation: (Float) -> Int,
) {
    vstack.setFrameSlot(destinationSlot, operation(operand).toLong())
}

internal inline fun executeF32ToI32S(
    vstack: ValueStack,
    destinationSlot: Int,
    operandSlot: Int,
    operation: (Float) -> Int,
) {
    val operand = Float.fromBits(vstack.getFrameSlot(operandSlot).toInt())
    vstack.setFrameSlot(destinationSlot, operation(operand).toLong())
}

internal inline fun executeF64ToI32I(
    vstack: ValueStack,
    destinationSlot: Int,
    operand: Double,
    operation: (Double) -> Int,
) {
    vstack.setFrameSlot(destinationSlot, operation(operand).toLong())
}

internal inline fun executeF64ToI32S(
    vstack: ValueStack,
    destinationSlot: Int,
    operandSlot: Int,
    operation: (Double) -> Int,
) {
    val operand = Double.fromBits(vstack.getFrameSlot(operandSlot))
    vstack.setFrameSlot(destinationSlot, operation(operand).toLong())
}

internal inline fun executeI64ToI32I(
    vstack: ValueStack,
    destinationSlot: Int,
    operand: Long,
    operation: (Long) -> Int,
) {
    vstack.setFrameSlot(destinationSlot, operation(operand).toLong())
}

internal inline fun executeI64ToI32S(
    vstack: ValueStack,
    destinationSlot: Int,
    operandSlot: Int,
    operation: (Long) -> Int,
) {
    val operand = vstack.getFrameSlot(operandSlot)
    vstack.setFrameSlot(destinationSlot, operation(operand).toLong())
}
