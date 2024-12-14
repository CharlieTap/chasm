@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.variable

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction

internal inline fun LocalGetExecutor(
    context: ExecutionContext,
    instruction: VariableInstruction.LocalGet,
): Result<Unit, InvocationError> = binding {
    val (stack) = context
    val frame = stack.peekFrame().bind()

    frame.state.locals.getOrNull(instruction.localIdx.index())?.let { local ->
        stack.push(Stack.Entry.Value(local))
    } ?: Err(InvocationError.MissingLocal).bind<Unit>()
}
