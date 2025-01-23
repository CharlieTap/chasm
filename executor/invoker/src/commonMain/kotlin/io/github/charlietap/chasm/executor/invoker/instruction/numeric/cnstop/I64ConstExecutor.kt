package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cnstop

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.constOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I64ConstExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64Const,
) {
    context.vstack.constOperation(instruction.value)
}
