package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun executeI64UnaryI(
    vstack: ValueStack,
    destinationSlot: Int,
    operand: Long,
    operation: (Long) -> Long,
) {
    vstack.setFrameSlot(destinationSlot, operation(operand))
}

internal inline fun executeI64UnaryS(
    vstack: ValueStack,
    destinationSlot: Int,
    operandSlot: Int,
    operation: (Long) -> Long,
) {
    val operand = vstack.getFrameSlot(operandSlot)
    vstack.setFrameSlot(destinationSlot, operation(operand))
}
