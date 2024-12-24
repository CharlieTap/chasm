package io.github.charlietap.chasm.executor.invoker.instruction.variable

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction

internal inline fun GlobalGetExecutor(
    context: ExecutionContext,
    instruction: VariableInstruction.GlobalGet,
): Result<Unit, InvocationError> = binding {
    val value = instruction.global.value
    context.stack.push(value)
}
