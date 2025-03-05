package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.wrap
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.convertOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I32WrapI64Executor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32WrapI64,
) {
    context.vstack.convertOperation(Long::wrap)
}
