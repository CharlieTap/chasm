package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal inline fun TableSizeExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.TableSize,
) {
    val stack = context.vstack

    val tableInstance = instruction.table
    val tableSize = tableInstance.elements.size

    stack.push(NumberValue.I32(tableSize))
}
