package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.executor.invoker.ext.shr
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.binaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I64ShrSExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64ShrS,
) {
    context.vstack.binaryOperation(Long::shr)
}
