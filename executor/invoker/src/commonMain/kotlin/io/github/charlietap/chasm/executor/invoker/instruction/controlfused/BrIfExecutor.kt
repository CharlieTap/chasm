package io.github.charlietap.chasm.executor.invoker.instruction.controlfused

import io.github.charlietap.chasm.executor.invoker.instruction.control.BreakExecutor
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedControlInstruction

internal fun BrIfExecutor(
    context: ExecutionContext,
    instruction: FusedControlInstruction.BrIf,
) = BrIfExecutor(
    context = context,
    instruction = instruction,
    breakExecutor = ::BreakExecutor,
)

internal inline fun BrIfExecutor(
    context: ExecutionContext,
    instruction: FusedControlInstruction.BrIf,
    crossinline breakExecutor: BreakExecutor,
) {
    val stack = context.vstack
    val shouldBreak = instruction.operand(stack) != 0L

    if (shouldBreak) {
        breakExecutor(context.cstack, stack, instruction.labelIndex)
    }
}
