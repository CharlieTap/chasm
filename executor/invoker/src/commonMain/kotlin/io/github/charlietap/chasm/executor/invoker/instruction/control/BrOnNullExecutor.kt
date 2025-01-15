package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

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
    val (stack) = context
    val value = stack.popReference().bind()
    val shouldBreak = value is ReferenceValue.Null

    if (shouldBreak) {
        breakExecutor(stack, instruction.labelIndex)
    } else {
        stack.push(value)
    }
}
