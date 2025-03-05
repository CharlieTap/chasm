package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

internal fun BrIfExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.BrIf,
) = BrIfExecutor(
    context = context,
    instruction = instruction,
    breakExecutor = ::BreakExecutor,
)

internal inline fun BrIfExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.BrIf,
    crossinline breakExecutor: BreakExecutor,
) {
    val stack = context.vstack
    val shouldBreak = stack.pop() != 0L

    if (shouldBreak) {
        breakExecutor(context.cstack, stack, instruction.labelIndex)
    }
}
