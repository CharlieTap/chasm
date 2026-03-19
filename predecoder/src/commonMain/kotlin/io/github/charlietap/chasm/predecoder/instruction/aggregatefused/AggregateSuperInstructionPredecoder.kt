package io.github.charlietap.chasm.predecoder.instruction.aggregatefused

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.AnyConvertExternDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayCopyDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayFillDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayGetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayGetSignedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayGetUnsignedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayInitDataDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayInitElementDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayLenDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayNewDataDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayNewDefaultDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayNewDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayNewElementDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArrayNewFixedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ArraySetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.ExternConvertAnyDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.I31GetSignedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.I31GetUnsignedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.RefI31Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.StructGetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.StructGetSignedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.StructGetUnsignedDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.StructNewDefaultDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.StructNewDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused.StructSetDispatcher
import io.github.charlietap.chasm.ir.instruction.AggregateSuperInstruction
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.dataAddress
import io.github.charlietap.chasm.predecoder.ext.elementAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.data
import io.github.charlietap.chasm.runtime.ext.default
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.ext.arrayType
import io.github.charlietap.chasm.type.ext.bitWidth
import io.github.charlietap.chasm.type.ext.structType
import io.github.charlietap.chasm.runtime.instruction.AggregateSuperInstruction as RuntimeFusedAggregateInstruction

internal fun AggregateSuperInstructionPredecoder(
    context: PredecodingContext,
    instruction: AggregateSuperInstruction,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is AggregateSuperInstruction.ArrayCopy -> strictArrayCopyInstruction(instruction)
        is AggregateSuperInstruction.ArrayFill -> strictArrayFillInstruction(instruction)
        is AggregateSuperInstruction.ArrayGet -> strictArrayGetInstruction(instruction)
        is AggregateSuperInstruction.ArrayGetSigned -> strictArrayGetSignedInstruction(instruction)
        is AggregateSuperInstruction.ArrayGetUnsigned -> strictArrayGetUnsignedInstruction(instruction)
        is AggregateSuperInstruction.ArrayLen -> strictArrayLenInstruction(instruction)
        is AggregateSuperInstruction.ArrayNew -> strictArrayNewInstruction(context, instruction).bind()
        is AggregateSuperInstruction.ArrayNewDefault -> strictArrayNewDefaultInstruction(context, instruction).bind()
        is AggregateSuperInstruction.ArrayNewData -> strictArrayNewDataInstruction(context, instruction).bind()
        is AggregateSuperInstruction.ArrayNewElement -> strictArrayNewElementInstruction(context, instruction).bind()
        is AggregateSuperInstruction.ArrayNewFixed -> strictArrayNewFixedInstruction(context, instruction).bind()
        is AggregateSuperInstruction.ArraySet -> strictArraySetInstruction(instruction)
        is AggregateSuperInstruction.ArrayInitData -> strictArrayInitDataInstruction(context, instruction).bind()
        is AggregateSuperInstruction.ArrayInitElement -> strictArrayInitElementInstruction(context, instruction).bind()
        is AggregateSuperInstruction.RefI31 -> strictRefI31Instruction(instruction)
        is AggregateSuperInstruction.I31GetSigned -> strictI31GetSignedInstruction(instruction)
        is AggregateSuperInstruction.I31GetUnsigned -> strictI31GetUnsignedInstruction(instruction)
        is AggregateSuperInstruction.AnyConvertExtern -> strictAnyConvertExternInstruction(instruction)
        is AggregateSuperInstruction.ExternConvertAny -> strictExternConvertAnyInstruction(instruction)
        is AggregateSuperInstruction.StructGet -> strictStructGetInstruction(instruction)
        is AggregateSuperInstruction.StructGetSigned -> strictStructGetSignedInstruction(instruction)
        is AggregateSuperInstruction.StructGetUnsigned -> strictStructGetUnsignedInstruction(instruction)
        is AggregateSuperInstruction.StructNew -> strictStructNewInstruction(context, instruction).bind()
        is AggregateSuperInstruction.StructNewDefault -> strictStructNewDefaultInstruction(context, instruction).bind()
        is AggregateSuperInstruction.StructSet -> strictStructSetInstruction(instruction)
    }
}

