package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

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
    val (stack) = context
    val value = stack.popReference()
    val shouldBreak = value !is ReferenceValue.Null

    if (shouldBreak) {
        stack.push(value)
        breakExecutor(stack, instruction.labelIndex)
    }
}
