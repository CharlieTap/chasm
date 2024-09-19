@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal typealias ExternConvertAnyExecutor = (Stack) -> Result<Unit, InvocationError>

internal fun ExternConvertAnyExecutor(
    stack: Stack,
): Result<Unit, InvocationError> = binding {
    when (val referenceValue = stack.popReference().bind()) {
        is ReferenceValue.Null -> {
            stack.pushValue(ReferenceValue.Null(AbstractHeapType.Extern))
        }
        else -> {
            stack.pushValue(ReferenceValue.Extern(referenceValue))
        }
    }
}
