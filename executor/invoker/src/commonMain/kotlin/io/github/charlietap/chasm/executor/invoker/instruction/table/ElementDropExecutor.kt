package io.github.charlietap.chasm.executor.invoker.instruction.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.TableInstruction

internal inline fun ElementDropExecutor(
    context: ExecutionContext,
    instruction: TableInstruction.ElemDrop,
): Result<Unit, InvocationError> = binding {
    instruction.element.elements = emptyArray()
}
