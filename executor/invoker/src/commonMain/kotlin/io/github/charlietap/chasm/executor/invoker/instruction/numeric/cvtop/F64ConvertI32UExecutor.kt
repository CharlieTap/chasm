package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.convertF64u
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.F64

internal inline fun F64ConvertI32UExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F64ConvertI32U,
) {
    context.vstack.convertOperation(::F64, Int::convertF64u)
}
