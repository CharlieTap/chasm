@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.variable

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.invoker.ext.peekFrameOrError
import io.github.charlietap.chasm.executor.invoker.ext.popValueOrError
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal inline fun LocalSetExecutorImpl(
    stack: Stack,
    instruction: VariableInstruction.LocalSet,
): Result<Unit, InvocationError> = binding {

    val value = stack.popValueOrError().bind()
    val frame = stack.peekFrameOrError().bind()

    frame.state.locals[instruction.localIdx.index()] = value.value
}
