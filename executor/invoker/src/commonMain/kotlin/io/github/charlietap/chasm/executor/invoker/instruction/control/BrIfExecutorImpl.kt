@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.invoker.ext.popI32
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal inline fun BrIfExecutorImpl(
    stack: Stack,
    instruction: ControlInstruction.BrIf,
): Result<Unit, InvocationError> =
    BrIfExecutorImpl(
        stack = stack,
        instruction = instruction,
        breakExecutor = ::BreakExecutorImpl,
    )

internal inline fun BrIfExecutorImpl(
    stack: Stack,
    instruction: ControlInstruction.BrIf,
    crossinline breakExecutor: BreakExecutor,
): Result<Unit, InvocationError> = binding {
    val shouldBreak = stack.popI32().bind() != 0

    if (shouldBreak) {
        breakExecutor(stack, instruction.labelIndex).bind()
    }
}
