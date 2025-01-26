package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.extendI64s
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I64ExtendI32SExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64ExtendI32S,
) {
    context.vstack.convertOperation(Int::extendI64s)
}
