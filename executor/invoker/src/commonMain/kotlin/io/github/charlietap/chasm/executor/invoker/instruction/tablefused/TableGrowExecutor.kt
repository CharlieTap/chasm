package io.github.charlietap.chasm.executor.invoker.instruction.tablefused

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedTableInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun TableGrowExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableGrow,
) {
    val tableInstance = instruction.table
    val tableSize = tableInstance.elements.size

    val elementsToAdd = instruction.elementsToAdd(vstack).toInt()
    val referenceValue = instruction.referenceValue(vstack)

    val proposedLength = tableSize + elementsToAdd
    if (proposedLength < tableSize || proposedLength > instruction.max) {
        vstack.push(-1L)
        return
    }

    tableInstance.type.limits.min = proposedLength.toUInt()
    tableInstance.elements += LongArray(elementsToAdd) { referenceValue }
    instruction.destination(tableSize.toLong(), vstack)
}
