package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun F64PromoteF32Executor(
    context: ExecutionContext,
    instruction: NumericInstruction.F64PromoteF32,
) {
    context.vstack.convertOperation(Float::toDouble)
}
