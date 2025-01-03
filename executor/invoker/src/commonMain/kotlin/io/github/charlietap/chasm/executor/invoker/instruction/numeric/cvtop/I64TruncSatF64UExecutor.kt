package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.invoker.ext.truncI64u
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.convertOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I64

internal inline fun I64TruncSatF64UExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64TruncSatF64U,
): Result<Unit, InvocationError> {
    return context.stack.convertOperation(::I64, Double::truncI64u)
}
