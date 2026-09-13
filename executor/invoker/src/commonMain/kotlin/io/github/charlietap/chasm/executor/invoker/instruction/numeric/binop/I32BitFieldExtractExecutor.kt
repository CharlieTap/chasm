package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun I32BitFieldExtractExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32BitFieldExtractS,
) {
    val value = (vstack.getFrameSlot(instruction.operandSlot).toInt() ushr instruction.shift) and instruction.mask
    vstack.setFrameSlot(instruction.destinationSlot, value.toLong())
}
