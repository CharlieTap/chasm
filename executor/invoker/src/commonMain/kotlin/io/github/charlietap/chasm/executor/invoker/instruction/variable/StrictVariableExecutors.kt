package io.github.charlietap.chasm.executor.invoker.instruction.variable

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun GlobalGetExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: VariableInstruction.GlobalGetS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, instruction.global.value)
}

internal inline fun GlobalSetExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: VariableInstruction.GlobalSetI,
) {
    instruction.global.value = instruction.value
}

internal inline fun GlobalSetExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: VariableInstruction.GlobalSetS,
) {
    instruction.global.value = vstack.getFrameSlot(instruction.sourceSlot)
}
