@file:Suppress("NOTHING_TO_INLINE", "UNUSED_PARAMETER")

package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.ParametricInstruction

@Suppress("UNUSED_PARAMETER")
internal inline fun SelectWithTypeExecutor(
    context: ExecutionContext,
    instruction: ParametricInstruction.SelectWithType,
) {
    val (stack) = context
    val select = stack.popI32()

    val value2 = stack.popValue()

    if (select == 0) {
        stack.popValue()
        stack.push(value2)
    }
}
