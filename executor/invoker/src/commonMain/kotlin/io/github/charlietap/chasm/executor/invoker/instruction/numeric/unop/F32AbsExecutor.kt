package io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.unaryOperation
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction
import kotlin.math.absoluteValue

internal inline fun F32AbsExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.F32Abs,
): Result<Unit, InvocationError> {
    return context.stack.unaryOperation(Float::absoluteValue)
}
