package io.github.charlietap.chasm.executor.invoker.instruction.variablefused

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedVariableInstruction

internal inline fun LocalTeeExecutor(
    context: ExecutionContext,
    instruction: FusedVariableInstruction.LocalTee,
) {
    val stack = context.vstack
    val value = instruction.operand(stack)

    stack.setLocal(
        instruction.localIdx,
        value,
    )
    stack.push(value)
}
