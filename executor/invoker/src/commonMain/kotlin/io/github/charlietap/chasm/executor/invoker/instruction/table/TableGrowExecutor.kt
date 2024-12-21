package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.asRange
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal inline fun TableGrowExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.TableGrow,
): Result<Unit, InvocationError> = binding {

    val (stack) = context

    val tableInstance = instruction.table
    val tableType = tableInstance.type

    val tableSize = tableInstance.elements.size
    val elementsToAdd = stack.popI32().bind()
    val referenceValue = stack.popReference().bind()

    val proposedLength = (tableSize + elementsToAdd).toUInt()
    if (proposedLength !in tableType.limits.asRange()) {
        stack.push(Stack.Entry.Value(NumberValue.I32(-1)))
        return@binding
    }

    tableInstance.apply {
        type = type.copy(
            limits = type.limits.copy(
                min = proposedLength,
            ),
        )
        elements += Array(elementsToAdd) { referenceValue }
    }

    stack.push(Stack.Entry.Value(NumberValue.I32(tableSize)))
}
