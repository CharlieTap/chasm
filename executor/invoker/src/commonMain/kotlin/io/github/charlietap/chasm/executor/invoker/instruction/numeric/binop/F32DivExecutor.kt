package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.binaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun F32DivExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F32Div,
) {
    context.vstack.binaryOperation(Float::div)
}
