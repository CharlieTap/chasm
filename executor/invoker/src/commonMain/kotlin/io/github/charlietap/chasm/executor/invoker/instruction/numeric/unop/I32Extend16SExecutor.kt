package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.executor.invoker.ext.extend16s
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.unaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I32Extend16SExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32Extend16S,
) {
    context.vstack.unaryOperation(Int::extend16s)
}
