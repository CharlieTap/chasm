package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.unaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I32CtzExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32Ctz,
) {
    context.stack.unaryOperation(Int::countTrailingZeroBits)
}
