package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.executor.invoker.ext.floor
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.unaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun F64FloorExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F64Floor,
) {
    context.vstack.unaryOperation(Double::floor)
}
