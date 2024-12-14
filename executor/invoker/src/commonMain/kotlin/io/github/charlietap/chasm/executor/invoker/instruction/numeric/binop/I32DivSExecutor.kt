package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation
import io.github.charlietap.chasm.executor.runtime.ext.peekNthValue
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I32

internal inline fun I32DivSExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I32DivS,
): Result<Unit, InvocationError> = binding {
    val stack = context.stack
    val operand1 = stack.peekNthValue(1).bind().value as I32
    val operand2 = stack.peekNthValue(0).bind().value as I32

    if (operand2.value == 0) {
        Err(InvocationError.CannotDivideIntegerByZero).bind()
    } else if (operand1.value == Int.MIN_VALUE && operand2.value == -1) {
        Err(InvocationError.IntegerOverflow).bind()
    }

    stack.binaryOperation(Int::div).bind()
}
