@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.ext.push
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal fun ArrayLenExecutorImpl(
    store: Store,
    stack: Stack,
): Result<Unit, InvocationError> = binding {
    val arrayReference = stack.popArrayReference().bind()
    val arrayInstance = store.array(arrayReference.address).bind()

    stack.push(NumberValue.I32(arrayInstance.fields.size))
}
