package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.asRange
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction

internal inline fun TableGrowExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.TableGrow,
) {
    val stack = context.vstack
    val tableInstance = instruction.table
    val tableType = tableInstance.type
    val tableSize = tableInstance.elements.size

    val elementsToAdd = stack.popI32()
    val referenceValue = stack.pop()

    val proposedLength = (tableSize + elementsToAdd).toUInt()
    if (proposedLength !in tableType.limits.asRange()) {
        stack.push(-1L)
        return
    }

    tableInstance.apply {
        type = type.copy(
            limits = type.limits.copy(
                min = proposedLength,
            ),
        )
        elements += LongArray(elementsToAdd) { referenceValue }
    }

    stack.pushI32(tableSize)
}
