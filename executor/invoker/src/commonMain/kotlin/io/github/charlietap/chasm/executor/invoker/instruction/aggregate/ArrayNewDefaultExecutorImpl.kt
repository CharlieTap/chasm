@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.ext.definedType
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpanderImpl

internal fun ArrayNewDefaultExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
): Result<Unit, InvocationError> =
    ArrayNewDefaultExecutorImpl(
        store = store,
        stack = stack,
        typeIndex = typeIndex,
        definedTypeExpander = ::DefinedTypeExpanderImpl,
        arrayNewFixedExecutor = ::ArrayNewFixedExecutorImpl,
    )

internal inline fun ArrayNewDefaultExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
    crossinline definedTypeExpander: DefinedTypeExpander,
    crossinline arrayNewFixedExecutor: ArrayNewFixedExecutor,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrame().bind()
    val definedType = frame.state.module.definedType(typeIndex).bind()

    val arrayType = definedTypeExpander(definedType).arrayType().bind()

    val size = stack.popI32().bind()
    val value = arrayType.fieldType.default().bind()
    repeat(size) {
        stack.pushValue(value)
    }

    arrayNewFixedExecutor(store, stack, typeIndex, size.toUInt())
}
