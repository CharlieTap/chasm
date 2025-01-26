package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI32s
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I32TruncSatF64SExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncSatF64S,
) {
    context.vstack.convertOperation(Double::truncI32s)
}
