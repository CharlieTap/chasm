package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.valueFromBytes
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.ext.bitWidth

internal fun ArrayNewDataExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewData,
) = ArrayNewDataExecutor(
    context = context,
    instruction = instruction,
    arrayNewFixedExecutor = ::ArrayNewFixedExecutor,
)

internal inline fun ArrayNewDataExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewData,
    crossinline arrayNewFixedExecutor: Executor<AggregateInstruction.ArrayNewFixed>,
) {
    val stack = context.vstack

    val arrayType = instruction.arrayType
    val dataInstance = instruction.dataInstance
    val byteArray = dataInstance.bytes

    val arrayLength = stack.popI32()
    val arrayStartOffsetInSegment = stack.popI32()

    val arrayElementSizeInBytes = arrayType.fieldType.bitWidth()?.let {
        it / 8
    } ?: throw InvocationException(InvocationError.UnobservableBitWidth)

    val arrayEndOffsetInSegment = arrayStartOffsetInSegment + (arrayLength * arrayElementSizeInBytes)
    if (arrayEndOffsetInSegment > byteArray.size) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }

    repeat(arrayLength) { offset ->
        val elementOffset = arrayStartOffsetInSegment + (offset * arrayElementSizeInBytes)
        val value = arrayType.fieldType.valueFromBytes(byteArray, elementOffset)

        stack.push(value)
    }

    arrayNewFixedExecutor(context, AggregateInstruction.ArrayNewFixed(instruction.definedType, instruction.arrayType, arrayLength.toUInt()))
}
