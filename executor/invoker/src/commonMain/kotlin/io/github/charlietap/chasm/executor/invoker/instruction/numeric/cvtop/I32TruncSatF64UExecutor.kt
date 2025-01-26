package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI32u
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I32TruncSatF64UExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncSatF64U,
) {
    context.vstack.convertOperation(Double::truncI32u)
}