private fun strictArrayCopyInstruction(
    instruction: AggregateSuperInstruction.ArrayCopy,
): DispatchableInstruction {
    val sourceAddressSlot = strictAggregateOperandSlot(instruction.sourceAddress)
    val destinationAddressSlot = strictAggregateOperandSlot(instruction.destinationAddress)
    val elementsToCopyImmediate = strictAggregateIndexImmediate(instruction.elementsToCopy)
    val elementsToCopySlot = strictAggregateOperandSlot(instruction.elementsToCopy)
    val sourceOffsetImmediate = strictAggregateIndexImmediate(instruction.sourceOffset)
    val sourceOffsetSlot = strictAggregateOperandSlot(instruction.sourceOffset)
    val destinationOffsetImmediate = strictAggregateIndexImmediate(instruction.destinationOffset)
    val destinationOffsetSlot = strictAggregateOperandSlot(instruction.destinationOffset)

    return when {
        sourceAddressSlot == null || destinationAddressSlot == null -> unsupportedUnloweredAggregateInstruction()
        elementsToCopyImmediate != null && sourceOffsetImmediate != null && destinationOffsetImmediate != null -> {
            ArrayCopyDispatcher(
                RuntimeFusedAggregateInstruction.ArrayCopyIii(
                    elementsToCopy = elementsToCopyImmediate,
                    sourceOffset = sourceOffsetImmediate,
                    destinationOffset = destinationOffsetImmediate,
                    sourceAddressSlot = sourceAddressSlot,
                    destinationAddressSlot = destinationAddressSlot,
                ),
            )
        }
        elementsToCopyImmediate != null && sourceOffsetImmediate != null && destinationOffsetSlot != null -> {
            ArrayCopyDispatcher(
                RuntimeFusedAggregateInstruction.ArrayCopyIis(
                    elementsToCopy = elementsToCopyImmediate,
                    sourceOffset = sourceOffsetImmediate,
                    destinationOffsetSlot = destinationOffsetSlot,
                    sourceAddressSlot = sourceAddressSlot,
                    destinationAddressSlot = destinationAddressSlot,
                ),
            )
        }
        elementsToCopyImmediate != null && sourceOffsetSlot != null && destinationOffsetImmediate != null -> {
            ArrayCopyDispatcher(
                RuntimeFusedAggregateInstruction.ArrayCopyIsi(
                    elementsToCopy = elementsToCopyImmediate,
                    sourceOffsetSlot = sourceOffsetSlot,
                    destinationOffset = destinationOffsetImmediate,
                    sourceAddressSlot = sourceAddressSlot,
                    destinationAddressSlot = destinationAddressSlot,
                ),
            )
        }
        elementsToCopyImmediate != null && sourceOffsetSlot != null && destinationOffsetSlot != null -> {
            ArrayCopyDispatcher(
                RuntimeFusedAggregateInstruction.ArrayCopyIss(
                    elementsToCopy = elementsToCopyImmediate,
                    sourceOffsetSlot = sourceOffsetSlot,
                    destinationOffsetSlot = destinationOffsetSlot,
                    sourceAddressSlot = sourceAddressSlot,
                    destinationAddressSlot = destinationAddressSlot,
                ),
            )
        }
        elementsToCopySlot != null && sourceOffsetImmediate != null && destinationOffsetImmediate != null -> {
            ArrayCopyDispatcher(
                RuntimeFusedAggregateInstruction.ArrayCopySii(
                    elementsToCopySlot = elementsToCopySlot,
                    sourceOffset = sourceOffsetImmediate,
                    destinationOffset = destinationOffsetImmediate,
                    sourceAddressSlot = sourceAddressSlot,
                    destinationAddressSlot = destinationAddressSlot,
                ),
            )
        }
        elementsToCopySlot != null && sourceOffsetImmediate != null && destinationOffsetSlot != null -> {
            ArrayCopyDispatcher(
                RuntimeFusedAggregateInstruction.ArrayCopySis(
                    elementsToCopySlot = elementsToCopySlot,
                    sourceOffset = sourceOffsetImmediate,
                    destinationOffsetSlot = destinationOffsetSlot,
                    sourceAddressSlot = sourceAddressSlot,
                    destinationAddressSlot = destinationAddressSlot,
                ),
            )
        }
        elementsToCopySlot != null && sourceOffsetSlot != null && destinationOffsetImmediate != null -> {
            ArrayCopyDispatcher(
                RuntimeFusedAggregateInstruction.ArrayCopySsi(
                    elementsToCopySlot = elementsToCopySlot,
                    sourceOffsetSlot = sourceOffsetSlot,
                    destinationOffset = destinationOffsetImmediate,
                    sourceAddressSlot = sourceAddressSlot,
                    destinationAddressSlot = destinationAddressSlot,
                ),
            )
        }
        elementsToCopySlot != null && sourceOffsetSlot != null && destinationOffsetSlot != null -> {
            ArrayCopyDispatcher(
                RuntimeFusedAggregateInstruction.ArrayCopySss(
                    elementsToCopySlot = elementsToCopySlot,
                    sourceOffsetSlot = sourceOffsetSlot,
                    destinationOffsetSlot = destinationOffsetSlot,
                    sourceAddressSlot = sourceAddressSlot,
                    destinationAddressSlot = destinationAddressSlot,
                ),
            )
        }
        else -> unsupportedUnloweredAggregateInstruction()
    }
}

private fun strictArrayFillInstruction(
    instruction: AggregateSuperInstruction.ArrayFill,
): DispatchableInstruction {
    val addressSlot = strictAggregateOperandSlot(instruction.address)
    val elementsToFillImmediate = strictAggregateIndexImmediate(instruction.elementsToFill)
    val elementsToFillSlot = strictAggregateOperandSlot(instruction.elementsToFill)
    val fillValueImmediate = strictAggregateImmediate(instruction.fillValue)
    val fillValueSlot = strictAggregateOperandSlot(instruction.fillValue)
    val arrayElementOffsetImmediate = strictAggregateIndexImmediate(instruction.arrayElementOffset)
    val arrayElementOffsetSlot = strictAggregateOperandSlot(instruction.arrayElementOffset)

    return when {
        addressSlot == null -> unsupportedUnloweredAggregateInstruction()
        elementsToFillImmediate != null && fillValueImmediate != null && arrayElementOffsetImmediate != null -> {
            ArrayFillDispatcher(
                RuntimeFusedAggregateInstruction.ArrayFillIii(
                    elementsToFill = elementsToFillImmediate,
                    fillValue = fillValueImmediate,
                    arrayElementOffset = arrayElementOffsetImmediate,
                    addressSlot = addressSlot,
                ),
            )
        }
        elementsToFillImmediate != null && fillValueImmediate != null && arrayElementOffsetSlot != null -> {
            ArrayFillDispatcher(
                RuntimeFusedAggregateInstruction.ArrayFillIis(
                    elementsToFill = elementsToFillImmediate,
                    fillValue = fillValueImmediate,
                    arrayElementOffsetSlot = arrayElementOffsetSlot,
                    addressSlot = addressSlot,
                ),
            )
        }
        elementsToFillImmediate != null && fillValueSlot != null && arrayElementOffsetImmediate != null -> {
            ArrayFillDispatcher(
                RuntimeFusedAggregateInstruction.ArrayFillIsi(
                    elementsToFill = elementsToFillImmediate,
                    fillValueSlot = fillValueSlot,
                    arrayElementOffset = arrayElementOffsetImmediate,
                    addressSlot = addressSlot,
                ),
            )
        }
        elementsToFillImmediate != null && fillValueSlot != null && arrayElementOffsetSlot != null -> {
            ArrayFillDispatcher(
                RuntimeFusedAggregateInstruction.ArrayFillIss(
                    elementsToFill = elementsToFillImmediate,
                    fillValueSlot = fillValueSlot,
                    arrayElementOffsetSlot = arrayElementOffsetSlot,
                    addressSlot = addressSlot,
                ),
            )
        }
        elementsToFillSlot != null && fillValueImmediate != null && arrayElementOffsetImmediate != null -> {
            ArrayFillDispatcher(
                RuntimeFusedAggregateInstruction.ArrayFillSii(
                    elementsToFillSlot = elementsToFillSlot,
                    fillValue = fillValueImmediate,
                    arrayElementOffset = arrayElementOffsetImmediate,
                    addressSlot = addressSlot,
                ),
            )
        }
        elementsToFillSlot != null && fillValueImmediate != null && arrayElementOffsetSlot != null -> {
            ArrayFillDispatcher(
                RuntimeFusedAggregateInstruction.ArrayFillSis(
                    elementsToFillSlot = elementsToFillSlot,
                    fillValue = fillValueImmediate,
                    arrayElementOffsetSlot = arrayElementOffsetSlot,
                    addressSlot = addressSlot,
                ),
            )
        }
        elementsToFillSlot != null && fillValueSlot != null && arrayElementOffsetImmediate != null -> {
            ArrayFillDispatcher(
                RuntimeFusedAggregateInstruction.ArrayFillSsi(
                    elementsToFillSlot = elementsToFillSlot,
                    fillValueSlot = fillValueSlot,
                    arrayElementOffset = arrayElementOffsetImmediate,
                    addressSlot = addressSlot,
                ),
            )
        }
        elementsToFillSlot != null && fillValueSlot != null && arrayElementOffsetSlot != null -> {
            ArrayFillDispatcher(
                RuntimeFusedAggregateInstruction.ArrayFillSss(
                    elementsToFillSlot = elementsToFillSlot,
                    fillValueSlot = fillValueSlot,
                    arrayElementOffsetSlot = arrayElementOffsetSlot,
                    addressSlot = addressSlot,
                ),
            )
        }
        else -> unsupportedUnloweredAggregateInstruction()
    }
}

