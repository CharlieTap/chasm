package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI64uTrapping
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I64TruncF32UExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64TruncF32U,
) {
    context.vstack.convertOperation(Float::truncI64uTrapping)
}
