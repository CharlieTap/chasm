package io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop

import io.github.charlietap.chasm.executor.invoker.ext.eq
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.relationalOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I64EqExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64Eq,
) {
    context.vstack.relationalOperation(Long::eq)
}
