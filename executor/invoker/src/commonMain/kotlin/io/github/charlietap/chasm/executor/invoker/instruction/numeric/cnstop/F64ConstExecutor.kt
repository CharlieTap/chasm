package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cnstop

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.constOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun F64ConstExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F64Const,
) {
    context.stack.constOperation(instruction.value)
}
