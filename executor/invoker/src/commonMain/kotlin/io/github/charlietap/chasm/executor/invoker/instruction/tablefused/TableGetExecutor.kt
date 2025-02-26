package io.github.charlietap.chasm.executor.invoker.instruction.tablefused

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.instruction.FusedTableInstruction

internal inline fun TableGetExecutor(
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableGet,
) {
    val stack = context.vstack
    val tableInstance = instruction.table

    instruction.destination(
        tableInstance.element(
            instruction.elementIndex(stack).toInt(),
        ),
        stack,
    )
}
