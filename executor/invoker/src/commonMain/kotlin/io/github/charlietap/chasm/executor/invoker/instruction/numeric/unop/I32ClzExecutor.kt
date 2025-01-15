package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.unaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I32ClzExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32Clz,
) {
    context.stack.unaryOperation(Int::countLeadingZeroBits)
}
