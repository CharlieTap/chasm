package io.github.charlietap.chasm.executor.invoker.instruction.tablefused

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedTableInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun TableInitExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableInit,
) {
    val tableInstance = instruction.table
    val elementInstance = instruction.element

    val elementsToInitialise = instruction.elementsToInitialise(vstack).toInt()
    val segmentOffset = instruction.segmentOffset(vstack).toInt()
    val tableOffset = instruction.tableOffset(vstack).toInt()

    try {
        elementInstance.elements.copyInto(tableInstance.elements, tableOffset, segmentOffset, segmentOffset + elementsToInitialise)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    }
}
