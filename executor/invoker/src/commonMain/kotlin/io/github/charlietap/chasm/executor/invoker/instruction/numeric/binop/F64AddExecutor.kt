package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.binaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun F64AddExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: NumericInstruction.F64Add,
) {
    vstack.binaryOperation(Double::plus)
}
