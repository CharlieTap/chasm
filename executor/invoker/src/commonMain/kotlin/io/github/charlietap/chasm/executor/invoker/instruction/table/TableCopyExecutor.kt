package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.TableInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun TableCopyExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: TableInstruction.TableCopy,
): InstructionPointer {
    val srcTableInstance = instruction.srcTable // taby
    val dstTableInstance = instruction.destTable // tabx

    val elementsToCopy = vstack.popI32()
    val srcOffset = vstack.popI32()
    val dstOffset = vstack.popI32()

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
    return ip + 1
}
