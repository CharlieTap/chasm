package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.executor.invoker.ext.allocateArray
import io.github.charlietap.chasm.executor.invoker.ext.valueFromBytes
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.array
import io.github.charlietap.chasm.runtime.ext.extendSigned
import io.github.charlietap.chasm.runtime.ext.extendUnsigned
import io.github.charlietap.chasm.runtime.ext.isExternReference
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.ext.toArrayAddress
import io.github.charlietap.chasm.runtime.ext.toExternReference
import io.github.charlietap.chasm.runtime.ext.toI31
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.ext.toReferenceValue
import io.github.charlietap.chasm.runtime.ext.wrapI31
import io.github.charlietap.chasm.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.runtime.instruction.AggregateSuperInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.RTT

internal inline fun ArrayNewDefaultExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayNewDefaultI,
) = executeArrayNewDefault(
    vstack = vstack,
    store = store,
    size = instruction.size,
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
    arrayType = instruction.arrayType,
    field = instruction.field,
)

internal inline fun ArrayNewDefaultExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayNewDefaultS,
) = executeArrayNewDefault(
    vstack = vstack,
    store = store,
    size = vstack.getFrameSlot(instruction.sizeSlot).toInt(),
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
    arrayType = instruction.arrayType,
    field = instruction.field,
)

internal inline fun ArrayNewDataExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayNewDataIi,
) = executeArrayNewData(
    vstack = vstack,
    store = store,
    sourceOffset = instruction.sourceOffset,
    arrayLength = instruction.arrayLength,
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
    arrayType = instruction.arrayType,
    dataInstance = instruction.dataInstance,
    fieldWidthInBytes = instruction.fieldWidthInBytes,
)

internal inline fun ArrayNewDataExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayNewDataIs,
) = executeArrayNewData(
    vstack = vstack,
    store = store,
    sourceOffset = instruction.sourceOffset,
    arrayLength = vstack.getFrameSlot(instruction.arrayLengthSlot).toInt(),
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
    arrayType = instruction.arrayType,
    dataInstance = instruction.dataInstance,
    fieldWidthInBytes = instruction.fieldWidthInBytes,
)

internal inline fun ArrayNewDataExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayNewDataSi,
) = executeArrayNewData(
    vstack = vstack,
    store = store,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    arrayLength = instruction.arrayLength,
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
    arrayType = instruction.arrayType,
    dataInstance = instruction.dataInstance,
    fieldWidthInBytes = instruction.fieldWidthInBytes,
)

internal inline fun ArrayNewDataExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayNewDataSs,
) = executeArrayNewData(
    vstack = vstack,
    store = store,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    arrayLength = vstack.getFrameSlot(instruction.arrayLengthSlot).toInt(),
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
    arrayType = instruction.arrayType,
    dataInstance = instruction.dataInstance,
    fieldWidthInBytes = instruction.fieldWidthInBytes,
)

internal inline fun ArrayNewElementExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayNewElementIi,
) = executeArrayNewElement(
    vstack = vstack,
    store = store,
    sourceOffset = instruction.sourceOffset,
    arrayLength = instruction.arrayLength,
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
    arrayType = instruction.arrayType,
    elementInstance = instruction.elementInstance,
)

internal inline fun ArrayNewElementExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayNewElementIs,
) = executeArrayNewElement(
    vstack = vstack,
    store = store,
    sourceOffset = instruction.sourceOffset,
    arrayLength = vstack.getFrameSlot(instruction.arrayLengthSlot).toInt(),
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
    arrayType = instruction.arrayType,
    elementInstance = instruction.elementInstance,
)

internal inline fun ArrayNewElementExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayNewElementSi,
) = executeArrayNewElement(
    vstack = vstack,
    store = store,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    arrayLength = instruction.arrayLength,
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
    arrayType = instruction.arrayType,
    elementInstance = instruction.elementInstance,
)

internal inline fun ArrayNewElementExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayNewElementSs,
) = executeArrayNewElement(
    vstack = vstack,
    store = store,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    arrayLength = vstack.getFrameSlot(instruction.arrayLengthSlot).toInt(),
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
    arrayType = instruction.arrayType,
    elementInstance = instruction.elementInstance,
)

