package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.unaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

internal inline fun I32PopcntExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32Popcnt,
): Result<Unit, InvocationError> {
    return context.stack.unaryOperation(Int::countOneBits)
}
