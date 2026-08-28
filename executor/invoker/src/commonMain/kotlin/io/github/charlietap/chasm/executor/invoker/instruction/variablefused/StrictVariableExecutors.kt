package io.github.charlietap.chasm.executor.invoker.instruction.variablefused

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.VariableSuperInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun GlobalGetExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: VariableSuperInstruction.GlobalGetS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, instruction.global.value)
}

internal inline fun GlobalSetExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: VariableSuperInstruction.GlobalSetI,
) {
    instruction.global.value = instruction.value
}

internal inline fun GlobalSetExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: VariableSuperInstruction.GlobalSetS,
) {
    instruction.global.value = vstack.getFrameSlot(instruction.sourceSlot)
}

internal inline fun LocalSetExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: VariableSuperInstruction.LocalSetI,
) {
    vstack.setFrameSlot(instruction.localSlot, instruction.value)
}

internal inline fun LocalSetExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: VariableSuperInstruction.LocalSetS,
) {
    vstack.setFrameSlot(instruction.localSlot, vstack.getFrameSlot(instruction.sourceSlot))
}
