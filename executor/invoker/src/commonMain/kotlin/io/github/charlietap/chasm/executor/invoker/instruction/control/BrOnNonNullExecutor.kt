package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun BrOnNonNullExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.BrOnNonNull,
): Result<Unit, InvocationError> =
    BrOnNonNullExecutor(
        context = context,
        instruction = instruction,
        breakExecutor = ::BreakExecutor,
    )

internal inline fun BrOnNonNullExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.BrOnNonNull,
    crossinline breakExecutor: BreakExecutor,
): Result<Unit, InvocationError> = binding {
    val (stack) = context
    val value = stack.popReference().bind()
    val shouldBreak = value !is ReferenceValue.Null

    if (shouldBreak) {
        stack.push(Stack.Entry.Value(value))
        breakExecutor(stack, instruction.labelIndex).bind()
    }
}
