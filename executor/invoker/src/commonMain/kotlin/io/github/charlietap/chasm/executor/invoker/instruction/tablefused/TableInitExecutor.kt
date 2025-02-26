package io.github.charlietap.chasm.executor.invoker.instruction.tablefused

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedTableInstruction

internal fun TableInitExecutor(
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableInit,
) {
    val stack = context.vstack
    val tableInstance = instruction.table
    val elementInstance = instruction.element

    val elementsToInitialise = instruction.elementsToInitialise(stack).toInt()
    val segmentOffset = instruction.segmentOffset(stack).toInt()
    val tableOffset = instruction.tableOffset(stack).toInt()

    try {
        elementInstance.elements.copyInto(tableInstance.elements, tableOffset, segmentOffset, segmentOffset + elementsToInitialise)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    }
}
