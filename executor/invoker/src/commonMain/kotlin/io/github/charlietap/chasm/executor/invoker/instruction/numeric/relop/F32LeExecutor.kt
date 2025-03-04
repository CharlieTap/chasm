package io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop

import io.github.charlietap.chasm.executor.invoker.ext.le
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.relationalOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun F32LeExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F32Le,
) {
    context.vstack.relationalOperation(Float::le)
}
