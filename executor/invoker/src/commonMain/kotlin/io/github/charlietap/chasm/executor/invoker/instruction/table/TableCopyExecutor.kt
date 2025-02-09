package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction

internal fun TableCopyExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.TableCopy,
) {
    val stack = context.vstack
    val srcTableInstance = instruction.srcTable // taby
    val dstTableInstance = instruction.destTable // tabx

    val elementsToCopy = stack.popI32()
    val srcOffset = stack.popI32()
    val dstOffset = stack.popI32()

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
