package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI64s
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I64TruncSatF64SExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64TruncSatF64S,
) {
    context.vstack.convertOperation(Double::truncI64s)
}
