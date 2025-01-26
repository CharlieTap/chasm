package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.convertF32u
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun F32ConvertI64UExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F32ConvertI64U,
) {
    context.vstack.convertOperation(Long::convertF32u)
}
