package io.github.charlietap.chasm.executor.invoker.instruction.variablefused

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedVariableInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun GlobalGetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedVariableInstruction.GlobalGetS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, instruction.global.value)
}

internal inline fun GlobalSetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedVariableInstruction.GlobalSetI,
) {
    instruction.global.value = instruction.value
}

internal inline fun GlobalSetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedVariableInstruction.GlobalSetS,
) {
    instruction.global.value = vstack.getFrameSlot(instruction.sourceSlot)
}

internal inline fun LocalSetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedVariableInstruction.LocalSetI,
) {
    vstack.setFrameSlot(instruction.localSlot, instruction.value)
}

internal inline fun LocalSetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedVariableInstruction.LocalSetS,
) {
    vstack.setFrameSlot(instruction.localSlot, vstack.getFrameSlot(instruction.sourceSlot))
}
