package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.unaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I32ClzExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32Clz,
) {
    context.vstack.unaryOperation(Int::countLeadingZeroBits)
}