private fun strictArrayGetInstruction(
    instruction: AggregateSuperInstruction.ArrayGet,
): DispatchableInstruction {
    val addressSlot = strictAggregateOperandSlot(instruction.address)
    val destinationSlot = strictAggregateDestinationSlot(instruction.destination)
    val fieldImmediate = strictAggregateIndexImmediate(instruction.field)
    val fieldSlot = strictAggregateOperandSlot(instruction.field)

    return when {
        addressSlot == null || destinationSlot == null -> unsupportedUnloweredAggregateInstruction()
        fieldImmediate != null -> {
            ArrayGetDispatcher(
                RuntimeFusedAggregateInstruction.ArrayGetI(
                    addressSlot = addressSlot,
                    field = fieldImmediate,
                    destinationSlot = destinationSlot,
                ),
            )
        }
        fieldSlot != null -> {
            ArrayGetDispatcher(
                RuntimeFusedAggregateInstruction.ArrayGetS(
                    addressSlot = addressSlot,
                    fieldSlot = fieldSlot,
                    destinationSlot = destinationSlot,
                ),
            )
        }
        else -> unsupportedUnloweredAggregateInstruction()
    }
}

private fun strictArrayGetSignedInstruction(
    instruction: AggregateSuperInstruction.ArrayGetSigned,
): DispatchableInstruction {
    val addressSlot = strictAggregateOperandSlot(instruction.address)
    val destinationSlot = strictAggregateDestinationSlot(instruction.destination)
    val fieldImmediate = strictAggregateIndexImmediate(instruction.field)
    val fieldSlot = strictAggregateOperandSlot(instruction.field)

    return when {
        addressSlot == null || destinationSlot == null -> unsupportedUnloweredAggregateInstruction()
        fieldImmediate != null -> {
            ArrayGetSignedDispatcher(
                RuntimeFusedAggregateInstruction.ArrayGetSignedI(
                    addressSlot = addressSlot,
                    field = fieldImmediate,
                    destinationSlot = destinationSlot,
                ),
            )
        }
        fieldSlot != null -> {
            ArrayGetSignedDispatcher(
                RuntimeFusedAggregateInstruction.ArrayGetSignedS(
                    addressSlot = addressSlot,
                    fieldSlot = fieldSlot,
                    destinationSlot = destinationSlot,
                ),
            )
        }
        else -> unsupportedUnloweredAggregateInstruction()
    }
}

private fun strictArrayGetUnsignedInstruction(
    instruction: AggregateSuperInstruction.ArrayGetUnsigned,
): DispatchableInstruction {
    val addressSlot = strictAggregateOperandSlot(instruction.address)
    val destinationSlot = strictAggregateDestinationSlot(instruction.destination)
    val fieldImmediate = strictAggregateIndexImmediate(instruction.field)
    val fieldSlot = strictAggregateOperandSlot(instruction.field)

    return when {
        addressSlot == null || destinationSlot == null -> unsupportedUnloweredAggregateInstruction()
        fieldImmediate != null -> {
            ArrayGetUnsignedDispatcher(
                RuntimeFusedAggregateInstruction.ArrayGetUnsignedI(
                    addressSlot = addressSlot,
                    field = fieldImmediate,
                    destinationSlot = destinationSlot,
                ),
            )
        }
        fieldSlot != null -> {
            ArrayGetUnsignedDispatcher(
                RuntimeFusedAggregateInstruction.ArrayGetUnsignedS(
                    addressSlot = addressSlot,
                    fieldSlot = fieldSlot,
                    destinationSlot = destinationSlot,
                ),
            )
        }
        else -> unsupportedUnloweredAggregateInstruction()
    }
}

private fun strictArrayLenInstruction(
    instruction: AggregateSuperInstruction.ArrayLen,
): DispatchableInstruction {
    val addressSlot = strictAggregateOperandSlot(instruction.address)
    val destinationSlot = strictAggregateDestinationSlot(instruction.destination)

    return when {
        addressSlot != null && destinationSlot != null -> {
            ArrayLenDispatcher(
                RuntimeFusedAggregateInstruction.ArrayLenS(
                    addressSlot = addressSlot,
                    destinationSlot = destinationSlot,
                ),
            )
        }
        else -> unsupportedUnloweredAggregateInstruction()
    }
}

private fun strictArrayNewInstruction(
    context: PredecodingContext,
    instruction: AggregateSuperInstruction.ArrayNew,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val destinationSlot = strictAggregateDestinationSlot(instruction.destination) ?: return@binding unsupportedUnloweredAggregateInstruction()
    val arrayType = resolveArrayType(context, instruction.typeIndex).bind()
    val rtt = context.instance.runtimeTypes[instruction.typeIndex.idx]
    val sizeImmediate = strictAggregateIndexImmediate(instruction.size)
    val sizeSlot = strictAggregateOperandSlot(instruction.size)
    val valueImmediate = strictAggregateImmediate(instruction.value)
    val valueSlot = strictAggregateOperandSlot(instruction.value)

    when {
        sizeImmediate != null && valueImmediate != null -> {
            ArrayNewDispatcher(
                RuntimeFusedAggregateInstruction.ArrayNewIi(
                    size = sizeImmediate,
                    value = valueImmediate,
                    destinationSlot = destinationSlot,
                    rtt = rtt,
                    arrayType = arrayType,
                ),
            )
        }
        sizeImmediate != null && valueSlot != null -> {
            ArrayNewDispatcher(
                RuntimeFusedAggregateInstruction.ArrayNewIs(
                    size = sizeImmediate,
                    valueSlot = valueSlot,
                    destinationSlot = destinationSlot,
                    rtt = rtt,
                    arrayType = arrayType,
                ),
            )
        }
        sizeSlot != null && valueImmediate != null -> {
            ArrayNewDispatcher(
                RuntimeFusedAggregateInstruction.ArrayNewSi(
                    sizeSlot = sizeSlot,
                    value = valueImmediate,
                    destinationSlot = destinationSlot,
                    rtt = rtt,
                    arrayType = arrayType,
                ),
            )
        }
        sizeSlot != null && valueSlot != null -> {
            ArrayNewDispatcher(
                RuntimeFusedAggregateInstruction.ArrayNewSs(
                    sizeSlot = sizeSlot,
                    valueSlot = valueSlot,
                    destinationSlot = destinationSlot,
                    rtt = rtt,
                    arrayType = arrayType,
                ),
            )
        }
        else -> unsupportedUnloweredAggregateInstruction()
    }
}

