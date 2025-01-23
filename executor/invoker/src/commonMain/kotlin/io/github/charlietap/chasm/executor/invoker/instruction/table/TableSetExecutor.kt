package io.github.charlietap.chasm.executor.invoker.instruction.table

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction

internal inline fun TableSetExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.TableSet,
) {
    val stack = context.vstack
    val tableInstance = instruction.table

    val value = stack.popReference()
    val elementIndex = stack.popI32()

    if (elementIndex !in tableInstance.elements.indices) {
        throw InvocationException(InvocationError.Trap.TrapEncountered)
    }

    tableInstance.elements[elementIndex] = value
}
