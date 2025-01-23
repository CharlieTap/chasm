package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.executor.invoker.ext.extend16s
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.unaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I64Extend16SExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64Extend16S,
) {
    context.vstack.unaryOperation(Long::extend16s)
}
