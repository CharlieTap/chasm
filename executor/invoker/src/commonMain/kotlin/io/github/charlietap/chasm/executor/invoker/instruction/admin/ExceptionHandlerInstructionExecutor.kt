@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.admin

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal typealias ExceptionHandlerInstructionExecutor = (Stack.Entry.ExceptionHandler, Stack) -> Result<Unit, InvocationError>

@Suppress("UNUSED_PARAMETER")
internal inline fun ExceptionHandlerInstructionExecutor(
    handler: Stack.Entry.ExceptionHandler,
    stack: Stack,
): Result<Unit, InvocationError> = binding {
}
