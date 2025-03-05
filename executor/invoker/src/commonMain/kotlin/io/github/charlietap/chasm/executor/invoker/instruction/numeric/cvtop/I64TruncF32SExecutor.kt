package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI64sTrapping
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.convertOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I64TruncF32SExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64TruncF32S,
) {
    context.vstack.convertOperation(Float::truncI64sTrapping)
}
