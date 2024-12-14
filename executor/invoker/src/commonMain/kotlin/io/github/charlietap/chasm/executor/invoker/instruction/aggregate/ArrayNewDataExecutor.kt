package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.executor.invoker.ext.valueFromBytes
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.data
import io.github.charlietap.chasm.executor.runtime.ext.dataAddress
import io.github.charlietap.chasm.executor.runtime.ext.definedType
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.ext.bitWidth

internal fun ArrayNewDataExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewData,
): Result<Unit, InvocationError> =
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
): Result<Unit, InvocationError> = binding {

    val (stack, store) = context
    val (typeIndex, dataIndex) = instruction
    val frame = stack.peekFrame().bind()
    val definedType = frame.instance
        .definedType(typeIndex)
        .bind()

    val arrayType = definedTypeExpander(definedType).arrayType().bind()
    val dataAddress = frame.instance
        .dataAddress(dataIndex)
        .bind()
    val dataInstance = store.data(dataAddress).bind()

    val arrayLength = stack.popI32().bind()
    val arrayStartOffsetInSegment = stack.popI32().bind()

    val arrayElementSizeInBytes = arrayType.fieldType
        .bitWidth()
        .toResultOr {
            InvocationError.UnobservableBitWidth
        }.bind() / 8
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

    arrayNewFixedExecutor(context, AggregateInstruction.ArrayNewFixed(typeIndex, arrayLength.toUInt())).bind()
}
