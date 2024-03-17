@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun BrOnNullExecutorImpl(
    stack: Stack,
    instruction: ControlInstruction.BrOnNull,
): Result<Unit, InvocationError> =
    BrOnNullExecutorImpl(
        stack = stack,
        instruction = instruction,
        breakExecutor = ::BreakExecutorImpl,
    )

internal inline fun BrOnNullExecutorImpl(
    stack: Stack,
    instruction: ControlInstruction.BrOnNull,
    crossinline breakExecutor: BreakExecutor,
): Result<Unit, InvocationError> = binding {
    val value = stack.popReference().bind()
    val shouldBreak = value is ReferenceValue.Null

    if (shouldBreak) {
        breakExecutor(stack, instruction.labelIndex).bind()
    } else {
        stack.push(Stack.Entry.Value(value))
    }
}
