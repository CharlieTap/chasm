package io.github.charlietap.chasm.executor.invoker.instruction.tablefused

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedTableInstruction

internal inline fun TableGrowExecutor(
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableGrow,
) {
    val stack = context.vstack
    val tableInstance = instruction.table
    val tableSize = tableInstance.elements.size

    val elementsToAdd = instruction.elementsToAdd(stack).toInt()
    val referenceValue = instruction.referenceValue(stack)

    val proposedLength = tableSize + elementsToAdd
    if (proposedLength < tableSize || proposedLength > instruction.max) {
        stack.push(-1L)
        return
    }

    tableInstance.type.limits.min = proposedLength.toUInt()
    tableInstance.elements += LongArray(elementsToAdd) { referenceValue }
    instruction.destination(tableSize.toLong(), stack)
}
