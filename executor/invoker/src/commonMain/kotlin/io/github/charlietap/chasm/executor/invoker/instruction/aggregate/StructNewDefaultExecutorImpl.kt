@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.ext.structType
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.executor.type.expansion.DefinedTypeExpanderImpl

internal fun StructNewDefaultExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
): Result<Unit, InvocationError> =
    StructNewDefaultExecutorImpl(
        store = store,
        stack = stack,
        typeIndex = typeIndex,
        definedTypeExpander = ::DefinedTypeExpanderImpl,
        structNewExecutor = ::StructNewExecutorImpl,
    )

internal inline fun StructNewDefaultExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
    crossinline definedTypeExpander: DefinedTypeExpander,
    crossinline structNewExecutor: StructNewExecutor,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrame().bind()
    val definedType = frame.state.module.types[typeIndex.index()]

    val structType = definedTypeExpander(definedType).structType().bind()
    structType.fields.forEach { fieldType ->
        val value = fieldType.default().bind()
        stack.pushValue(value)
    }

    structNewExecutor(store, stack, typeIndex)
}
