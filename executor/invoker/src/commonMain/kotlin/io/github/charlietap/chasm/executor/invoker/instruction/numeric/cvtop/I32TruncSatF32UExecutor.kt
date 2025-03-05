package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI32u
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.convertOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I32TruncSatF32UExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncSatF32U,
) {
    context.vstack.convertOperation(Float::truncI32u)
}
