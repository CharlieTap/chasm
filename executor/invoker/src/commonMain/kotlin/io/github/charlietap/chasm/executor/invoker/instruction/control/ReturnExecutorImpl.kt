@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.flow.ReturnException
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.peekFrameOrError
import io.github.charlietap.chasm.executor.runtime.ext.popValueOrError

internal inline fun ReturnExecutorImpl(
    stack: Stack,
): Result<Unit, InvocationError> = binding {
    val frame = stack.peekFrameOrError().bind()
    val results = List(frame.arity.value) {
        stack.popValueOrError().bind().value
    }
    throw ReturnException(results)
}
