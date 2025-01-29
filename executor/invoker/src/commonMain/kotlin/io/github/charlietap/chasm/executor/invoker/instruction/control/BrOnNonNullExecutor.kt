package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.runtime.encoder.isNullableReference
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal fun BrOnNonNullExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.BrOnNonNull,
) = BrOnNonNullExecutor(
    context = context,
    instruction = instruction,
    breakExecutor = ::BreakExecutor,
)

internal inline fun BrOnNonNullExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.BrOnNonNull,
    crossinline breakExecutor: BreakExecutor,
) {
    val stack = context.vstack
    val value = stack.pop()

    if (!value.isNullableReference()) {
        stack.push(value)
        breakExecutor(context.cstack, stack, instruction.labelIndex)
    }
}
