@file:Suppress("NOTHING_TO_INLINE", "UNUSED_PARAMETER")

package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ParametricInstruction

@Suppress("UNUSED_PARAMETER")
internal inline fun SelectWithTypeExecutor(
    context: ExecutionContext,
    instruction: ParametricInstruction.SelectWithType,
) {
    val stack = context.vstack
    val select = stack.pop()
    val value = stack.pop()

    if (select == 0L) {
        stack.pop()
        stack.push(value)
    }
}
