package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.binaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I32XorExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32Xor,
) {
    context.vstack.binaryOperation(Int::xor)
}
