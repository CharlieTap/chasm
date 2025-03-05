package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.binaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I32AddExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32Add,
) {
    context.vstack.binaryOperation(Int::plus)
}
