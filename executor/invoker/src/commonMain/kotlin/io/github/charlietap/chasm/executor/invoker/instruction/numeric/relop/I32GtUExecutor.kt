package io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop

import io.github.charlietap.chasm.executor.invoker.ext.gtu
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.relationalOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I32GtUExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32GtU,
) {
    context.vstack.relationalOperation(Int::gtu)
}
