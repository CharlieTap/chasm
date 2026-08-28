package io.github.charlietap.chasm.executor.invoker.instruction.numeric.constant

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun I32ConstExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I32ConstS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, instruction.value.toLong())
}

internal inline fun I64ConstExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.I64ConstS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, instruction.value)
}

internal inline fun F32ConstExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F32ConstS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, instruction.bits.toLong())
}

internal inline fun F64ConstExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64ConstS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, instruction.bits)
}
