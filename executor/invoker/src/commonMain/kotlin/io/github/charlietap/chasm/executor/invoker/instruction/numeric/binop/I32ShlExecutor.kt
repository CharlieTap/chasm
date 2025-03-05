package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.binaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I32ShlExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32Shl,
) {
    context.vstack.binaryOperation(Int::shl)
}
