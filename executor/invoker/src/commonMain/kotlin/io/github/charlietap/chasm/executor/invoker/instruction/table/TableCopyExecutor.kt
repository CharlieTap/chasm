package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction

internal fun TableCopyExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.TableCopy,
): Result<Unit, InvocationError> = binding {

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
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    val step = if (dstOffset <= srcOffset) 1 else -1
    val start = if (dstOffset <= srcOffset) 0 else elementsToCopy - 1

    repeat(elementsToCopy) { i ->
        val index = start + i * step
        dstTableInstance.elements[dstOffset + index] = srcTableInstance.elements[srcOffset + index]
    }
}
