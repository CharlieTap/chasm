package io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop

import io.github.charlietap.chasm.executor.invoker.ext.lt
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.relationalOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun F64LtExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F64Lt,
) {
    context.vstack.relationalOperation(Double::lt)
}
