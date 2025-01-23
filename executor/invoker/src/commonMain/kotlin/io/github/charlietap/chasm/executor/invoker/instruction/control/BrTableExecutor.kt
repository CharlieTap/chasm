package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal fun BrTableExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.BrTable,
) = BrTableExecutor(
    context = context,
    instruction = instruction,
    breakExecutor = ::BreakExecutor,
)

internal inline fun BrTableExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.BrTable,
    crossinline breakExecutor: BreakExecutor,
) {
    val stack = context.vstack
    val index = stack.popI32()

    val label = if (index >= 0 && index < instruction.labelIndices.size) {
        instruction.labelIndices[index]
    } else {
        instruction.defaultLabelIndex
    }

    breakExecutor(context.cstack, stack, label)
}
