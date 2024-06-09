@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.ext.elementAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.store.Store

internal fun ArrayNewElementExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
    elementIndex: Index.ElementIndex,
): Result<Unit, InvocationError> =
    ArrayNewElementExecutorImpl(
        store = store,
        stack = stack,
        typeIndex = typeIndex,
        elementIndex = elementIndex,
        arrayNewFixedExecutor = ::ArrayNewFixedExecutorImpl,
    )

internal inline fun ArrayNewElementExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
    elementIndex: Index.ElementIndex,
    crossinline arrayNewFixedExecutor: ArrayNewFixedExecutor,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrame().bind()

    val elementAddress = frame.state.module.elementAddress(elementIndex.index()).bind()
    val elementInstance = store.element(elementAddress).bind()

    val arrayLength = stack.popI32().bind()
    val arrayStartOffsetInSegment = stack.popI32().bind()
    val arrayEndOffsetInSegment = arrayStartOffsetInSegment + arrayLength

    if (arrayEndOffsetInSegment > elementInstance.elements.size) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    elementInstance.elements
        .slice(arrayStartOffsetInSegment until arrayEndOffsetInSegment)
        .forEach { referenceValue ->
            stack.pushValue(referenceValue)
        }

    arrayNewFixedExecutor(store, stack, typeIndex, arrayLength.toUInt())
}
