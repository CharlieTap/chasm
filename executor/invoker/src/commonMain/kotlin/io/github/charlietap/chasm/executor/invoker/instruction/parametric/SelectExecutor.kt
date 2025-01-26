package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ParametricInstruction

@Suppress("UNUSED_PARAMETER")
internal inline fun SelectExecutor(
    context: ExecutionContext,
    instruction: ParametricInstruction.Select,
) {
    val stack = context.vstack
    val select = stack.pop()
    val value = stack.pop()

    if (select == 0L) {
        stack.pop()
        stack.push(value)
    }
}