internal inline fun ArrayInitDataExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitDataIii,
) = executeArrayInitData(
    store = store,
    elementsToCopy = instruction.elementsToCopy,
    sourceOffset = instruction.sourceOffset,
    destinationOffset = instruction.destinationOffset,
    address = vstack.getFrameSlot(instruction.addressSlot).toArrayAddress(),
    dataInstance = instruction.dataInstance,
    fieldWidthInBytes = instruction.fieldWidthInBytes,
)

internal inline fun ArrayInitDataExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitDataIis,
) = executeArrayInitData(
    store = store,
    elementsToCopy = instruction.elementsToCopy,
    sourceOffset = instruction.sourceOffset,
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    address = vstack.getFrameSlot(instruction.addressSlot).toArrayAddress(),
    dataInstance = instruction.dataInstance,
    fieldWidthInBytes = instruction.fieldWidthInBytes,
)

internal inline fun ArrayInitDataExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitDataIsi,
) = executeArrayInitData(
    store = store,
    elementsToCopy = instruction.elementsToCopy,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = instruction.destinationOffset,
    address = vstack.getFrameSlot(instruction.addressSlot).toArrayAddress(),
    dataInstance = instruction.dataInstance,
    fieldWidthInBytes = instruction.fieldWidthInBytes,
)

internal inline fun ArrayInitDataExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitDataIss,
) = executeArrayInitData(
    store = store,
    elementsToCopy = instruction.elementsToCopy,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    address = vstack.getFrameSlot(instruction.addressSlot).toArrayAddress(),
    dataInstance = instruction.dataInstance,
    fieldWidthInBytes = instruction.fieldWidthInBytes,
)

internal inline fun ArrayInitDataExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitDataSii,
) = executeArrayInitData(
    store = store,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    sourceOffset = instruction.sourceOffset,
    destinationOffset = instruction.destinationOffset,
    address = vstack.getFrameSlot(instruction.addressSlot).toArrayAddress(),
    dataInstance = instruction.dataInstance,
    fieldWidthInBytes = instruction.fieldWidthInBytes,
)

internal inline fun ArrayInitDataExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitDataSis,
) = executeArrayInitData(
    store = store,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    sourceOffset = instruction.sourceOffset,
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    address = vstack.getFrameSlot(instruction.addressSlot).toArrayAddress(),
    dataInstance = instruction.dataInstance,
    fieldWidthInBytes = instruction.fieldWidthInBytes,
)

internal inline fun ArrayInitDataExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitDataSsi,
) = executeArrayInitData(
    store = store,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = instruction.destinationOffset,
    address = vstack.getFrameSlot(instruction.addressSlot).toArrayAddress(),
    dataInstance = instruction.dataInstance,
    fieldWidthInBytes = instruction.fieldWidthInBytes,
)

internal inline fun ArrayInitDataExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitDataSss,
) = executeArrayInitData(
    store = store,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    address = vstack.getFrameSlot(instruction.addressSlot).toArrayAddress(),
    dataInstance = instruction.dataInstance,
    fieldWidthInBytes = instruction.fieldWidthInBytes,
)

internal inline fun ArrayInitElementExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitElementIii,
) = executeArrayInitElement(
    store = store,
    elementsToCopy = instruction.elementsToCopy,
    sourceOffset = instruction.sourceOffset,
    destinationOffset = instruction.destinationOffset,
    address = vstack.getFrameSlot(instruction.addressSlot).toArrayAddress(),
    elementInstance = instruction.elementInstance,
)

internal inline fun ArrayInitElementExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitElementIis,
) = executeArrayInitElement(
    store = store,
    elementsToCopy = instruction.elementsToCopy,
    sourceOffset = instruction.sourceOffset,
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    address = vstack.getFrameSlot(instruction.addressSlot).toArrayAddress(),
    elementInstance = instruction.elementInstance,
)

internal inline fun ArrayInitElementExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitElementIsi,
) = executeArrayInitElement(
    store = store,
    elementsToCopy = instruction.elementsToCopy,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = instruction.destinationOffset,
    address = vstack.getFrameSlot(instruction.addressSlot).toArrayAddress(),
    elementInstance = instruction.elementInstance,
)

