package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun F64SubExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F64Sub,
) {
    context.stack.binaryOperation(Double::minus)
}
