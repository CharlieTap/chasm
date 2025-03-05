package io.github.charlietap.chasm.executor.invoker.instruction.numeric.testop

import io.github.charlietap.chasm.executor.invoker.ext.eqz
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.testOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I64EqzExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64Eqz,
) {
    context.vstack.testOperation(Long::eqz)
}