internal inline fun ArrayInitElementExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitElementIss,
) = executeArrayInitElement(
    store = store,
    elementsToCopy = instruction.elementsToCopy,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    address = vstack.getFrameSlot(instruction.addressSlot).toArrayAddress(),
    elementInstance = instruction.elementInstance,
)

internal inline fun ArrayInitElementExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitElementSii,
) = executeArrayInitElement(
    store = store,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    sourceOffset = instruction.sourceOffset,
    destinationOffset = instruction.destinationOffset,
    address = vstack.getFrameSlot(instruction.addressSlot).toArrayAddress(),
    elementInstance = instruction.elementInstance,
)

internal inline fun ArrayInitElementExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitElementSis,
) = executeArrayInitElement(
    store = store,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    sourceOffset = instruction.sourceOffset,
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    address = vstack.getFrameSlot(instruction.addressSlot).toArrayAddress(),
    elementInstance = instruction.elementInstance,
)

internal inline fun ArrayInitElementExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitElementSsi,
) = executeArrayInitElement(
    store = store,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = instruction.destinationOffset,
    address = vstack.getFrameSlot(instruction.addressSlot).toArrayAddress(),
    elementInstance = instruction.elementInstance,
)

internal inline fun ArrayInitElementExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitElementSss,
) = executeArrayInitElement(
    store = store,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    address = vstack.getFrameSlot(instruction.addressSlot).toArrayAddress(),
    elementInstance = instruction.elementInstance,
)

internal inline fun RefI31Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.RefI31I,
) = executeRefI31(
    vstack = vstack,
    value = instruction.value,
    destinationSlot = instruction.destinationSlot,
)

internal inline fun RefI31Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.RefI31S,
) = executeRefI31(
    vstack = vstack,
    value = vstack.getFrameSlot(instruction.valueSlot).toInt(),
    destinationSlot = instruction.destinationSlot,
)

internal inline fun I31GetSignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.I31GetSignedS,
) = executeI31Get(
    vstack = vstack,
    value = vstack.getFrameSlot(instruction.valueSlot).toI31(),
    destinationSlot = instruction.destinationSlot,
    extender = UInt::extendSigned,
)

internal inline fun I31GetUnsignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.I31GetUnsignedS,
) = executeI31Get(
    vstack = vstack,
    value = vstack.getFrameSlot(instruction.valueSlot).toI31(),
    destinationSlot = instruction.destinationSlot,
    extender = UInt::extendUnsigned,
)

internal inline fun AnyConvertExternExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.AnyConvertExternS,
) = executeAnyConvertExtern(
    vstack = vstack,
    referenceValue = vstack.getFrameSlot(instruction.valueSlot),
    destinationSlot = instruction.destinationSlot,
)

internal inline fun ExternConvertAnyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ExternConvertAnyS,
) = executeExternConvertAny(
    vstack = vstack,
    referenceValue = vstack.getFrameSlot(instruction.valueSlot),
    destinationSlot = instruction.destinationSlot,
)

private fun executeArrayNewDefault(
    vstack: ValueStack,
    store: Store,
    size: Int,
    destinationSlot: Int,
    rtt: RTT,
    arrayType: ArrayType,
    field: Long,
) {
    val fields = LongArray(size) { field }
    val instance = ArrayInstance(rtt, arrayType, fields)
    val address = store.allocateArray(instance)
    vstack.setFrameSlot(destinationSlot, ReferenceValue.Array(address).toLong())
}

private fun executeArrayNewData(
    vstack: ValueStack,
    store: Store,
    sourceOffset: Int,
    arrayLength: Int,
    destinationSlot: Int,
    rtt: RTT,
    arrayType: ArrayType,
    dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
    fieldWidthInBytes: Int,
) {
    val byteArray = dataInstance.bytes
    val arrayEndOffsetInSegment = sourceOffset + (arrayLength * fieldWidthInBytes)
    if (arrayLength < 0 || arrayEndOffsetInSegment > byteArray.size) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }

    val fields = LongArray(arrayLength) { offset ->
        val elementOffset = sourceOffset + (offset * fieldWidthInBytes)
        arrayType.fieldType.valueFromBytes(byteArray, elementOffset)
    }

    val instance = ArrayInstance(rtt, arrayType, fields)
    val address = store.allocateArray(instance)
    vstack.setFrameSlot(destinationSlot, ReferenceValue.Array(address).toLong())
}

