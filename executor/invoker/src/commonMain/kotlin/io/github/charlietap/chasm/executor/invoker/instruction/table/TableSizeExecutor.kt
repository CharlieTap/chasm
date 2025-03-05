package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

internal inline fun TableSizeExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.TableSize,
) {
    val stack = context.vstack
    val tableInstance = instruction.table

    stack.pushI32(tableInstance.elements.size)
}
