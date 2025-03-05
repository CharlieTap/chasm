package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.executor.invoker.ext.floor
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.unaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun F32FloorExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F32Floor,
) {
    context.vstack.unaryOperation(Float::floor)
}
