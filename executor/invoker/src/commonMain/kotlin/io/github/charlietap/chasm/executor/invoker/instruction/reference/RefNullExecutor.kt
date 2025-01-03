package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun RefNullExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefNull,
): Result<Unit, InvocationError> {
    val (stack) = context
    stack.push(ReferenceValue.Null(instruction.type))
    return Ok(Unit)
}
