@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.flow.ReturnException
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popValue

internal inline fun ReturnExecutorImpl(
    stack: Stack,
): Result<Unit, InvocationError> = binding {
    val frame = stack.peekFrame().bind()
    val results = List(frame.arity.value) {
        stack.popValue().bind().value
    }.asReversed()
    throw ReturnException(results)
}
