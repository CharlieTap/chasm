@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popValue

internal typealias DropExecutor = (Stack) -> Result<Unit, InvocationError>

internal inline fun DropExecutor(
    stack: Stack,
): Result<Unit, InvocationError> = binding {
    stack.popValue().bind()
}
