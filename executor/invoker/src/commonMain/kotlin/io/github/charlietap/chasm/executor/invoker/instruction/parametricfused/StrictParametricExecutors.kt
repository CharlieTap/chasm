package io.github.charlietap.chasm.executor.invoker.instruction.parametricfused

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedParametricInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun SelectExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedParametricInstruction.SelectIii,
) = executeSelect(
    vstack = vstack,
    condition = instruction.condition,
    val1 = instruction.val1,
    val2 = instruction.val2,
    destinationSlot = instruction.destinationSlot,
)

internal inline fun SelectExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedParametricInstruction.SelectIis,
) = executeSelect(
    vstack = vstack,
    condition = instruction.condition,
    val1 = instruction.val1,
    val2 = vstack.getFrameSlot(instruction.val2Slot),
    destinationSlot = instruction.destinationSlot,
)

internal inline fun SelectExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedParametricInstruction.SelectIsi,
) = executeSelect(
    vstack = vstack,
    condition = instruction.condition,
    val1 = vstack.getFrameSlot(instruction.val1Slot),
    val2 = instruction.val2,
    destinationSlot = instruction.destinationSlot,
)

internal inline fun SelectExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedParametricInstruction.SelectIss,
) = executeSelect(
    vstack = vstack,
    condition = instruction.condition,
    val1 = vstack.getFrameSlot(instruction.val1Slot),
    val2 = vstack.getFrameSlot(instruction.val2Slot),
    destinationSlot = instruction.destinationSlot,
)

internal inline fun SelectExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedParametricInstruction.SelectSii,
) = executeSelect(
    vstack = vstack,
    condition = vstack.getFrameSlot(instruction.conditionSlot),
    val1 = instruction.val1,
    val2 = instruction.val2,
    destinationSlot = instruction.destinationSlot,
)

internal inline fun SelectExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedParametricInstruction.SelectSis,
) = executeSelect(
    vstack = vstack,
    condition = vstack.getFrameSlot(instruction.conditionSlot),
    val1 = instruction.val1,
    val2 = vstack.getFrameSlot(instruction.val2Slot),
    destinationSlot = instruction.destinationSlot,
)

internal inline fun SelectExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedParametricInstruction.SelectSsi,
) = executeSelect(
    vstack = vstack,
    condition = vstack.getFrameSlot(instruction.conditionSlot),
    val1 = vstack.getFrameSlot(instruction.val1Slot),
    val2 = instruction.val2,
    destinationSlot = instruction.destinationSlot,
)

internal inline fun SelectExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedParametricInstruction.SelectSss,
) = executeSelect(
    vstack = vstack,
    condition = vstack.getFrameSlot(instruction.conditionSlot),
    val1 = vstack.getFrameSlot(instruction.val1Slot),
    val2 = vstack.getFrameSlot(instruction.val2Slot),
    destinationSlot = instruction.destinationSlot,
)

private inline fun executeSelect(
    vstack: ValueStack,
    condition: Long,
    val1: Long,
    val2: Long,
    destinationSlot: Int,
) {
    if (condition == 0L) {
        vstack.setFrameSlot(destinationSlot, val2)
    } else {
        vstack.setFrameSlot(destinationSlot, val1)
    }
}
