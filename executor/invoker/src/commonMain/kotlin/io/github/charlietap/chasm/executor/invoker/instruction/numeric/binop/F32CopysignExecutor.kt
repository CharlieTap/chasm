package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.executor.invoker.ext.copySign
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.binaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun F32CopysignExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F32Copysign,
) {
    context.vstack.binaryOperation(Float::copySign)
}
