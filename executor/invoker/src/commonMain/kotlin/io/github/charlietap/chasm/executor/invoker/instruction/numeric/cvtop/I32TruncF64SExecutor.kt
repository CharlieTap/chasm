package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI32sTrapping
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.convertOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I32TruncF64SExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncF64S,
) {
    context.vstack.convertOperation(Double::truncI32sTrapping)
}
