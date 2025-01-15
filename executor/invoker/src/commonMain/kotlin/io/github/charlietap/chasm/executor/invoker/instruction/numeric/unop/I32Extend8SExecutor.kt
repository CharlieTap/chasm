package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.executor.invoker.ext.extend8s
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.unaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I32Extend8SExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32Extend8S,
) {
    context.stack.unaryOperation(Int::extend8s)
}
