package io.github.charlietap.chasm.executor.invoker.instruction.tablefused

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedTableInstruction

internal inline fun TableSizeExecutor(
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableSize,
) {
    val stack = context.vstack
    val tableInstance = instruction.table

    instruction.destination(tableInstance.elements.size.toLong(), stack)
}
