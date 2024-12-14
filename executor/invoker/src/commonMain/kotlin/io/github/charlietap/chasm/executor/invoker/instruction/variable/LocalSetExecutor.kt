@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.variable

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction

internal inline fun LocalSetExecutor(
    context: ExecutionContext,
    instruction: VariableInstruction.LocalSet,
): Result<Unit, InvocationError> = binding {

    val (stack) = context

    val value = stack.popValue().bind()
    val frame = stack.peekFrame().bind()

    frame.state.locals[instruction.localIdx.index()] = value.value
}
