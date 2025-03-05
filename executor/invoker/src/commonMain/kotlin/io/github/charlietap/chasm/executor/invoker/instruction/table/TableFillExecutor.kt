package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.TableInstruction

internal fun TableFillExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.TableFill,
) {
    val stack = context.vstack
    val tableInstance = instruction.table

    val elementsToFill = stack.popI32()
    val fillValue = stack.pop()
    val tableOffset = stack.popI32()

    try {
        tableInstance.elements.fill(fillValue, tableOffset, tableOffset + elementsToFill)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    }
}
