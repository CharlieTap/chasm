package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

internal inline fun TableSetExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.TableSet,
) {
    val stack = context.vstack
    val tableInstance = instruction.table

    val value = stack.pop()
    val elementIndex = stack.popI32()

    try {
        tableInstance.elements[elementIndex] = value
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    }
}
