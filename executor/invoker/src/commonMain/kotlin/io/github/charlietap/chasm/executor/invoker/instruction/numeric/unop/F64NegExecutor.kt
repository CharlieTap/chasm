package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.unaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun F64NegExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F64Neg,
) {
    context.vstack.unaryOperation(Double::unaryMinus)
}
