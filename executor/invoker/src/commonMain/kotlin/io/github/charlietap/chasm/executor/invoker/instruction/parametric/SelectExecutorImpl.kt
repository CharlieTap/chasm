@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.parametric

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.popValueOrError

internal inline fun SelectExecutorImpl(
    stack: Stack,
): Result<Unit, InvocationError> = binding {
    val select = stack.popI32().bind()

    val value2 = stack.popValueOrError().bind()

    if (select == 0) {
        stack.popValueOrError().bind()
        stack.push(value2)
    }
}
