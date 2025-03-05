package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.binaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I64AndExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64And,
) {
    context.vstack.binaryOperation(Long::and)
}
