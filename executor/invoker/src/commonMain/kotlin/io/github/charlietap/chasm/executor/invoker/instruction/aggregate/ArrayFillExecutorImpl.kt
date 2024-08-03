@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Err
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

internal fun ArrayFillExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
): Result<Unit, InvocationError> =
    ArrayFillExecutorImpl(
        store = store,
        stack = stack,
        typeIndex = typeIndex,
        definedTypeExpander = ::DefinedTypeExpanderImpl,
        fieldPacker = ::FieldPackerImpl,
    )

internal fun ArrayFillExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
    definedTypeExpander: DefinedTypeExpander,
    fieldPacker: FieldPacker,
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

    val frame = stack.peekFrame().bind()
    val definedType = frame.state.module.definedType(typeIndex).bind()
    val arrayType = definedTypeExpander(definedType).arrayType().bind()

    repeat(elementsToFill) { fillOffset ->

        val fieldIndex = arrayElementOffset + fillOffset
        val fieldValue = fieldPacker(fillValue.value, arrayType.fieldType).bind()

        arrayInstance.fields[fieldIndex] = fieldValue
    }
}
