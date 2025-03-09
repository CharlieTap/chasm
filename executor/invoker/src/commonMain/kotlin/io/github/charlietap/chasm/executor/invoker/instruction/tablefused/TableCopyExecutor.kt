package io.github.charlietap.chasm.executor.invoker.instruction.tablefused

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedTableInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun TableCopyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableCopy,
) {
    val srcTableInstance = instruction.srcTable // taby
    val dstTableInstance = instruction.destTable // tabx

    val elementsToCopy = instruction.elementsToCopy(vstack).toInt()
    val srcOffset = instruction.srcOffset(vstack).toInt()
    val dstOffset = instruction.dstOffset(vstack).toInt()

    try {
        srcTableInstance.elements.copyInto(
            destination = dstTableInstance.elements,
            destinationOffset = dstOffset,
            startIndex = srcOffset,
            endIndex = srcOffset + elementsToCopy,
        )
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    }
}
