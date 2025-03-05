package io.github.charlietap.chasm.executor.invoker.instruction.tablefused

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedTableInstruction

internal fun TableCopyExecutor(
    context: ExecutionContext,
    instruction: FusedTableInstruction.TableCopy,
) {
    val stack = context.vstack
    val srcTableInstance = instruction.srcTable // taby
    val dstTableInstance = instruction.destTable // tabx

    val elementsToCopy = instruction.elementsToCopy(stack).toInt()
    val srcOffset = instruction.srcOffset(stack).toInt()
    val dstOffset = instruction.dstOffset(stack).toInt()

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
