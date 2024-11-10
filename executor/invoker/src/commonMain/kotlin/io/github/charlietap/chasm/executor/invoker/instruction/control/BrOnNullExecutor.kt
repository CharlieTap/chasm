package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun BrOnNullExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.BrOnNull,
): Result<Unit, InvocationError> =
    BrOnNullExecutor(
        context = context,
        instruction = instruction,
        breakExecutor = ::BreakExecutor,
    )

internal inline fun BrOnNullExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.BrOnNull,
    crossinline breakExecutor: BreakExecutor,
): Result<Unit, InvocationError> = binding {
    val (stack) = context
    val value = stack.popReference().bind()
    val shouldBreak = value is ReferenceValue.Null

    if (shouldBreak) {
        breakExecutor(stack, instruction.labelIndex).bind()
    } else {
        stack.push(Stack.Entry.Value(value))
    }
}
