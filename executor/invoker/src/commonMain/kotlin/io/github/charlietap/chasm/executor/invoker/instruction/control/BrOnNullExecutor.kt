package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

internal fun BrOnNullExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.BrOnNull,
) = BrOnNullExecutor(
    context = context,
    instruction = instruction,
    breakExecutor = ::BreakExecutor,
)

internal inline fun BrOnNullExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.BrOnNull,
    crossinline breakExecutor: BreakExecutor,
) {
    val stack = context.vstack
    val value = stack.peek()

    if (value.isNullableReference()) {
        stack.pop()
        breakExecutor(context.cstack, stack, instruction.labelIndex)
    }
}
