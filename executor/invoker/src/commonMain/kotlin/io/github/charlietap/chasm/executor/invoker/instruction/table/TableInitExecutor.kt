package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction

internal fun TableInitExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.TableInit,
) {
    val stack = context.vstack
    val tableInstance = instruction.table
    val elementInstance = instruction.element

    val elementsToInitialise = stack.popI32()
    val segmentOffset = stack.popI32()
    val tableOffset = stack.popI32()

    try {
        elementInstance.elements.copyInto(tableInstance.elements, tableOffset, segmentOffset, segmentOffset + elementsToInitialise)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    }
}
