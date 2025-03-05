package io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop

import io.github.charlietap.chasm.executor.invoker.ext.ne
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.relationalOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I32NeExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32Ne,
) {
    context.vstack.relationalOperation(Int::ne)
}
