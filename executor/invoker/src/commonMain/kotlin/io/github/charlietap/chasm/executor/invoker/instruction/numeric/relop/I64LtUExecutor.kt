package io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop

import io.github.charlietap.chasm.executor.invoker.ext.ltu
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.relationalOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I64LtUExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64LtU,
) {
    context.vstack.relationalOperation(Long::ltu)
}
