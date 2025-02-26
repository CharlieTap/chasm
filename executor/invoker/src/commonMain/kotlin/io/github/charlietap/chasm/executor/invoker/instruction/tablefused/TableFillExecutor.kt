package io.github.charlietap.chasm.executor.invoker.instruction.tablefused

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedTableInstruction

internal fun TableFillExecutor(
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableFill,
) {
    val stack = context.vstack
    val tableInstance = instruction.table

    val elementsToFill = instruction.elementsToFill(stack).toInt()
    val fillValue = instruction.fillValue(stack)
    val tableOffset = instruction.tableOffset(stack).toInt()

    try {
        tableInstance.elements.fill(fillValue, tableOffset, tableOffset + elementsToFill)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    }
}
