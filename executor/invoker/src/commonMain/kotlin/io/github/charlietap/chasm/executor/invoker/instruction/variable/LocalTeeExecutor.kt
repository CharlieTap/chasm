@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.variable

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.peekValue

internal typealias LocalTeeExecutor = (Stack, VariableInstruction.LocalTee) -> Result<Unit, InvocationError>

internal inline fun LocalTeeExecutor(
    stack: Stack,
    instruction: VariableInstruction.LocalTee,
): Result<Unit, InvocationError> = binding {

    val value = stack.peekValue().bind()
    val frame = stack.peekFrame().bind()

    frame.state.locals[instruction.localIdx.index()] = value.value
}
