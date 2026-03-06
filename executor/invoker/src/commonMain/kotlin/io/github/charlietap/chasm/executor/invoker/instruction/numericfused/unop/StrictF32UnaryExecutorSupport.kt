package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun executeF32UnaryI(
    vstack: ValueStack,
    destinationSlot: Int,
    operand: Float,
    operation: (Float) -> Float,
) {
    vstack.setFrameSlot(destinationSlot, operation(operand).toRawBits().toLong())
}

internal inline fun executeF32UnaryS(
    vstack: ValueStack,
    destinationSlot: Int,
    operandSlot: Int,
    operation: (Float) -> Float,
) {
    val operand = Float.fromBits(vstack.getFrameSlot(operandSlot).toInt())
    vstack.setFrameSlot(destinationSlot, operation(operand).toRawBits().toLong())
}
