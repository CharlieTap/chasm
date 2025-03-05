package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.executor.invoker.ext.countOnePopulation
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.unaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun I64PopcntExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64Popcnt,
) {
    context.vstack.unaryOperation(Long::countOnePopulation)
}