private fun strictArrayNewDefaultInstruction(
    context: PredecodingContext,
    instruction: AggregateSuperInstruction.ArrayNewDefault,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val destinationSlot = strictAggregateDestinationSlot(instruction.destination) ?: return@binding unsupportedUnloweredAggregateInstruction()
    val arrayType = resolveArrayType(context, instruction.typeIndex).bind()
    val rtt = context.instance.runtimeTypes[instruction.typeIndex.idx]
    val field = arrayType.fieldType.default()
    val sizeImmediate = strictAggregateIndexImmediate(instruction.size)
    val sizeSlot = strictAggregateOperandSlot(instruction.size)

    when {
        sizeImmediate != null -> {
            ArrayNewDefaultDispatcher(
                RuntimeFusedAggregateInstruction.ArrayNewDefaultI(
                    size = sizeImmediate,
                    destinationSlot = destinationSlot,
                    rtt = rtt,
                    arrayType = arrayType,
                    field = field,
                ),
            )
        }
        sizeSlot != null -> {
            ArrayNewDefaultDispatcher(
                RuntimeFusedAggregateInstruction.ArrayNewDefaultS(
                    sizeSlot = sizeSlot,
                    destinationSlot = destinationSlot,
                    rtt = rtt,
                    arrayType = arrayType,
                    field = field,
                ),
            )
        }
        else -> unsupportedUnloweredAggregateInstruction()
    }
}

private fun strictArrayNewDataInstruction(
    context: PredecodingContext,
    instruction: AggregateSuperInstruction.ArrayNewData,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val destinationSlot = strictAggregateDestinationSlot(instruction.destination) ?: return@binding unsupportedUnloweredAggregateInstruction()
    val arrayType = resolveArrayType(context, instruction.typeIndex).bind()
    val rtt = context.instance.runtimeTypes[instruction.typeIndex.idx]
    val dataAddress = context.instance.dataAddress(instruction.dataIndex).bind()
    val dataInstance = context.store.data(dataAddress)
    val fieldWidthInBytes = arrayType.fieldType.bitWidth()?.let { sizeInBits ->
        sizeInBits / 8
    } ?: throw InvocationException(InvocationError.UnobservableBitWidth)
    val sourceOffsetImmediate = strictAggregateIndexImmediate(instruction.sourceOffset)
    val sourceOffsetSlot = strictAggregateOperandSlot(instruction.sourceOffset)
    val arrayLengthImmediate = strictAggregateIndexImmediate(instruction.arrayLength)
    val arrayLengthSlot = strictAggregateOperandSlot(instruction.arrayLength)

    when {
        sourceOffsetImmediate != null && arrayLengthImmediate != null -> {
            ArrayNewDataDispatcher(
                RuntimeFusedAggregateInstruction.ArrayNewDataIi(
                    sourceOffset = sourceOffsetImmediate,
                    arrayLength = arrayLengthImmediate,
                    destinationSlot = destinationSlot,
                    rtt = rtt,
                    arrayType = arrayType,
                    dataInstance = dataInstance,
                    fieldWidthInBytes = fieldWidthInBytes,
                ),
            )
        }
        sourceOffsetImmediate != null && arrayLengthSlot != null -> {
            ArrayNewDataDispatcher(
                RuntimeFusedAggregateInstruction.ArrayNewDataIs(
                    sourceOffset = sourceOffsetImmediate,
                    arrayLengthSlot = arrayLengthSlot,
                    destinationSlot = destinationSlot,
                    rtt = rtt,
                    arrayType = arrayType,
                    dataInstance = dataInstance,
                    fieldWidthInBytes = fieldWidthInBytes,
                ),
            )
        }
        sourceOffsetSlot != null && arrayLengthImmediate != null -> {
            ArrayNewDataDispatcher(
                RuntimeFusedAggregateInstruction.ArrayNewDataSi(
                    sourceOffsetSlot = sourceOffsetSlot,
                    arrayLength = arrayLengthImmediate,
                    destinationSlot = destinationSlot,
                    rtt = rtt,
                    arrayType = arrayType,
                    dataInstance = dataInstance,
                    fieldWidthInBytes = fieldWidthInBytes,
                ),
            )
        }
        sourceOffsetSlot != null && arrayLengthSlot != null -> {
            ArrayNewDataDispatcher(
                RuntimeFusedAggregateInstruction.ArrayNewDataSs(
                    sourceOffsetSlot = sourceOffsetSlot,
                    arrayLengthSlot = arrayLengthSlot,
                    destinationSlot = destinationSlot,
                    rtt = rtt,
                    arrayType = arrayType,
                    dataInstance = dataInstance,
                    fieldWidthInBytes = fieldWidthInBytes,
                ),
            )
        }
        else -> unsupportedUnloweredAggregateInstruction()
    }
}

