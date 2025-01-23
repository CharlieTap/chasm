package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.executor.invoker.ext.extendI64u
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I64

internal inline fun I64ExtendI32UExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64ExtendI32U,
) {
    context.vstack.convertOperation(::I64, Int::extendI64u)
}
