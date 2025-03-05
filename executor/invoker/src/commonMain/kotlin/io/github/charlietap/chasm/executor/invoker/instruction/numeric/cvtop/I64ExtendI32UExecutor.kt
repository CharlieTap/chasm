package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.extendI64u
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.convertOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I64ExtendI32UExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64ExtendI32U,
) {
    context.vstack.convertOperation(Int::extendI64u)
}
