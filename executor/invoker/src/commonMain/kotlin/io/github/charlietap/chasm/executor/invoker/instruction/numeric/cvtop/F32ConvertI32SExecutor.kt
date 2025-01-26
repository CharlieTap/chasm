package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.convertF32s
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun F32ConvertI32SExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F32ConvertI32S,
) {
    context.vstack.convertOperation(Int::convertF32s)
}
