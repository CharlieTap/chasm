package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cnstop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.constOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun F64ConstExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F64Const,
) {
    context.vstack.constOperation(instruction.value)
}
