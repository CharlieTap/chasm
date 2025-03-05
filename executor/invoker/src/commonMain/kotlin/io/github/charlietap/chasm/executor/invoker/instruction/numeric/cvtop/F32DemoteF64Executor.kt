package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.convertOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun F32DemoteF64Executor(
    context: ExecutionContext,
    instruction: NumericInstruction.F32DemoteF64,
) {
    context.vstack.convertOperation(Double::toFloat)
}
