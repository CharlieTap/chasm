package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I32

internal inline fun I32ReinterpretF32Executor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32ReinterpretF32,
): Result<Unit, InvocationError> {
    return context.stack.convertOperation(::I32, Float::toRawBits)
}
