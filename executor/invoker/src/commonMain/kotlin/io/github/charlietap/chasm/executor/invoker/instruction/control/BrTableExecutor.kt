@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popI32

internal inline fun BrTableExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.BrTable,
): Result<Unit, InvocationError> =
    BrTableExecutor(
        context = context,
        instruction = instruction,
        breakExecutor = ::BreakExecutor,
    )

internal inline fun BrTableExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.BrTable,
    crossinline breakExecutor: BreakExecutor,
): Result<Unit, InvocationError> = binding {
    val (stack) = context
    val index = stack.popI32().bind()

    val label = if (index >= 0 && index < instruction.labelIndices.size) {
        instruction.labelIndices[index]
    } else {
        instruction.defaultLabelIndex
    }

    breakExecutor(stack, label).bind()
}
