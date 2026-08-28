@file:Suppress("NOTHING_TO_INLINE", "UNUSED_PARAMETER")

package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ParametricInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

@Suppress("UNUSED_PARAMETER")
internal inline fun SelectWithTypeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ParametricInstruction.SelectWithType,
) {
    val select = vstack.pop()
    val value = vstack.pop()

    if (select == 0L) {
        vstack.pop()
        vstack.push(value)
    }
}
