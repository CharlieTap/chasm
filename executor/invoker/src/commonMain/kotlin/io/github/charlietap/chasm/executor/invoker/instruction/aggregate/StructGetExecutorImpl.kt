@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.field
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popStructReference
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.ext.struct
import io.github.charlietap.chasm.executor.runtime.ext.structType
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpanderImpl

internal fun StructGetExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
    fieldIndex: Index.FieldIndex,
    signedUnpack: Boolean,
): Result<Unit, InvocationError> =
    StructGetExecutorImpl(
        store = store,
        stack = stack,
        typeIndex = typeIndex,
        fieldIndex = fieldIndex,
        signedUnpack = signedUnpack,
        definedTypeExpander = ::DefinedTypeExpanderImpl,
        fieldUnpacker = ::FieldUnpackerImpl,
    )

internal inline fun StructGetExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
    fieldIndex: Index.FieldIndex,
    signedUnpack: Boolean,
    crossinline definedTypeExpander: DefinedTypeExpander,
    crossinline fieldUnpacker: FieldUnpacker,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrame().bind()
    val definedType = frame.state.module.types[typeIndex.index()]

    val structType = definedTypeExpander(definedType).structType().bind()
    val fieldType = structType.field(fieldIndex).bind()

    val structRef = stack.popStructReference().bind()

    val structInstance = store.struct(structRef.address).bind()

    val fieldValue = structInstance.field(fieldIndex).bind()

    val unpackedValue = fieldUnpacker(fieldValue, fieldType, signedUnpack).bind()

    stack.pushValue(unpackedValue)
}
