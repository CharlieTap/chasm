package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun I64ExtendI32SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64ExtendI32SI,
) {
    vstack.setFrameSlot(instruction.destinationSlot, instruction.operand.toLong())
}

internal inline fun I64ExtendI32SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64ExtendI32SS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, vstack.getFrameSlot(instruction.operandSlot).toInt().toLong())
}

internal inline fun I64ExtendI32UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64ExtendI32UI,
) {
    vstack.setFrameSlot(instruction.destinationSlot, instruction.operand.toUInt().toLong())
}

internal inline fun I64ExtendI32UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64ExtendI32US,
) {
    vstack.setFrameSlot(instruction.destinationSlot, vstack.getFrameSlot(instruction.operandSlot).toInt().toUInt().toLong())
}
