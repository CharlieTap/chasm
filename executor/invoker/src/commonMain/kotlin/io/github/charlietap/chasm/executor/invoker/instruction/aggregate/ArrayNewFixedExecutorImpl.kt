@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.gc.weakReference
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.ext.push
import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.executor.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.executor.type.expansion.DefinedTypeExpanderImpl

internal fun ArrayNewFixedExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
    size: UInt,
): Result<Unit, InvocationError> =
    ArrayNewFixedExecutorImpl(
        store = store,
        stack = stack,
        typeIndex = typeIndex,
        size = size,
        definedTypeExpander = ::DefinedTypeExpanderImpl,
        fieldPacker = ::FieldPackerImpl,
    )

internal inline fun ArrayNewFixedExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
    size: UInt,
    crossinline definedTypeExpander: DefinedTypeExpander,
    crossinline fieldPacker: FieldPacker,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrame().bind()
    val definedType = frame.state.module.types[typeIndex.index()]

    val arrayType = definedTypeExpander(definedType).arrayType().bind()

    val fields = MutableList(size.toInt()) { _ ->
        val value = stack.popValue().bind().value
        fieldPacker(value, arrayType.fieldType).bind()
    }.asReversed()

    val instance = ArrayInstance(definedType, fields)

    store.arrays.add(weakReference(instance))

    val address = Address.Array(store.arrays.size - 1)
    val reference = ReferenceValue.Array(address, instance)

    stack.push(reference)
}
