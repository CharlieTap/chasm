package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popValue

@Suppress("UNUSED_PARAMETER")
internal inline fun DropExecutor(
    context: ExecutionContext,
    instruction: ParametricInstruction.Drop,
): Result<Unit, InvocationError> = binding {
    context.stack.popValue().bind()
}