private fun strictArrayNewElementInstruction(
    context: PredecodingContext,
    instruction: AggregateSuperInstruction.ArrayNewElement,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val destinationSlot = strictAggregateDestinationSlot(instruction.destination) ?: return@binding unsupportedUnloweredAggregateInstruction()
    val arrayType = resolveArrayType(context, instruction.typeIndex).bind()
    val rtt = context.instance.runtimeTypes[instruction.typeIndex.idx]
    val elementAddress = context.instance.elementAddress(instruction.elementIndex).bind()
    val elementInstance = context.store.element(elementAddress)
    val sourceOffsetImmediate = strictAggregateIndexImmediate(instruction.sourceOffset)
    val sourceOffsetSlot = strictAggregateOperandSlot(instruction.sourceOffset)
    val arrayLengthImmediate = strictAggregateIndexImmediate(instruction.arrayLength)
    val arrayLengthSlot = strictAggregateOperandSlot(instruction.arrayLength)

    when {
        sourceOffsetImmediate != null && arrayLengthImmediate != null -> {
            ArrayNewElementDispatcher(
                RuntimeFusedAggregateInstruction.ArrayNewElementIi(
                    sourceOffset = sourceOffsetImmediate,
                    arrayLength = arrayLengthImmediate,
                    destinationSlot = destinationSlot,
                    rtt = rtt,
                    arrayType = arrayType,
                    elementInstance = elementInstance,
                ),
            )
        }
        sourceOffsetImmediate != null && arrayLengthSlot != null -> {
            ArrayNewElementDispatcher(
                RuntimeFusedAggregateInstruction.ArrayNewElementIs(
                    sourceOffset = sourceOffsetImmediate,
                    arrayLengthSlot = arrayLengthSlot,
                    destinationSlot = destinationSlot,
                    rtt = rtt,
                    arrayType = arrayType,
                    elementInstance = elementInstance,
                ),
            )
        }
        sourceOffsetSlot != null && arrayLengthImmediate != null -> {
            ArrayNewElementDispatcher(
                RuntimeFusedAggregateInstruction.ArrayNewElementSi(
                    sourceOffsetSlot = sourceOffsetSlot,
                    arrayLength = arrayLengthImmediate,
                    destinationSlot = destinationSlot,
                    rtt = rtt,
                    arrayType = arrayType,
                    elementInstance = elementInstance,
                ),
            )
        }
        sourceOffsetSlot != null && arrayLengthSlot != null -> {
            ArrayNewElementDispatcher(
                RuntimeFusedAggregateInstruction.ArrayNewElementSs(
                    sourceOffsetSlot = sourceOffsetSlot,
                    arrayLengthSlot = arrayLengthSlot,
                    destinationSlot = destinationSlot,
                    rtt = rtt,
                    arrayType = arrayType,
                    elementInstance = elementInstance,
                ),
            )
        }
        else -> unsupportedUnloweredAggregateInstruction()
    }
}

private fun strictArrayNewFixedInstruction(
    context: PredecodingContext,
    instruction: AggregateSuperInstruction.ArrayNewFixed,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val destinationSlot = strictAggregateDestinationSlot(instruction.destination) ?: return@binding unsupportedUnloweredAggregateInstruction()
    val valueSlots = strictAggregateSlots(instruction.valueSlots, instruction.size, "array.new_fixed")
    val arrayType = resolveArrayType(context, instruction.typeIndex).bind()
    val rtt = context.instance.runtimeTypes[instruction.typeIndex.idx]

    ArrayNewFixedDispatcher(
        RuntimeFusedAggregateInstruction.ArrayNewFixedS(
            valueSlots = valueSlots,
            destinationSlot = destinationSlot,
            rtt = rtt,
            arrayType = arrayType,
            size = instruction.size,
        ),
    )
}

private fun strictArraySetInstruction(
    instruction: AggregateSuperInstruction.ArraySet,
): DispatchableInstruction {
    val addressSlot = strictAggregateOperandSlot(instruction.address)
    val valueImmediate = strictAggregateImmediate(instruction.value)
    val valueSlot = strictAggregateOperandSlot(instruction.value)
    val fieldImmediate = strictAggregateIndexImmediate(instruction.field)
    val fieldSlot = strictAggregateOperandSlot(instruction.field)

    return when {
        addressSlot == null -> unsupportedUnloweredAggregateInstruction()
        valueImmediate != null && fieldImmediate != null -> {
            ArraySetDispatcher(
                RuntimeFusedAggregateInstruction.ArraySetIi(
                    value = valueImmediate,
                    field = fieldImmediate,
                    addressSlot = addressSlot,
                ),
            )
        }
        valueImmediate != null && fieldSlot != null -> {
            ArraySetDispatcher(
                RuntimeFusedAggregateInstruction.ArraySetIs(
                    value = valueImmediate,
                    fieldSlot = fieldSlot,
                    addressSlot = addressSlot,
                ),
            )
        }
        valueSlot != null && fieldImmediate != null -> {
            ArraySetDispatcher(
                RuntimeFusedAggregateInstruction.ArraySetSi(
                    valueSlot = valueSlot,
                    field = fieldImmediate,
                    addressSlot = addressSlot,
                ),
            )
        }
        valueSlot != null && fieldSlot != null -> {
            ArraySetDispatcher(
                RuntimeFusedAggregateInstruction.ArraySetSs(
                    valueSlot = valueSlot,
                    fieldSlot = fieldSlot,
                    addressSlot = addressSlot,
                ),
            )
        }
        else -> unsupportedUnloweredAggregateInstruction()
    }
}

