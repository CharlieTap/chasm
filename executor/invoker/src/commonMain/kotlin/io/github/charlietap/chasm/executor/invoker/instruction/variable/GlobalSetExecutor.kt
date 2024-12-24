package io.github.charlietap.chasm.executor.invoker.instruction.variable

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction

internal inline fun GlobalSetExecutor(
    context: ExecutionContext,
    instruction: VariableInstruction.GlobalSet,
): Result<Unit, InvocationError> = binding {
    val value = context.stack.popValue().bind()
    instruction.global.value = value
}
