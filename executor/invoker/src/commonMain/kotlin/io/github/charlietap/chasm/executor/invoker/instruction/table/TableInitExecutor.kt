package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.TableInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal fun TableInitExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: TableInstruction.TableInit,
) {
    val tableInstance = instruction.table
    val elementInstance = instruction.element

    val elementsToInitialise = vstack.popI32()
    val segmentOffset = vstack.popI32()
    val tableOffset = vstack.popI32()

    try {
        elementInstance.elements.copyInto(tableInstance.elements, tableOffset, segmentOffset, segmentOffset + elementsToInitialise)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    }
}