private fun strictArrayInitDataInstruction(
    context: PredecodingContext,
    instruction: AggregateSuperInstruction.ArrayInitData,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val addressSlot = strictAggregateOperandSlot(instruction.address) ?: return@binding unsupportedUnloweredAggregateInstruction()
    val arrayType = resolveArrayType(context, instruction.typeIndex).bind()
    val dataAddress = context.instance.dataAddress(instruction.dataIndex).bind()
    val dataInstance = context.store.data(dataAddress)
    val fieldWidthInBytes = arrayType.fieldType.bitWidth()?.let { sizeInBits ->
        sizeInBits / 8
    } ?: throw InvocationException(InvocationError.UnobservableBitWidth)

    strictAggregateI32TernaryOperands(
        first = instruction.elementsToCopy,
        second = instruction.sourceOffset,
        third = instruction.destinationOffset,
        instructionName = "array.init_data",
        iii = { elementsToCopy, sourceOffset, destinationOffset ->
            ArrayInitDataDispatcher(
                RuntimeFusedAggregateInstruction.ArrayInitDataIii(
                    elementsToCopy = elementsToCopy,
                    sourceOffset = sourceOffset,
                    destinationOffset = destinationOffset,
                    addressSlot = addressSlot,
                    dataInstance = dataInstance,
                    fieldWidthInBytes = fieldWidthInBytes,
                ),
            )
        },
        iis = { elementsToCopy, sourceOffset, destinationOffsetSlot ->
            ArrayInitDataDispatcher(
                RuntimeFusedAggregateInstruction.ArrayInitDataIis(
                    elementsToCopy = elementsToCopy,
                    sourceOffset = sourceOffset,
                    destinationOffsetSlot = destinationOffsetSlot,
                    addressSlot = addressSlot,
                    dataInstance = dataInstance,
                    fieldWidthInBytes = fieldWidthInBytes,
                ),
            )
        },
        isi = { elementsToCopy, sourceOffsetSlot, destinationOffset ->
            ArrayInitDataDispatcher(
                RuntimeFusedAggregateInstruction.ArrayInitDataIsi(
                    elementsToCopy = elementsToCopy,
                    sourceOffsetSlot = sourceOffsetSlot,
                    destinationOffset = destinationOffset,
                    addressSlot = addressSlot,
                    dataInstance = dataInstance,
                    fieldWidthInBytes = fieldWidthInBytes,
                ),
            )
        },
        iss = { elementsToCopy, sourceOffsetSlot, destinationOffsetSlot ->
            ArrayInitDataDispatcher(
                RuntimeFusedAggregateInstruction.ArrayInitDataIss(
                    elementsToCopy = elementsToCopy,
                    sourceOffsetSlot = sourceOffsetSlot,
                    destinationOffsetSlot = destinationOffsetSlot,
                    addressSlot = addressSlot,
                    dataInstance = dataInstance,
                    fieldWidthInBytes = fieldWidthInBytes,
                ),
            )
        },
        sii = { elementsToCopySlot, sourceOffset, destinationOffset ->
            ArrayInitDataDispatcher(
                RuntimeFusedAggregateInstruction.ArrayInitDataSii(
                    elementsToCopySlot = elementsToCopySlot,
                    sourceOffset = sourceOffset,
                    destinationOffset = destinationOffset,
                    addressSlot = addressSlot,
                    dataInstance = dataInstance,
                    fieldWidthInBytes = fieldWidthInBytes,
                ),
            )
        },
        sis = { elementsToCopySlot, sourceOffset, destinationOffsetSlot ->
            ArrayInitDataDispatcher(
                RuntimeFusedAggregateInstruction.ArrayInitDataSis(
                    elementsToCopySlot = elementsToCopySlot,
                    sourceOffset = sourceOffset,
                    destinationOffsetSlot = destinationOffsetSlot,
                    addressSlot = addressSlot,
                    dataInstance = dataInstance,
                    fieldWidthInBytes = fieldWidthInBytes,
                ),
            )
        },
        ssi = { elementsToCopySlot, sourceOffsetSlot, destinationOffset ->
            ArrayInitDataDispatcher(
                RuntimeFusedAggregateInstruction.ArrayInitDataSsi(
                    elementsToCopySlot = elementsToCopySlot,
                    sourceOffsetSlot = sourceOffsetSlot,
                    destinationOffset = destinationOffset,
                    addressSlot = addressSlot,
                    dataInstance = dataInstance,
                    fieldWidthInBytes = fieldWidthInBytes,
                ),
            )
        },
        sss = { elementsToCopySlot, sourceOffsetSlot, destinationOffsetSlot ->
            ArrayInitDataDispatcher(
                RuntimeFusedAggregateInstruction.ArrayInitDataSss(
                    elementsToCopySlot = elementsToCopySlot,
                    sourceOffsetSlot = sourceOffsetSlot,
                    destinationOffsetSlot = destinationOffsetSlot,
                    addressSlot = addressSlot,
                    dataInstance = dataInstance,
                    fieldWidthInBytes = fieldWidthInBytes,
                ),
            )
        },
    )
}

private fun strictArrayInitElementInstruction(
    context: PredecodingContext,
    instruction: AggregateSuperInstruction.ArrayInitElement,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val addressSlot = strictAggregateOperandSlot(instruction.address) ?: return@binding unsupportedUnloweredAggregateInstruction()
    val elementAddress = context.instance.elementAddress(instruction.elementIndex).bind()
    val elementInstance = context.store.element(elementAddress)

    strictAggregateI32TernaryOperands(
        first = instruction.elementsToCopy,
        second = instruction.sourceOffset,
        third = instruction.destinationOffset,
        instructionName = "array.init_elem",
        iii = { elementsToCopy, sourceOffset, destinationOffset ->
            ArrayInitElementDispatcher(
                RuntimeFusedAggregateInstruction.ArrayInitElementIii(
                    elementsToCopy = elementsToCopy,
                    sourceOffset = sourceOffset,
                    destinationOffset = destinationOffset,
                    addressSlot = addressSlot,
                    elementInstance = elementInstance,
                ),
            )
        },
        iis = { elementsToCopy, sourceOffset, destinationOffsetSlot ->
            ArrayInitElementDispatcher(
                RuntimeFusedAggregateInstruction.ArrayInitElementIis(
                    elementsToCopy = elementsToCopy,
                    sourceOffset = sourceOffset,
                    destinationOffsetSlot = destinationOffsetSlot,
                    addressSlot = addressSlot,
                    elementInstance = elementInstance,
                ),
            )
        },
        isi = { elementsToCopy, sourceOffsetSlot, destinationOffset ->
            ArrayInitElementDispatcher(
                RuntimeFusedAggregateInstruction.ArrayInitElementIsi(
                    elementsToCopy = elementsToCopy,
                    sourceOffsetSlot = sourceOffsetSlot,
                    destinationOffset = destinationOffset,
                    addressSlot = addressSlot,
                    elementInstance = elementInstance,
                ),
            )
        },
        iss = { elementsToCopy, sourceOffsetSlot, destinationOffsetSlot ->
            ArrayInitElementDispatcher(
                RuntimeFusedAggregateInstruction.ArrayInitElementIss(
                    elementsToCopy = elementsToCopy,
                    sourceOffsetSlot = sourceOffsetSlot,
                    destinationOffsetSlot = destinationOffsetSlot,
                    addressSlot = addressSlot,
                    elementInstance = elementInstance,
                ),
            )
        },
        sii = { elementsToCopySlot, sourceOffset, destinationOffset ->
            ArrayInitElementDispatcher(
                RuntimeFusedAggregateInstruction.ArrayInitElementSii(
                    elementsToCopySlot = elementsToCopySlot,
                    sourceOffset = sourceOffset,
                    destinationOffset = destinationOffset,
                    addressSlot = addressSlot,
                    elementInstance = elementInstance,
                ),
            )
        },
        sis = { elementsToCopySlot, sourceOffset, destinationOffsetSlot ->
            ArrayInitElementDispatcher(
                RuntimeFusedAggregateInstruction.ArrayInitElementSis(
                    elementsToCopySlot = elementsToCopySlot,
                    sourceOffset = sourceOffset,
                    destinationOffsetSlot = destinationOffsetSlot,
                    addressSlot = addressSlot,
                    elementInstance = elementInstance,
                ),
            )
        },
        ssi = { elementsToCopySlot, sourceOffsetSlot, destinationOffset ->
            ArrayInitElementDispatcher(
                RuntimeFusedAggregateInstruction.ArrayInitElementSsi(
                    elementsToCopySlot = elementsToCopySlot,
                    sourceOffsetSlot = sourceOffsetSlot,
                    destinationOffset = destinationOffset,
                    addressSlot = addressSlot,
                    elementInstance = elementInstance,
                ),
            )
        },
        sss = { elementsToCopySlot, sourceOffsetSlot, destinationOffsetSlot ->
            ArrayInitElementDispatcher(
                RuntimeFusedAggregateInstruction.ArrayInitElementSss(
                    elementsToCopySlot = elementsToCopySlot,
                    sourceOffsetSlot = sourceOffsetSlot,
                    destinationOffsetSlot = destinationOffsetSlot,
                    addressSlot = addressSlot,
                    elementInstance = elementInstance,
                ),
            )
        },
    )
}

