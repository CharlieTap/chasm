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
import io.github.charlietap.chasm.executor.runtime.ext.field
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpanderImpl

internal fun ArrayGetExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
    signedUnpack: Boolean,
): Result<Unit, InvocationError> =
    ArrayGetExecutorImpl(
        store = store,
        stack = stack,
        typeIndex = typeIndex,
        signedUnpack = signedUnpack,
        definedTypeExpander = ::DefinedTypeExpanderImpl,
        fieldUnpacker = ::FieldUnpackerImpl,
    )

internal inline fun ArrayGetExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
    signedUnpack: Boolean,
    crossinline definedTypeExpander: DefinedTypeExpander,
    crossinline fieldUnpacker: FieldUnpacker,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrame().bind()
    val definedType = frame.state.module.definedType(typeIndex).bind()

    val arrayType = definedTypeExpander(definedType).arrayType().bind()
    val fieldType = arrayType.fieldType

    val fieldIndex = stack.popI32().bind()
    val arrayRef = stack.popArrayReference().bind()

    val arrayInstance = store.array(arrayRef.address).bind()

    val fieldValue = arrayInstance.field(Index.FieldIndex(fieldIndex.toUInt())).bind()

    val unpackedValue = fieldUnpacker(fieldValue, fieldType, signedUnpack).bind()

    stack.pushValue(unpackedValue)
}
