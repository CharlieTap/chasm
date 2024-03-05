@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.ext.popValueOrError
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal inline fun DropExecutorImpl(
    stack: Stack,
): Result<Unit, InvocationError> = binding {
    stack.popValueOrError().bind()
}
