package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I64XorExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64Xor,
) {
    context.stack.binaryOperation(Long::xor)
}
