package io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop

import io.github.charlietap.chasm.executor.invoker.ext.le
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.relationalOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I32LeSExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32LeS,
) {
    context.vstack.relationalOperation(Int::le)
}
