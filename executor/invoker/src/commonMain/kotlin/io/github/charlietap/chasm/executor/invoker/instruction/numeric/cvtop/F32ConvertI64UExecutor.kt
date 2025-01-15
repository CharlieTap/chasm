package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.invoker.ext.convertF32u
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.F32

internal inline fun F32ConvertI64UExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F32ConvertI64U,
) {
    context.stack.convertOperation(::F32, Long::convertF32u)
}
