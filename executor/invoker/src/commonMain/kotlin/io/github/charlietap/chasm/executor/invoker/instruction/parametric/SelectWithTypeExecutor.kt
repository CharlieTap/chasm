@file:Suppress("NOTHING_TO_INLINE", "UNUSED_PARAMETER")

package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.instruction.ParametricInstruction

@Suppress("UNUSED_PARAMETER")
internal inline fun SelectWithTypeExecutor(
    context: ExecutionContext,
    instruction: ParametricInstruction.SelectWithType,
) {
    val (stack) = context
    val select = stack.popI32().bind()

    val value2 = stack.popValue().bind()

    if (select == 0) {
        stack.popValue().bind()
        stack.push(value2)
    }
}
