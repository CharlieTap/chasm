package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.executor.invoker.ext.extend8s
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.unaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I64Extend8SExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64Extend8S,
) {
    context.vstack.unaryOperation(Long::extend8s)
}
