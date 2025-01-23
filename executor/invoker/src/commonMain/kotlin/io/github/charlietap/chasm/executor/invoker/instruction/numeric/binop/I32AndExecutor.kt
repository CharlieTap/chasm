package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I32AndExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32And,
) {
    context.vstack.binaryOperation(Int::and)
}
