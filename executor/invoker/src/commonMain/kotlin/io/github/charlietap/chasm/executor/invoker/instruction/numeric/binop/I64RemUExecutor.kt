@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.ext.remu
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.binaryOperation
import io.github.charlietap.chasm.executor.runtime.ext.peekNthValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I64

internal inline fun I64RemUExecutor(
    context: ExecutionContext,
    instruction: NumericInstruction.I64RemU,
): Result<Unit, InvocationError> = binding {
    val operand2 = context.stack.peekNthValue(0).bind().value as I64

    if (operand2.value.toULong() == 0uL) {
        Err(InvocationError.CannotDivideIntegerByZero).bind()
    }

    context.stack.binaryOperation(Long::remu).bind()
}
