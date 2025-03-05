package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.executor.invoker.ext.max
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.binaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun F32MaxExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F32Max,
) {
    context.vstack.binaryOperation(Float::max)
}
