package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I32SubExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32Sub,
) {
    context.vstack.binaryOperation(Int::minus)
}
