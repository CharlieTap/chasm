@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.ext.wrapI31
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal typealias RefI31Executor = (Stack) -> Result<Unit, InvocationError>

internal fun RefI31Executor(
    stack: Stack,
): Result<Unit, InvocationError> =
    RefI31Executor(
        stack = stack,
        i31Wrapper = Int::wrapI31,
    )

internal inline fun RefI31Executor(
    stack: Stack,
    crossinline i31Wrapper: (Int) -> ReferenceValue.I31,
): Result<Unit, InvocationError> = binding {

    val value = stack.popI32().bind()

    val i31 = i31Wrapper(value)

    stack.pushValue(i31)
}
