package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.TableInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun TableSizeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableSize,
) {
    val tableInstance = instruction.table
    vstack.pushI32(tableInstance.elements.size)
}
