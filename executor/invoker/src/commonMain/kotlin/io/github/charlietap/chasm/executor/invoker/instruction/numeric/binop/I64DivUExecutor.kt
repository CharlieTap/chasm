package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.ext.divu
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation
import io.github.charlietap.chasm.executor.runtime.ext.peekNthValue
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I64

internal inline fun I64DivUExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64DivU,
): Result<Unit, InvocationError> = binding {
    val operand2 = context.stack
        .peekNthValue(0)
        .bind()
        .value as I64

    if (operand2.value.toULong() == 0uL) {
        Err(InvocationError.CannotDivideIntegerByZero).bind()
    }

    context.stack.binaryOperation(Long::divu).bind()
}
