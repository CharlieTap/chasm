@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal typealias RefAsNonNullExecutor = (Stack) -> Result<Unit, InvocationError>

internal inline fun RefAsNonNullExecutor(
    stack: Stack,
): Result<Unit, InvocationError> = binding {

    val value = stack.peekValueOrNull()?.value

    if (value is ReferenceValue) {
        if (value is ReferenceValue.Null) {
            Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
        }
    } else {
        Err(InvocationError.ReferenceValueExpected).bind<Unit>()
    }
}
