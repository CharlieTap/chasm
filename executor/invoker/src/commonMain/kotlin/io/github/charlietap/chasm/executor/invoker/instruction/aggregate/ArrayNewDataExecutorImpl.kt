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
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.bitWidth
import io.github.charlietap.chasm.executor.runtime.ext.data
import io.github.charlietap.chasm.executor.runtime.ext.dataAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpanderImpl

internal fun ArrayNewDataExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
    dataIndex: Index.DataIndex,
): Result<Unit, InvocationError> =
    ArrayNewDataExecutorImpl(
        store = store,
        stack = stack,
        typeIndex = typeIndex,
        dataIndex = dataIndex,
        definedTypeExpander = ::DefinedTypeExpanderImpl,
        arrayNewFixedExecutor = ::ArrayNewFixedExecutorImpl,
    )

internal inline fun ArrayNewDataExecutorImpl(
    store: Store,
    stack: Stack,
    typeIndex: Index.TypeIndex,
    dataIndex: Index.DataIndex,
    crossinline definedTypeExpander: DefinedTypeExpander,
    crossinline arrayNewFixedExecutor: ArrayNewFixedExecutor,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrame().bind()
    val definedType = frame.state.module.types[typeIndex.index()]

    val arrayType = definedTypeExpander(definedType).arrayType().bind()
    val dataAddress = frame.state.module.dataAddress(dataIndex.index()).bind()
    val dataInstance = store.data(dataAddress).bind()

    val arrayLength = stack.popI32().bind()
    val arrayStartOffsetInSegment = stack.popI32().bind()

    val arrayElementSizeInBytes = arrayType.fieldType.bitWidth().bind() / 8
    val arrayEndOffsetInSegment = arrayStartOffsetInSegment + (arrayLength * arrayElementSizeInBytes)

    if (arrayEndOffsetInSegment > dataInstance.bytes.size) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    val byteArray = dataInstance.bytes.sliceArray(arrayStartOffsetInSegment until arrayEndOffsetInSegment)

    for (i in byteArray.indices step arrayElementSizeInBytes) {
        val elementBytes = byteArray.sliceArray(i until i + arrayElementSizeInBytes)
        val value = arrayType.fieldType.valueFromBytes(elementBytes).bind()

        stack.pushValue(value)
    }

    arrayNewFixedExecutor(store, stack, typeIndex, arrayLength.toUInt())
}
