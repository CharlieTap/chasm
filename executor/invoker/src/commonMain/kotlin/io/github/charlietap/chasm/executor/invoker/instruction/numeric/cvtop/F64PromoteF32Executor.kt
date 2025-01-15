package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.F64

internal inline fun F64PromoteF32Executor(
    context: ExecutionContext,
    instruction: NumericInstruction.F64PromoteF32,
) {
    context.stack.convertOperation(::F64, Float::toDouble)
}
