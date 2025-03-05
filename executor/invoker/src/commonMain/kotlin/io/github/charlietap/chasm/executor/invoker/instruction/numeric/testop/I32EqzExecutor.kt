package io.github.charlietap.chasm.executor.invoker.instruction.numeric.testop

import io.github.charlietap.chasm.executor.invoker.ext.eqz
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.testOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I32EqzExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32Eqz,
) {
    context.vstack.testOperation(Int::eqz)
}
