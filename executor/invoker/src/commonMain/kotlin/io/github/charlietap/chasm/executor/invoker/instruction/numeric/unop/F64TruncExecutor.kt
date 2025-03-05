package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.executor.invoker.ext.trunc
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.unaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun F64TruncExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F64Trunc,
) {
    context.vstack.unaryOperation(Double::trunc)
}
