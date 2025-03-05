package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.executor.invoker.ext.countTrailingZero
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.unaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I64CtzExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64Ctz,
) {
    context.vstack.unaryOperation(Long::countTrailingZero)
}
