@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.ext.push
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun AnyConvertExternExecutorImpl(
    stack: Stack,
): Result<Unit, InvocationError> = binding {
    when (val referenceValue = stack.popReference().bind()) {
        is ReferenceValue.Null -> {
            stack.push(ReferenceValue.Null(AbstractHeapType.Any))
        }
        is ReferenceValue.Extern -> {
            stack.push(referenceValue.referenceValue)
        }
        else -> Err(InvocationError.UnexpectedReferenceValue).bind<Unit>()
    }
}
