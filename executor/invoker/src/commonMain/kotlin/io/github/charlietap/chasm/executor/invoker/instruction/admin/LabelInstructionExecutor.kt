package io.github.charlietap.chasm.executor.invoker.instruction.admin

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popLabel

internal inline fun LabelInstructionExecutor(
    context: ExecutionContext,
): Result<Unit, InvocationError> = binding {
    context.stack.popLabel().bind()
}
