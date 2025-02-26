package io.github.charlietap.chasm.executor.invoker.instruction.tablefused

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedTableInstruction

internal inline fun TableSetExecutor(
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableSet,
) {
    val stack = context.vstack
    val tableInstance = instruction.table

    try {
        tableInstance.elements[instruction.elementIndex(stack).toInt()] = instruction.value(stack)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    }
}
