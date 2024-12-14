package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.ext.remu
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation
import io.github.charlietap.chasm.executor.runtime.ext.peekNthValue
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I32

internal inline fun I32RemUExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32RemU,
): Result<Unit, InvocationError> = binding {
    val operand2 = context.stack
        .peekNthValue(0)
        .bind()
        .value as I32

    if (operand2.value.toUInt() == 0u) {
        Err(InvocationError.CannotDivideIntegerByZero).bind()
    }

    context.stack.binaryOperation(Int::remu).bind()
}
