package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.asRange
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal inline fun TableGrowExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.TableGrow,
) {

    val stack = context.vstack

    val tableInstance = instruction.table
    val tableType = tableInstance.type

    val tableSize = tableInstance.elements.size
    val elementsToAdd = stack.popI32()
    val referenceValue = stack.popReference()

    val proposedLength = (tableSize + elementsToAdd).toUInt()
    if (proposedLength !in tableType.limits.asRange()) {
        stack.push(NumberValue.I32(-1))
        return
    }

    tableInstance.apply {
        type = type.copy(
            limits = type.limits.copy(
                min = proposedLength,
            ),
        )
        elements += Array(elementsToAdd) { referenceValue }
    }

    stack.push(NumberValue.I32(tableSize))
}
