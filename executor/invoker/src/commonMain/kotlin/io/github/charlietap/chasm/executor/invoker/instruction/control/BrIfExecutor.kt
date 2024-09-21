@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popI32

internal inline fun BrIfExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.BrIf,
): Result<Unit, InvocationError> =
    BrIfExecutor(
        context = context,
        instruction = instruction,
        breakExecutor = ::BreakExecutor,
    )

internal inline fun BrIfExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.BrIf,
    crossinline breakExecutor: BreakExecutor,
): Result<Unit, InvocationError> = binding {
    val (stack) = context
    val shouldBreak = stack.popI32().bind() != 0

    if (shouldBreak) {
        breakExecutor(stack, instruction.labelIndex).bind()
    }
}
