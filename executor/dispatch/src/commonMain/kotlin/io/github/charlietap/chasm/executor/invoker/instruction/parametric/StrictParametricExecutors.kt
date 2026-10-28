package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ParametricInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun SelectExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ParametricInstruction.SelectIii,
) = executeSelect(
    vstack = vstack,
    condition = instruction.condition,
    val1 = instruction.val1,
    val2 = instruction.val2,
    destinationSlot = instruction.destinationSlot,
)

internal inline fun SelectExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ParametricInstruction.SelectIis,
) = executeSelect(
    vstack = vstack,
    condition = instruction.condition,
    val1 = instruction.val1,
    val2 = vstack.getFrameSlot(instruction.val2Slot),
    destinationSlot = instruction.destinationSlot,
)

internal inline fun SelectExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ParametricInstruction.SelectIsi,
) = executeSelect(
    vstack = vstack,
    condition = instruction.condition,
    val1 = vstack.getFrameSlot(instruction.val1Slot),
    val2 = instruction.val2,
    destinationSlot = instruction.destinationSlot,
)

internal inline fun SelectExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ParametricInstruction.SelectIss,
) = executeSelect(
    vstack = vstack,
    condition = instruction.condition,
    val1 = vstack.getFrameSlot(instruction.val1Slot),
    val2 = vstack.getFrameSlot(instruction.val2Slot),
    destinationSlot = instruction.destinationSlot,
)

internal inline fun SelectExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ParametricInstruction.SelectSii,
) = executeSelect(
    vstack = vstack,
    condition = vstack.getFrameSlot(instruction.conditionSlot),
    val1 = instruction.val1,
    val2 = instruction.val2,
    destinationSlot = instruction.destinationSlot,
)

internal inline fun SelectExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ParametricInstruction.SelectSis,
) = executeSelect(
    vstack = vstack,
    condition = vstack.getFrameSlot(instruction.conditionSlot),
    val1 = instruction.val1,
    val2 = vstack.getFrameSlot(instruction.val2Slot),
    destinationSlot = instruction.destinationSlot,
)

internal inline fun SelectExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ParametricInstruction.SelectSsi,
) = executeSelect(
    vstack = vstack,
    condition = vstack.getFrameSlot(instruction.conditionSlot),
    val1 = vstack.getFrameSlot(instruction.val1Slot),
    val2 = instruction.val2,
    destinationSlot = instruction.destinationSlot,
)

internal inline fun SelectExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ParametricInstruction.SelectSss,
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
