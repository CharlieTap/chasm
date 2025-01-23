package io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop

import io.github.charlietap.chasm.executor.invoker.ext.leu
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.relationalOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I32LeUExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32LeU,
) {
    context.vstack.relationalOperation(Int::leu)
}
