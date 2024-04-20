@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.ext.push
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal fun ArrayFillExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
): Result<Unit, InvocationError> =
    ArrayFillExecutorImpl(
        store = store,
        stack = stack,
        typeIndex = typeIndex,
        arraySetExecutor = ::ArraySetExecutorImpl,
    )

internal fun ArrayFillExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
    arraySetExecutor: ArraySetExecutor,
): Result<Unit, InvocationError> = binding {

    val elementsToFill = stack.popI32().bind()
    val fillValue = stack.popValue().bind()
    val arrayElementOffset = stack.popI32().bind()
    val arrayReference = stack.popArrayReference().bind()
    val arrayInstance = store.array(arrayReference.address).bind()

    if (arrayElementOffset + elementsToFill > arrayInstance.fields.size) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    if (elementsToFill == 0) return@binding

    stack.push(arrayReference)
    stack.push(NumberValue.I32(arrayElementOffset))
    stack.push(fillValue)

    arraySetExecutor(store, stack, typeIndex).bind()

    stack.push(arrayReference)
    stack.push(NumberValue.I32(arrayElementOffset + 1))
    stack.push(fillValue)
    stack.push(NumberValue.I32(elementsToFill - 1))

    ArrayFillExecutorImpl(store, stack, typeIndex, arraySetExecutor).bind()
}
