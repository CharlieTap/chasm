@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.definedType
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpanderImpl

internal fun ArraySetExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
): Result<Unit, InvocationError> =
    ArraySetExecutorImpl(
        store = store,
        stack = stack,
        typeIndex = typeIndex,
        definedTypeExpander = ::DefinedTypeExpanderImpl,
        fieldPacker = ::FieldPackerImpl,
    )

internal inline fun ArraySetExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
    crossinline definedTypeExpander: DefinedTypeExpander,
    crossinline fieldPacker: FieldPacker,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrame().bind()
    val definedType = frame.state.module.definedType(typeIndex).bind()

    val arrayType = definedTypeExpander(definedType).arrayType().bind()

    val value = stack.popValue().bind()

    val fieldIndex = stack.popI32().bind()
    val arrayReference = stack.popArrayReference().bind()

    val arrayInstance = store.array(arrayReference.address).bind()

    val fieldValue = fieldPacker(value.value, arrayType.fieldType).bind()

    arrayInstance.fields[fieldIndex] = fieldValue
}
