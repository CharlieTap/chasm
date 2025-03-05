package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI64u
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.convertOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I64TruncSatF64UExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64TruncSatF64U,
) {
    context.vstack.convertOperation(Double::truncI64u)
}
