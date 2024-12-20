package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.invoker.ext.convertF64s
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.F64

internal inline fun F64ConvertI64SExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F64ConvertI64S,
): Result<Unit, InvocationError> {
    return context.stack.convertOperation(::F64, Long::convertF64s)
}
