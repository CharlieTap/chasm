package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction

internal inline fun TableGrowExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.TableGrow,
) {
    val stack = context.vstack
    val tableInstance = instruction.table
    val tableSize = tableInstance.elements.size

    val elementsToAdd = stack.popI32()
    val referenceValue = stack.pop()

    val proposedLength = tableSize + elementsToAdd
    if (proposedLength < tableSize || proposedLength > instruction.max) {
        stack.push(-1L)
        return
    }

    tableInstance.type.limits.min = proposedLength.toUInt()
    tableInstance.elements += LongArray(elementsToAdd) { referenceValue }
    stack.pushI32(tableSize)
}
