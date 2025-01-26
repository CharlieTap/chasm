package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI32sTrapping
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I32TruncF32SExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncF32S,
) {
    context.vstack.convertOperation(Float::truncI32sTrapping)
}
