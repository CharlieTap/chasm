package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun executeF64UnaryI(
    vstack: ValueStack,
    destinationSlot: Int,
    operand: Double,
    operation: (Double) -> Double,
) {
    vstack.setFrameSlot(destinationSlot, operation(operand).toRawBits())
}

internal inline fun executeF64UnaryS(
    vstack: ValueStack,
    destinationSlot: Int,
    operandSlot: Int,
    operation: (Double) -> Double,
) {
    val operand = Double.fromBits(vstack.getFrameSlot(operandSlot))
    vstack.setFrameSlot(destinationSlot, operation(operand).toRawBits())
}
