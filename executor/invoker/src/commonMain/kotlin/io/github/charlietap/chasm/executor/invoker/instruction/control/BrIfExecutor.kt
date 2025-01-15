package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

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
    val (stack) = context
    val shouldBreak = stack.popI32().bind() != 0

    if (shouldBreak) {
        breakExecutor(stack, instruction.labelIndex)
    }
}
