package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.convertF64s
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.convertOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun F64ConvertI64SExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F64ConvertI64S,
) {
    context.vstack.convertOperation(Long::convertF64s)
}
