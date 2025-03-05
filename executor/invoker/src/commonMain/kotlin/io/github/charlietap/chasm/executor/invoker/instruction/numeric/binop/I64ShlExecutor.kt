package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.executor.invoker.ext.shl
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.binaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I64ShlExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64Shl,
) {
    context.vstack.binaryOperation(Long::shl)
}