private fun strictRefI31Instruction(
    instruction: AggregateSuperInstruction.RefI31,
): DispatchableInstruction {
    val destinationSlot = strictAggregateDestinationSlot(instruction.destination)
    val valueImmediate = strictAggregateIndexImmediate(instruction.value)
    val valueSlot = strictAggregateOperandSlot(instruction.value)

    return when {
        destinationSlot == null -> unsupportedUnloweredAggregateInstruction()
        valueImmediate != null -> RefI31Dispatcher(RuntimeFusedAggregateInstruction.RefI31I(valueImmediate, destinationSlot))
        valueSlot != null -> RefI31Dispatcher(RuntimeFusedAggregateInstruction.RefI31S(valueSlot, destinationSlot))
        else -> unsupportedUnloweredAggregateInstruction()
    }
}

private fun strictI31GetSignedInstruction(
    instruction: AggregateSuperInstruction.I31GetSigned,
): DispatchableInstruction {
    val valueSlot = strictAggregateOperandSlot(instruction.value)
    val destinationSlot = strictAggregateDestinationSlot(instruction.destination)

    return when {
        valueSlot != null && destinationSlot != null -> {
            I31GetSignedDispatcher(RuntimeFusedAggregateInstruction.I31GetSignedS(valueSlot, destinationSlot))
        }
        else -> unsupportedUnloweredAggregateInstruction()
    }
}

private fun strictI31GetUnsignedInstruction(
    instruction: AggregateSuperInstruction.I31GetUnsigned,
): DispatchableInstruction {
    val valueSlot = strictAggregateOperandSlot(instruction.value)
    val destinationSlot = strictAggregateDestinationSlot(instruction.destination)

    return when {
        valueSlot != null && destinationSlot != null -> {
            I31GetUnsignedDispatcher(RuntimeFusedAggregateInstruction.I31GetUnsignedS(valueSlot, destinationSlot))
        }
        else -> unsupportedUnloweredAggregateInstruction()
    }
}

private fun strictAnyConvertExternInstruction(
    instruction: AggregateSuperInstruction.AnyConvertExtern,
): DispatchableInstruction {
    val valueSlot = strictAggregateOperandSlot(instruction.value)
    val destinationSlot = strictAggregateDestinationSlot(instruction.destination)

    return when {
        valueSlot != null && destinationSlot != null -> {
            AnyConvertExternDispatcher(RuntimeFusedAggregateInstruction.AnyConvertExternS(valueSlot, destinationSlot))
        }
        else -> unsupportedUnloweredAggregateInstruction()
    }
}

private fun strictExternConvertAnyInstruction(
    instruction: AggregateSuperInstruction.ExternConvertAny,
): DispatchableInstruction {
    val valueSlot = strictAggregateOperandSlot(instruction.value)
    val destinationSlot = strictAggregateDestinationSlot(instruction.destination)

    return when {
        valueSlot != null && destinationSlot != null -> {
            ExternConvertAnyDispatcher(RuntimeFusedAggregateInstruction.ExternConvertAnyS(valueSlot, destinationSlot))
        }
        else -> unsupportedUnloweredAggregateInstruction()
    }
}

private fun strictStructGetInstruction(
    instruction: AggregateSuperInstruction.StructGet,
): DispatchableInstruction {
    val addressSlot = strictAggregateOperandSlot(instruction.address)
    val destinationSlot = strictAggregateDestinationSlot(instruction.destination)

    return when {
        addressSlot != null && destinationSlot != null -> {
            StructGetDispatcher(
                RuntimeFusedAggregateInstruction.StructGetS(
                    addressSlot = addressSlot,
                    destinationSlot = destinationSlot,
                    fieldIndex = instruction.fieldIndex.idx,
                ),
            )
        }
        else -> unsupportedUnloweredAggregateInstruction()
    }
}

private fun strictStructGetSignedInstruction(
    instruction: AggregateSuperInstruction.StructGetSigned,
): DispatchableInstruction {
    val addressSlot = strictAggregateOperandSlot(instruction.address)
    val destinationSlot = strictAggregateDestinationSlot(instruction.destination)

    return when {
        addressSlot != null && destinationSlot != null -> {
            StructGetSignedDispatcher(
                RuntimeFusedAggregateInstruction.StructGetSignedS(
                    addressSlot = addressSlot,
                    destinationSlot = destinationSlot,
                    fieldIndex = instruction.fieldIndex.idx,
                ),
            )
        }
        else -> unsupportedUnloweredAggregateInstruction()
    }
}

private fun strictStructGetUnsignedInstruction(
    instruction: AggregateSuperInstruction.StructGetUnsigned,
): DispatchableInstruction {
    val addressSlot = strictAggregateOperandSlot(instruction.address)
    val destinationSlot = strictAggregateDestinationSlot(instruction.destination)

    return when {
        addressSlot != null && destinationSlot != null -> {
            StructGetUnsignedDispatcher(
                RuntimeFusedAggregateInstruction.StructGetUnsignedS(
                    addressSlot = addressSlot,
                    destinationSlot = destinationSlot,
                    fieldIndex = instruction.fieldIndex.idx,
                ),
            )
        }
        else -> unsupportedUnloweredAggregateInstruction()
    }
}

private fun strictStructNewInstruction(
    context: PredecodingContext,
    instruction: AggregateSuperInstruction.StructNew,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val destinationSlot = strictAggregateDestinationSlot(instruction.destination) ?: return@binding unsupportedUnloweredAggregateInstruction()
    val structType = resolveStructType(context, instruction.typeIndex).bind()
    val fieldSlots = strictAggregateSlots(instruction.fieldSlots, structType.fields.size, "struct.new")
    val rtt = context.instance.runtimeTypes[instruction.typeIndex.idx]

    StructNewDispatcher(
        RuntimeFusedAggregateInstruction.StructNewS(
            fieldSlots = fieldSlots,
            destinationSlot = destinationSlot,
            rtt = rtt,
            structType = structType,
        ),
    )
}

