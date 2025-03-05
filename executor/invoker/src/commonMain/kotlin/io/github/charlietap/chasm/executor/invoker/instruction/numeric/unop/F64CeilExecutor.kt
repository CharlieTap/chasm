package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.executor.invoker.ext.ceil
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.unaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun F64CeilExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F64Ceil,
) {
    context.vstack.unaryOperation(Double::ceil)
}
