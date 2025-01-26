package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.convertF32u
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun F32ConvertI32UExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F32ConvertI32U,
) {
    context.vstack.convertOperation(Int::convertF32u)
}