private fun strictStructNewDefaultInstruction(
    context: PredecodingContext,
    instruction: AggregateSuperInstruction.StructNewDefault,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val destinationSlot = strictAggregateDestinationSlot(instruction.destination) ?: return@binding unsupportedUnloweredAggregateInstruction()
    val structType = resolveStructType(context, instruction.typeIndex).bind()
    val rtt = context.instance.runtimeTypes[instruction.typeIndex.idx]
    val fields = LongArray(structType.fields.size) { idx ->
        structType.fields[idx].default()
    }

    StructNewDefaultDispatcher(
        RuntimeFusedAggregateInstruction.StructNewDefaultS(
            destinationSlot = destinationSlot,
            rtt = rtt,
            structType = structType,
            fields = fields,
        ),
    )
}

private fun strictStructSetInstruction(
    instruction: AggregateSuperInstruction.StructSet,
): DispatchableInstruction {
    val addressSlot = strictAggregateOperandSlot(instruction.address)
    val valueImmediate = strictAggregateImmediate(instruction.value)
    val valueSlot = strictAggregateOperandSlot(instruction.value)

    return when {
        addressSlot == null -> unsupportedUnloweredAggregateInstruction()
        valueImmediate != null -> {
            StructSetDispatcher(
                RuntimeFusedAggregateInstruction.StructSetI(
                    value = valueImmediate,
                    addressSlot = addressSlot,
                    fieldIndex = instruction.fieldIndex.idx,
                ),
            )
        }
        valueSlot != null -> {
            StructSetDispatcher(
                RuntimeFusedAggregateInstruction.StructSetS(
                    valueSlot = valueSlot,
                    addressSlot = addressSlot,
                    fieldIndex = instruction.fieldIndex.idx,
                ),
            )
        }
        else -> unsupportedUnloweredAggregateInstruction()
    }
}

private fun strictAggregateImmediate(
    operand: FusedOperand,
): Long? = when (operand) {
    is FusedOperand.I32Const -> operand.const.toLong()
    is FusedOperand.I64Const -> operand.const
    is FusedOperand.F32Const -> operand.const.toRawBits().toLong()
    is FusedOperand.F64Const -> operand.const.toRawBits()
    else -> null
}

private fun strictAggregateIndexImmediate(
    operand: FusedOperand,
): Int? = strictAggregateImmediate(operand)?.toInt()

private inline fun strictAggregateI32TernaryOperands(
    first: FusedOperand,
    second: FusedOperand,
    third: FusedOperand,
    instructionName: String,
    crossinline iii: (Int, Int, Int) -> DispatchableInstruction,
    crossinline iis: (Int, Int, Int) -> DispatchableInstruction,
    crossinline isi: (Int, Int, Int) -> DispatchableInstruction,
    crossinline iss: (Int, Int, Int) -> DispatchableInstruction,
    crossinline sii: (Int, Int, Int) -> DispatchableInstruction,
    crossinline sis: (Int, Int, Int) -> DispatchableInstruction,
    crossinline ssi: (Int, Int, Int) -> DispatchableInstruction,
    crossinline sss: (Int, Int, Int) -> DispatchableInstruction,
): DispatchableInstruction {
    val firstSlot = strictAggregateOperandSlot(first)
    val secondSlot = strictAggregateOperandSlot(second)
    val thirdSlot = strictAggregateOperandSlot(third)

    return when {
        first is FusedOperand.I32Const && second is FusedOperand.I32Const && third is FusedOperand.I32Const -> iii(first.const, second.const, third.const)
        first is FusedOperand.I32Const && second is FusedOperand.I32Const && thirdSlot != null -> iis(first.const, second.const, thirdSlot)
        first is FusedOperand.I32Const && secondSlot != null && third is FusedOperand.I32Const -> isi(first.const, secondSlot, third.const)
        first is FusedOperand.I32Const && secondSlot != null && thirdSlot != null -> iss(first.const, secondSlot, thirdSlot)
        firstSlot != null && second is FusedOperand.I32Const && third is FusedOperand.I32Const -> sii(firstSlot, second.const, third.const)
        firstSlot != null && second is FusedOperand.I32Const && thirdSlot != null -> sis(firstSlot, second.const, thirdSlot)
        firstSlot != null && secondSlot != null && third is FusedOperand.I32Const -> ssi(firstSlot, secondSlot, third.const)
        firstSlot != null && secondSlot != null && thirdSlot != null -> sss(firstSlot, secondSlot, thirdSlot)
        else -> error("$instructionName operands must lower to i32 immediate/frame-slot shapes before predecode: first=$first second=$second third=$third")
    }
}

private fun strictAggregateOperandSlot(
    operand: FusedOperand,
): Int? = when (operand) {
    is FusedOperand.FrameSlot -> operand.offset
    is FusedOperand.LocalGet -> operand.index.idx
    else -> null
}

private fun strictAggregateDestinationSlot(
    destination: FusedDestination,
): Int? = when (destination) {
    is FusedDestination.FrameSlot -> destination.offset
    is FusedDestination.LocalSet -> destination.index.idx
    else -> null
}

private fun strictAggregateSlots(
    slots: List<Int>,
    expectedSize: Int,
    instructionName: String,
): List<Int> {
    require(slots.size == expectedSize) {
        "$instructionName operands must lower to $expectedSize frame slots before predecode: $slots"
    }
    return slots
}

private fun resolveArrayType(
    context: PredecodingContext,
    typeIndex: Index.TypeIndex,
): Result<ArrayType, ModuleTrapError> = binding {
    val definedType = context.runtimeTypes[typeIndex.idx].type
    definedType.asSubType.compositeType.arrayType() ?: Err(
        InvocationError.ArrayCompositeTypeExpected,
    ).bind()
}

private fun resolveStructType(
    context: PredecodingContext,
    typeIndex: Index.TypeIndex,
): Result<StructType, ModuleTrapError> = binding {
    val definedType = context.runtimeTypes[typeIndex.idx].type
    definedType.asSubType.compositeType.structType() ?: Err(
        InvocationError.StructCompositeTypeExpected,
    ).bind()
}

private fun unsupportedUnloweredAggregateInstruction(): DispatchableInstruction =
    error("aggregate fused instruction must be frame-slot lowered to immediate/frame-slot shapes before predecode")
