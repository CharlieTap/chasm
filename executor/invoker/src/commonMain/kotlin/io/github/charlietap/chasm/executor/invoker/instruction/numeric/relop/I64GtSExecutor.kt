package io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop

import io.github.charlietap.chasm.executor.invoker.ext.gt
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.relationalOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I64GtSExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64GtS,
) {
    context.vstack.relationalOperation(Long::gt)
}
