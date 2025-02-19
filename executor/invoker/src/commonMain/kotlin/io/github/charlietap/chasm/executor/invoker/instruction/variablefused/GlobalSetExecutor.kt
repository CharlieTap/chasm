package io.github.charlietap.chasm.executor.invoker.instruction.variablefused

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedVariableInstruction

internal inline fun GlobalSetExecutor(
    context: ExecutionContext,
    instruction: FusedVariableInstruction.GlobalSet,
) {
    instruction.global.value = instruction.operand(context.vstack)
}
