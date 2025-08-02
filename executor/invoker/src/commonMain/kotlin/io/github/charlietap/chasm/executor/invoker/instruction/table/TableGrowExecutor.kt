package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.TableInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun TableGrowExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: TableInstruction.TableGrow,
) {
    val tableInstance = instruction.table
    val tableSize = tableInstance.elements.size

    val elementsToAdd = vstack.popI32()
    val referenceValue = vstack.pop()

    val proposedLength = tableSize + elementsToAdd
    if (proposedLength < tableSize || proposedLength > instruction.max) {
        vstack.push(-1L)
        return
    }

    tableInstance.type.limits.min = proposedLength.toULong()
    tableInstance.elements += LongArray(elementsToAdd) { referenceValue }
    vstack.pushI32(tableSize)
}
