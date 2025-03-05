package io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop

import io.github.charlietap.chasm.executor.invoker.ext.ge
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.relationalOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I64GeSExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64GeS,
) {
    context.vstack.relationalOperation(Long::ge)
}
