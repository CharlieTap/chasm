package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import io.github.charlietap.chasm.executor.invoker.ext.nearest
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.unaryOperation
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

internal inline fun F32NearestExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F32Nearest,
) {
    context.vstack.unaryOperation(Float::nearest)
}
