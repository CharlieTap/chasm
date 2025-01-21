package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.invoker.ext.dataAddress
import io.github.charlietap.chasm.executor.invoker.ext.definedType
import io.github.charlietap.chasm.executor.invoker.ext.valueFromBytes
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.data
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.ext.bitWidth

internal fun ArrayNewDataExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewData,
) =
    ArrayNewDataExecutor(
        context = context,
        instruction = instruction,
        definedTypeExpander = ::DefinedTypeExpander,
        arrayNewFixedExecutor = ::ArrayNewFixedExecutor,
    )

internal inline fun ArrayNewDataExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewData,
    crossinline definedTypeExpander: DefinedTypeExpander,
    crossinline arrayNewFixedExecutor: Executor<AggregateInstruction.ArrayNewFixed>,
) {

    val (stack, store) = context
    val (typeIndex, dataIndex) = instruction
    val frame = stack.peekFrame()
    val definedType = frame.instance.definedType(typeIndex)

    val arrayType = definedTypeExpander(definedType).arrayType()
    val dataAddress = frame.instance
        .dataAddress(dataIndex)

    val dataInstance = store.data(dataAddress)

    val arrayLength = stack.popI32()
    val arrayStartOffsetInSegment = stack.popI32()

    val arrayElementSizeInBytes = arrayType.fieldType
        .bitWidth()
        .toResultOr {
            InvocationError.UnobservableBitWidth
        }.bind() / 8
    val arrayEndOffsetInSegment = arrayStartOffsetInSegment + (arrayLength * arrayElementSizeInBytes)

    if (arrayEndOffsetInSegment > dataInstance.bytes.size) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }

    val byteArray = dataInstance.bytes.sliceArray(arrayStartOffsetInSegment until arrayEndOffsetInSegment)

    for (i in byteArray.indices step arrayElementSizeInBytes) {
        val elementBytes = byteArray.sliceArray(i until i + arrayElementSizeInBytes)
        val value = arrayType.fieldType.valueFromBytes(elementBytes).bind()

        stack.push(value)
    }

    arrayNewFixedExecutor(context, AggregateInstruction.ArrayNewFixed(typeIndex, arrayLength.toUInt()))
}
