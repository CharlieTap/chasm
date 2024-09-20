@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal inline fun RefNullExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefNull,
): Result<Unit, InvocationError> {
    val (stack) = context
    stack.push(Stack.Entry.Value(ReferenceValue.Null(instruction.type)))
    return Ok(Unit)
}
