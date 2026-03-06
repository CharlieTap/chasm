package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun executeF32ToI64I(
    vstack: ValueStack,
    destinationSlot: Int,
    operand: Float,
    operation: (Float) -> Long,
) {
    vstack.setFrameSlot(destinationSlot, operation(operand))
}

internal inline fun executeF32ToI64S(
    vstack: ValueStack,
    destinationSlot: Int,
    operandSlot: Int,
    operation: (Float) -> Long,
) {
    val operand = Float.fromBits(vstack.getFrameSlot(operandSlot).toInt())
    vstack.setFrameSlot(destinationSlot, operation(operand))
}

internal inline fun executeF64ToI64I(
    vstack: ValueStack,
    destinationSlot: Int,
    operand: Double,
    operation: (Double) -> Long,
) {
    vstack.setFrameSlot(destinationSlot, operation(operand))
}

internal inline fun executeF64ToI64S(
    vstack: ValueStack,
    destinationSlot: Int,
    operandSlot: Int,
    operation: (Double) -> Long,
) {
    val operand = Double.fromBits(vstack.getFrameSlot(operandSlot))
    vstack.setFrameSlot(destinationSlot, operation(operand))
}
