package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.executor.invoker.ext.shru
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.binaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I32ShrUExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32ShrU,
) {
    context.vstack.binaryOperation(Int::shru)
}
