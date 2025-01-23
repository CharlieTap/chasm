package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI32u
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I32

internal inline fun I32TruncSatF32UExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32TruncSatF32U,
) {
    context.vstack.convertOperation(::I32, Float::truncI32u)
}
