@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.extendSigned
import io.github.charlietap.chasm.executor.runtime.ext.extendUnsigned
import io.github.charlietap.chasm.executor.runtime.ext.popI31Reference
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal typealias I31GetExecutor = (Stack, Boolean) -> Result<Unit, InvocationError>

internal fun I31GetExecutor(
    stack: Stack,
    signedExtension: Boolean,
): Result<Unit, InvocationError> =
    I31GetExecutor(
        stack = stack,
        signedExtension = signedExtension,
        i31SignedExtender = ReferenceValue.I31::extendSigned,
        i31UnsignedExtender = ReferenceValue.I31::extendUnsigned,
    )

internal inline fun I31GetExecutor(
    stack: Stack,
    signedExtension: Boolean,
    crossinline i31SignedExtender: (ReferenceValue.I31) -> Int,
    crossinline i31UnsignedExtender: (ReferenceValue.I31) -> Int,
): Result<Unit, InvocationError> = binding {

    val value = stack.popI31Reference().bind()

    val extended = if (signedExtension) {
        i31SignedExtender(value)
    } else {
        i31UnsignedExtender(value)
    }

    stack.pushValue(NumberValue.I32(extended))
}
