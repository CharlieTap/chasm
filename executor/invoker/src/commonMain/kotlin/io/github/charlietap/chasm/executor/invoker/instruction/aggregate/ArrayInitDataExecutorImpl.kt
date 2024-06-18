@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.memory.ext.valueFromBytes
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.array
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.bitWidth
import io.github.charlietap.chasm.executor.runtime.ext.data
import io.github.charlietap.chasm.executor.runtime.ext.dataAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpanderImpl

internal fun ArrayInitDataExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
    dataIndex: Index.DataIndex,
): Result<Unit, InvocationError> =
    ArrayInitDataExecutorImpl(
        store = store,
        stack = stack,
        typeIndex = typeIndex,
        dataIndex = dataIndex,
        definedTypeExpander = ::DefinedTypeExpanderImpl,
        arraySetExecutor = ::ArraySetExecutorImpl,
    )

internal fun ArrayInitDataExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
    dataIndex: Index.DataIndex,
    definedTypeExpander: DefinedTypeExpander,
    arraySetExecutor: ArraySetExecutor,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrame().bind()
    val definedType = frame.state.module.types[typeIndex.index()]

    val arrayType = definedTypeExpander(definedType).arrayType().bind()

    val dataAddress = frame.state.module.dataAddress(dataIndex.index()).bind()
    val dataInstance = store.data(dataAddress).bind()

    val elementsToCopy = stack.popI32().bind()
    val sourceOffsetInByteArray = stack.popI32().bind()
    val destinationOffsetInArray = stack.popI32().bind()

    val arrayReference = stack.popArrayReference().bind()
    val arrayInstance = store.array(arrayReference.address).bind()

    val arrayElementSizeInBytes = arrayType.fieldType.bitWidth().bind() / 8
    val endOffsetInByteArray = sourceOffsetInByteArray + arrayElementSizeInBytes

    if (
        (destinationOffsetInArray + elementsToCopy > arrayInstance.fields.size) ||
        (sourceOffsetInByteArray + (elementsToCopy * arrayElementSizeInBytes) > dataInstance.bytes.size)
    ) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    if (elementsToCopy == 0) return@binding

    val byteArray = dataInstance.bytes.sliceArray(sourceOffsetInByteArray until endOffsetInByteArray)
    val element = arrayType.fieldType.valueFromBytes(byteArray).bind()

    stack.pushValue(arrayReference)
    stack.pushValue(NumberValue.I32(destinationOffsetInArray))
    stack.pushValue(element)

    arraySetExecutor(store, stack, typeIndex).bind()

    stack.pushValue(arrayReference)

    stack.pushValue(NumberValue.I32(destinationOffsetInArray + 1))
    stack.pushValue(NumberValue.I32(sourceOffsetInByteArray + arrayElementSizeInBytes))
    stack.pushValue(NumberValue.I32(elementsToCopy - 1))

    ArrayInitDataExecutorImpl(store, stack, typeIndex, dataIndex, definedTypeExpander, arraySetExecutor).bind()
}
