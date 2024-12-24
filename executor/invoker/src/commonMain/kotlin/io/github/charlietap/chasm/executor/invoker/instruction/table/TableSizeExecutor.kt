package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal inline fun TableSizeExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.TableSize,
): Result<Unit, InvocationError> = binding {

    val (stack) = context

    val tableInstance = instruction.table
    val tableSize = tableInstance.elements.size

    stack.push(NumberValue.I32(tableSize))
}
