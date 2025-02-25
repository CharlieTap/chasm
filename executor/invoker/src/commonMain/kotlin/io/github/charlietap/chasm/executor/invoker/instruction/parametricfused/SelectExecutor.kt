package io.github.charlietap.chasm.executor.invoker.instruction.parametricfused

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedParametricInstruction

internal inline fun SelectExecutor(
    context: ExecutionContext,
    instruction: FusedParametricInstruction.Select,
) {
    val stack = context.vstack
    val select = instruction.const(stack)
    val val2 = instruction.val2(stack)
    val val1 = instruction.val1(stack)

    if (select == 0L) {
        instruction.destination(val2, stack)
    } else {
        instruction.destination(val1, stack)
    }
}
