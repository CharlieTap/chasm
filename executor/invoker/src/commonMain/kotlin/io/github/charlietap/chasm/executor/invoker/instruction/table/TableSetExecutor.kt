package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.TableInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun TableSetExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableSet,
) {
    val tableInstance = instruction.table
    val value = vstack.pop()
    val elementIndex = vstack.popI32()

    try {
        tableInstance.elements[elementIndex] = value
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    }
}
