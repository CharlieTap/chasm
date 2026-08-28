package io.github.charlietap.chasm.executor.invoker.instruction.variable

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun LocalTeeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: VariableInstruction.LocalTee,
) {
    vstack.setLocal(
        instruction.localIdx,
        vstack.peek(),
    )
}
