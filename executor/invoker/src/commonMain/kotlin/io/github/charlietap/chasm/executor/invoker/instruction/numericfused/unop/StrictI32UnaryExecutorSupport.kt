package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun executeI32UnaryI(
    vstack: ValueStack,
    destinationSlot: Int,
    operand: Int,
    operation: (Int) -> Int,
) {
    vstack.setFrameSlot(destinationSlot, operation(operand).toLong())
}

internal inline fun executeI32UnaryS(
    vstack: ValueStack,
    destinationSlot: Int,
    operandSlot: Int,
    operation: (Int) -> Int,
) {
    val operand = vstack.getFrameSlot(operandSlot).toInt()
    vstack.setFrameSlot(destinationSlot, operation(operand).toLong())
}
