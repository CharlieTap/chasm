package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.executor.invoker.ext.remu
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I64RemUExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64RemU,
) {
    val stack = context.vstack

    val right = instruction.right(stack)

    if (right.toULong() == 0uL) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    }

    val left = instruction.left(stack)
    val result = left.remu(right)

    instruction.destination(result, stack)
}
