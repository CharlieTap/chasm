package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction

internal inline fun TableSetExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.TableSet,
): Result<Unit, InvocationError> = binding {

    val (stack) = context
    val tableInstance = instruction.table

    val value = stack.popReference().bind()
    val elementIndex = stack.popI32().bind()

    if (elementIndex !in tableInstance.elements.indices) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    tableInstance.elements[elementIndex] = value
}
