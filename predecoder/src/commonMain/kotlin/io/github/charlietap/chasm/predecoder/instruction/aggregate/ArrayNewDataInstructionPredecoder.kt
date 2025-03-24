package io.github.charlietap.chasm.predecoder.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewDataDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.dataAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.data
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction.ArrayNewData
import io.github.charlietap.chasm.type.ext.arrayType
import io.github.charlietap.chasm.type.ext.bitWidth

internal fun ArrayNewDataInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayNewData,
): Result<DispatchableInstruction, ModuleTrapError> =
    ArrayNewDataInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::ArrayNewDataDispatcher,
    )

internal inline fun ArrayNewDataInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateInstruction.ArrayNewData,
    crossinline dispatcher: Dispatcher<ArrayNewData>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val dataAddress = context.instance.dataAddress(instruction.dataIndex).bind()
    val dataInstance = context.store.data(dataAddress)
    val definedType = context.types[instruction.typeIndex.idx]
    val arrayType = definedType.asSubType.compositeType.arrayType() ?: Err(
        InvocationError.ArrayCompositeTypeExpected,
    ).bind()
    val fieldWidthInBytes = arrayType.fieldType.bitWidth()?.let { sizeInBits ->
        sizeInBits / 8
    } ?: throw InvocationException(InvocationError.UnobservableBitWidth)

    dispatcher(ArrayNewData(definedType, arrayType, dataInstance, fieldWidthInBytes))
}
