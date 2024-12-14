package io.github.charlietap.chasm.executor.invoker.instruction.variable

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.peekValue
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction

internal inline fun LocalTeeExecutor(
    context: ExecutionContext,
    instruction: VariableInstruction.LocalTee,
): Result<Unit, InvocationError> = binding {

    val (stack) = context

    val value = stack.peekValue().bind()
    val frame = stack.peekFrame().bind()

    frame.state.locals[instruction.localIdx.index()] = value.value
}
