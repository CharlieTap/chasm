package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I32ShlExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32Shl,
) {
    context.stack.binaryOperation(Int::shl)
}
