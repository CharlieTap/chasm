package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.executor.invoker.ext.trunc
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.unaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun F32TruncExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F32Trunc,
) {
    context.stack.unaryOperation(Float::trunc)
}
