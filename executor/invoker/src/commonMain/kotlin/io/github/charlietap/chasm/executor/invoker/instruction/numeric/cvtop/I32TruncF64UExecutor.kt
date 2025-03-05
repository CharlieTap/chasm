package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI32uTrapping
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.convertOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I32TruncF64UExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncF64U,
) {
    context.vstack.convertOperation(Double::truncI32uTrapping)
}
