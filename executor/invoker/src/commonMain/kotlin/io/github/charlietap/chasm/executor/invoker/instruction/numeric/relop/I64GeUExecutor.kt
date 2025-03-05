package io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop

import io.github.charlietap.chasm.executor.invoker.ext.geu
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.relationalOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I64GeUExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64GeU,
) {
    context.vstack.relationalOperation(Long::geu)
}
