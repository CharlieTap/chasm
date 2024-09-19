@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal typealias RefNullExecutor = (Stack, ReferenceInstruction.RefNull) -> Result<Unit, InvocationError>

internal inline fun RefNullExecutor(
    stack: Stack,
    instruction: ReferenceInstruction.RefNull,
): Result<Unit, InvocationError> {
    stack.push(Stack.Entry.Value(ReferenceValue.Null(instruction.type)))
    return Ok(Unit)
}
