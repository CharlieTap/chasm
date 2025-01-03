package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I32XorExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32Xor,
): Result<Unit, InvocationError> {
    return context.stack.binaryOperation(Int::xor)
}
