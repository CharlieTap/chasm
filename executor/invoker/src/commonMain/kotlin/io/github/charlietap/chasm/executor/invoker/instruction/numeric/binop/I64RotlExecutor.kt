package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.executor.invoker.ext.rotateLeft
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I64RotlExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64Rotl,
) {
    context.vstack.binaryOperation(Long::rotateLeft)
}
