package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.invoker.ext.trunc
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.unaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun F64TruncExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F64Trunc,
) {
    context.stack.unaryOperation(Double::trunc)
}
