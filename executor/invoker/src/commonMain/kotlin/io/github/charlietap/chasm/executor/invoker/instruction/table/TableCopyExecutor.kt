package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Err
import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction

internal fun TableCopyExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.TableCopy,
) {

    val (stack) = context

    val srcTableInstance = instruction.srcTable // taby
    val dstTableInstance = instruction.destTable // tabx

    val elementsToCopy = stack.popI32().bind()
    val srcOffset = stack.popI32().bind()
    val dstOffset = stack.popI32().bind()

    if (elementsToCopy < 0 ||
        srcOffset < 0 ||
        dstOffset < 0 ||
        srcOffset + elementsToCopy > srcTableInstance.elements.size ||
        dstOffset + elementsToCopy > dstTableInstance.elements.size
    ) {
        Err(InvocationError.Trap.TrapEncountered).bind()
    }

    srcTableInstance.elements.copyInto(
        destination = dstTableInstance.elements,
        destinationOffset = dstOffset,
        startIndex = srcOffset,
        endIndex = srcOffset + elementsToCopy,
    )
}
