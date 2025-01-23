package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
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

    if (elementsToCopy < 0 ||
        srcOffset < 0 ||
        dstOffset < 0 ||
        srcOffset + elementsToCopy > srcTableInstance.elements.size ||
        dstOffset + elementsToCopy > dstTableInstance.elements.size
    ) {
        throw InvocationException(InvocationError.Trap.TrapEncountered)
    }

    srcTableInstance.elements.copyInto(
        destination = dstTableInstance.elements,
        destinationOffset = dstOffset,
        startIndex = srcOffset,
        endIndex = srcOffset + elementsToCopy,
    )
}
