package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I32

internal inline fun I32ReinterpretF32Executor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32ReinterpretF32,
) {
    context.vstack.convertOperation(::I32, Float::toRawBits)
}