private fun executeArrayNewElement(
    vstack: ValueStack,
    store: Store,
    sourceOffset: Int,
    arrayLength: Int,
    destinationSlot: Int,
    rtt: RTT,
    arrayType: ArrayType,
    elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
) {
    val arrayEndOffsetInSegment = sourceOffset + arrayLength
    if (arrayLength < 0 || arrayEndOffsetInSegment > elementInstance.elements.size) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }

    val fields = LongArray(arrayLength)
    elementInstance.elements.copyInto(fields, 0, sourceOffset, arrayEndOffsetInSegment)

    val instance = ArrayInstance(rtt, arrayType, fields)
    val address = store.allocateArray(instance)
    vstack.setFrameSlot(destinationSlot, ReferenceValue.Array(address).toLong())
}

private fun executeArrayInitData(
    store: Store,
    elementsToCopy: Int,
    sourceOffset: Int,
    destinationOffset: Int,
    address: io.github.charlietap.chasm.runtime.address.Address.Array,
    dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
    fieldWidthInBytes: Int,
) {
    val arrayInstance = store.array(address)
    val arrayType = arrayInstance.arrayType

    if (elementsToCopy == 0) {
        if (
            (destinationOffset + elementsToCopy > arrayInstance.fields.size) ||
            (sourceOffset + (elementsToCopy * fieldWidthInBytes) > dataInstance.bytes.size)
        ) {
            throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
        }
        return
    }

    val byteArray = dataInstance.bytes
    try {
        val elements = LongArray(elementsToCopy) { offset ->
            val srcOffsetInByteArray = sourceOffset + (fieldWidthInBytes * offset)
            arrayType.fieldType.valueFromBytes(byteArray, srcOffsetInByteArray)
        }
        elements.copyInto(arrayInstance.fields, destinationOffset)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}

private fun executeArrayInitElement(
    store: Store,
    elementsToCopy: Int,
    sourceOffset: Int,
    destinationOffset: Int,
    address: io.github.charlietap.chasm.runtime.address.Address.Array,
    elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
) {
    val arrayInstance = store.array(address)

    try {
        elementInstance.elements.copyInto(arrayInstance.fields, destinationOffset, sourceOffset, sourceOffset + elementsToCopy)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    }
}

private inline fun executeRefI31(
    vstack: ValueStack,
    value: Int,
    destinationSlot: Int,
) {
    vstack.setFrameSlot(destinationSlot, value.wrapI31().toLong())
}

private inline fun executeI31Get(
    vstack: ValueStack,
    value: UInt,
    destinationSlot: Int,
    crossinline extender: (UInt) -> Int,
) {
    vstack.setFrameSlot(destinationSlot, extender(value).toLong())
}

private inline fun executeAnyConvertExtern(
    vstack: ValueStack,
    referenceValue: Long,
    destinationSlot: Int,
) {
    when {
        referenceValue.isNullableReference() -> {
            vstack.setFrameSlot(destinationSlot, ReferenceValue.Null(AbstractHeapType.Any).toLong())
        }
        referenceValue.isExternReference() -> {
            vstack.setFrameSlot(destinationSlot, referenceValue.toExternReference().referenceValue.toLongFromBoxed())
        }
        else -> throw InvocationException(InvocationError.UnexpectedReferenceValue)
    }
}

private inline fun executeExternConvertAny(
    vstack: ValueStack,
    referenceValue: Long,
    destinationSlot: Int,
) {
    when {
        referenceValue.isNullableReference() -> {
            vstack.setFrameSlot(destinationSlot, ReferenceValue.Null(AbstractHeapType.Extern).toLong())
        }
        else -> {
            vstack.setFrameSlot(destinationSlot, ReferenceValue.Extern(referenceValue.toReferenceValue()).toLongFromBoxed())
        }
    }
}
