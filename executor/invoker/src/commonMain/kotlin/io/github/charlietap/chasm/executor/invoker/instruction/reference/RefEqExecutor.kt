@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal typealias RefEqExecutor = (Stack) -> Result<Unit, InvocationError>

internal inline fun RefEqExecutor(
    stack: Stack,
): Result<Unit, InvocationError> = binding {

    val referenceValue1 = stack.popValue().bind().value
    val referenceValue2 = stack.popValue().bind().value

    if (referenceValue1 == referenceValue2) {
        stack.pushValue(NumberValue.I32(1))
    } else {
        stack.pushValue(NumberValue.I32(0))
    }
}
