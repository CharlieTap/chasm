package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.convertF64u
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.convertOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun F64ConvertI64UExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F64ConvertI64U,
) {
    context.vstack.convertOperation(Long::convertF64u)
}
