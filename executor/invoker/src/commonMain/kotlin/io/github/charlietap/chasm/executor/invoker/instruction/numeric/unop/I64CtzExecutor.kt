package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.invoker.ext.countTrailingZero
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.unaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I64CtzExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64Ctz,
) {
    context.stack.unaryOperation(Long::countTrailingZero).bind()
}
