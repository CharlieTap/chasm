package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.executor.invoker.ext.extend32s
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.unaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I64Extend32SExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64Extend32S,
) {
    context.vstack.unaryOperation(Long::extend32s)
}
