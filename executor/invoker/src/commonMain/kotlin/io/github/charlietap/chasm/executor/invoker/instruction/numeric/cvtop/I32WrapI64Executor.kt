package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.wrap
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I32WrapI64Executor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32WrapI64,
) {
    context.vstack.convertOperation(Long::wrap)
}
