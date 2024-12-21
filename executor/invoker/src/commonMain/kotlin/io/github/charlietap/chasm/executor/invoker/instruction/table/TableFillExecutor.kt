package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.contains
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction

internal fun TableFillExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.TableFill,
): Result<Unit, InvocationError> = binding {

    val (stack) = context
    val tableInstance = instruction.table

    val elementsToFill = stack.popI32().bind()
    val fillValue = stack.popReference().bind()
    val tableOffset = stack.popI32().bind()

    val fillRange = tableOffset..<(tableOffset + elementsToFill)

    if (!tableInstance.elements.indices.contains(fillRange)) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    if (elementsToFill == 0) return@binding

    tableInstance.elements.fill(fillValue, fillRange.first, fillRange.last + 1)
}
