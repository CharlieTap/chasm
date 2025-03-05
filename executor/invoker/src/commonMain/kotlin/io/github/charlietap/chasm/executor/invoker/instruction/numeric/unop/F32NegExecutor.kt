package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.unaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun F32NegExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F32Neg,
) {
    context.vstack.unaryOperation(Float::unaryMinus)
}
