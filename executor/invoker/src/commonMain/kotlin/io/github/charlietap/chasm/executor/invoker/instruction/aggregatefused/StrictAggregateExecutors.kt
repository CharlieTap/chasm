package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.executor.invoker.ext.allocateArray
import io.github.charlietap.chasm.executor.invoker.ext.allocateStruct
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.FieldUnpacker
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.array
import io.github.charlietap.chasm.runtime.ext.field
import io.github.charlietap.chasm.runtime.ext.packedField
import io.github.charlietap.chasm.runtime.ext.struct
import io.github.charlietap.chasm.runtime.ext.toArrayAddress
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.ext.toStructAddress
import io.github.charlietap.chasm.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.runtime.instance.StructInstance
import io.github.charlietap.chasm.runtime.instruction.AggregateSuperInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.RTT
import io.github.charlietap.chasm.type.StructType

internal inline fun ArrayCopyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayCopyIii,
) = executeArrayCopy(
    store = store,
    elementsToCopy = instruction.elementsToCopy,
    sourceOffset = instruction.sourceOffset,
    sourceAddress = arrayAddress(vstack, instruction.sourceAddressSlot),
    destinationOffset = instruction.destinationOffset,
    destinationAddress = arrayAddress(vstack, instruction.destinationAddressSlot),
)

internal inline fun ArrayCopyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayCopyIis,
) = executeArrayCopy(
    store = store,
    elementsToCopy = instruction.elementsToCopy,
    sourceOffset = instruction.sourceOffset,
    sourceAddress = arrayAddress(vstack, instruction.sourceAddressSlot),
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    destinationAddress = arrayAddress(vstack, instruction.destinationAddressSlot),
)

internal inline fun ArrayCopyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayCopyIsi,
) = executeArrayCopy(
    store = store,
    elementsToCopy = instruction.elementsToCopy,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    sourceAddress = arrayAddress(vstack, instruction.sourceAddressSlot),
    destinationOffset = instruction.destinationOffset,
    destinationAddress = arrayAddress(vstack, instruction.destinationAddressSlot),
)

internal inline fun ArrayCopyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayCopyIss,
) = executeArrayCopy(
    store = store,
    elementsToCopy = instruction.elementsToCopy,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    sourceAddress = arrayAddress(vstack, instruction.sourceAddressSlot),
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    destinationAddress = arrayAddress(vstack, instruction.destinationAddressSlot),
)

internal inline fun ArrayCopyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayCopySii,
) = executeArrayCopy(
    store = store,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    sourceOffset = instruction.sourceOffset,
    sourceAddress = arrayAddress(vstack, instruction.sourceAddressSlot),
    destinationOffset = instruction.destinationOffset,
    destinationAddress = arrayAddress(vstack, instruction.destinationAddressSlot),
)

internal inline fun ArrayCopyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayCopySis,
) = executeArrayCopy(
    store = store,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    sourceOffset = instruction.sourceOffset,
    sourceAddress = arrayAddress(vstack, instruction.sourceAddressSlot),
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    destinationAddress = arrayAddress(vstack, instruction.destinationAddressSlot),
)

internal inline fun ArrayCopyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayCopySsi,
) = executeArrayCopy(
    store = store,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    sourceAddress = arrayAddress(vstack, instruction.sourceAddressSlot),
    destinationOffset = instruction.destinationOffset,
    destinationAddress = arrayAddress(vstack, instruction.destinationAddressSlot),
)

internal inline fun ArrayCopyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayCopySss,
) = executeArrayCopy(
    store = store,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    sourceAddress = arrayAddress(vstack, instruction.sourceAddressSlot),
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    destinationAddress = arrayAddress(vstack, instruction.destinationAddressSlot),
)

internal inline fun ArrayFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayFillIii,
) = executeArrayFill(
    store = store,
    elementsToFill = instruction.elementsToFill,
    fillValue = instruction.fillValue,
    arrayElementOffset = instruction.arrayElementOffset,
    address = arrayAddress(vstack, instruction.addressSlot),
)

internal inline fun ArrayFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayFillIis,
) = executeArrayFill(
    store = store,
    elementsToFill = instruction.elementsToFill,
    fillValue = instruction.fillValue,
    arrayElementOffset = vstack.getFrameSlot(instruction.arrayElementOffsetSlot).toInt(),
    address = arrayAddress(vstack, instruction.addressSlot),
)

internal inline fun ArrayFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayFillIsi,
) = executeArrayFill(
    store = store,
    elementsToFill = instruction.elementsToFill,
    fillValue = vstack.getFrameSlot(instruction.fillValueSlot),
    arrayElementOffset = instruction.arrayElementOffset,
    address = arrayAddress(vstack, instruction.addressSlot),
)

internal inline fun ArrayFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayFillIss,
) = executeArrayFill(
    store = store,
    elementsToFill = instruction.elementsToFill,
    fillValue = vstack.getFrameSlot(instruction.fillValueSlot),
    arrayElementOffset = vstack.getFrameSlot(instruction.arrayElementOffsetSlot).toInt(),
    address = arrayAddress(vstack, instruction.addressSlot),
)

internal inline fun ArrayFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayFillSii,
) = executeArrayFill(
    store = store,
    elementsToFill = vstack.getFrameSlot(instruction.elementsToFillSlot).toInt(),
    fillValue = instruction.fillValue,
    arrayElementOffset = instruction.arrayElementOffset,
    address = arrayAddress(vstack, instruction.addressSlot),
)

internal inline fun ArrayFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayFillSis,
) = executeArrayFill(
    store = store,
    elementsToFill = vstack.getFrameSlot(instruction.elementsToFillSlot).toInt(),
    fillValue = instruction.fillValue,
    arrayElementOffset = vstack.getFrameSlot(instruction.arrayElementOffsetSlot).toInt(),
    address = arrayAddress(vstack, instruction.addressSlot),
)

internal inline fun ArrayFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayFillSsi,
) = executeArrayFill(
    store = store,
    elementsToFill = vstack.getFrameSlot(instruction.elementsToFillSlot).toInt(),
    fillValue = vstack.getFrameSlot(instruction.fillValueSlot),
    arrayElementOffset = instruction.arrayElementOffset,
    address = arrayAddress(vstack, instruction.addressSlot),
)

internal inline fun ArrayFillExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayFillSss,
) = executeArrayFill(
    store = store,
    elementsToFill = vstack.getFrameSlot(instruction.elementsToFillSlot).toInt(),
    fillValue = vstack.getFrameSlot(instruction.fillValueSlot),
    arrayElementOffset = vstack.getFrameSlot(instruction.arrayElementOffsetSlot).toInt(),
    address = arrayAddress(vstack, instruction.addressSlot),
)

internal inline fun ArrayGetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayGetI,
) = executeArrayGet(
    vstack = vstack,
    store = store,
    fieldIndex = instruction.field,
    address = arrayAddress(vstack, instruction.addressSlot),
    destinationSlot = instruction.destinationSlot,
)

internal inline fun ArrayGetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayGetS,
) = executeArrayGet(
    vstack = vstack,
    store = store,
    fieldIndex = vstack.getFrameSlot(instruction.fieldSlot).toInt(),
    address = arrayAddress(vstack, instruction.addressSlot),
    destinationSlot = instruction.destinationSlot,
)

internal fun ArrayGetSignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayGetSignedI,
) = ArrayGetSignedExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun ArrayGetSignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayGetSignedI,
    crossinline fieldUnpacker: FieldUnpacker,
) = executePackedArrayGet(
    vstack = vstack,
    store = store,
    fieldIndex = instruction.field,
    address = arrayAddress(vstack, instruction.addressSlot),
    destinationSlot = instruction.destinationSlot,
    signed = true,
    fieldUnpacker = fieldUnpacker,
)

internal fun ArrayGetSignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayGetSignedS,
) = ArrayGetSignedExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun ArrayGetSignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayGetSignedS,
    crossinline fieldUnpacker: FieldUnpacker,
) = executePackedArrayGet(
    vstack = vstack,
    store = store,
    fieldIndex = vstack.getFrameSlot(instruction.fieldSlot).toInt(),
    address = arrayAddress(vstack, instruction.addressSlot),
    destinationSlot = instruction.destinationSlot,
    signed = true,
    fieldUnpacker = fieldUnpacker,
)

internal fun ArrayGetUnsignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayGetUnsignedI,
) = ArrayGetUnsignedExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun ArrayGetUnsignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayGetUnsignedI,
    crossinline fieldUnpacker: FieldUnpacker,
) = executePackedArrayGet(
    vstack = vstack,
    store = store,
    fieldIndex = instruction.field,
    address = arrayAddress(vstack, instruction.addressSlot),
    destinationSlot = instruction.destinationSlot,
    signed = false,
    fieldUnpacker = fieldUnpacker,
)

internal fun ArrayGetUnsignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayGetUnsignedS,
) = ArrayGetUnsignedExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun ArrayGetUnsignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayGetUnsignedS,
    crossinline fieldUnpacker: FieldUnpacker,
) = executePackedArrayGet(
    vstack = vstack,
    store = store,
    fieldIndex = vstack.getFrameSlot(instruction.fieldSlot).toInt(),
    address = arrayAddress(vstack, instruction.addressSlot),
    destinationSlot = instruction.destinationSlot,
    signed = false,
    fieldUnpacker = fieldUnpacker,
)

internal fun ArrayLenExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayLenS,
) = executeArrayLen(
    vstack = vstack,
    store = store,
    address = arrayAddress(vstack, instruction.addressSlot),
    destinationSlot = instruction.destinationSlot,
)

internal inline fun ArrayNewExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayNewIi,
) = executeArrayNew(
    vstack = vstack,
    store = store,
    size = instruction.size,
    value = instruction.value,
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
    arrayType = instruction.arrayType,
)

internal inline fun ArrayNewExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayNewIs,
) = executeArrayNew(
    vstack = vstack,
    store = store,
    size = instruction.size,
    value = vstack.getFrameSlot(instruction.valueSlot),
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
    arrayType = instruction.arrayType,
)

internal inline fun ArrayNewExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayNewSi,
) = executeArrayNew(
    vstack = vstack,
    store = store,
    size = vstack.getFrameSlot(instruction.sizeSlot).toInt(),
    value = instruction.value,
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
    arrayType = instruction.arrayType,
)

internal inline fun ArrayNewExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayNewSs,
) = executeArrayNew(
    vstack = vstack,
    store = store,
    size = vstack.getFrameSlot(instruction.sizeSlot).toInt(),
    value = vstack.getFrameSlot(instruction.valueSlot),
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
    arrayType = instruction.arrayType,
)

internal inline fun ArrayNewFixedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayNewFixedS,
) = executeArrayNewFixed(
    vstack = vstack,
    store = store,
    valueSlots = instruction.valueSlots,
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
    arrayType = instruction.arrayType,
    size = instruction.size,
)

internal inline fun ArraySetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArraySetIi,
) = executeArraySet(
    store = store,
    value = instruction.value,
    fieldIndex = instruction.field,
    address = arrayAddress(vstack, instruction.addressSlot),
)

internal inline fun ArraySetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArraySetIs,
) = executeArraySet(
    store = store,
    value = instruction.value,
    fieldIndex = vstack.getFrameSlot(instruction.fieldSlot).toInt(),
    address = arrayAddress(vstack, instruction.addressSlot),
)

internal inline fun ArraySetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArraySetSi,
) = executeArraySet(
    store = store,
    value = vstack.getFrameSlot(instruction.valueSlot),
    fieldIndex = instruction.field,
    address = arrayAddress(vstack, instruction.addressSlot),
)

internal inline fun ArraySetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArraySetSs,
) = executeArraySet(
    store = store,
    value = vstack.getFrameSlot(instruction.valueSlot),
    fieldIndex = vstack.getFrameSlot(instruction.fieldSlot).toInt(),
    address = arrayAddress(vstack, instruction.addressSlot),
)

internal inline fun StructGetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.StructGetS,
) = executeStructGet(
    vstack = vstack,
    store = store,
    address = structAddress(vstack, instruction.addressSlot),
    destinationSlot = instruction.destinationSlot,
    fieldIndex = instruction.fieldIndex,
)

internal fun StructGetSignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.StructGetSignedS,
) = StructGetSignedExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun StructGetSignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.StructGetSignedS,
    crossinline fieldUnpacker: FieldUnpacker,
) = executePackedStructGet(
    vstack = vstack,
    store = store,
    address = structAddress(vstack, instruction.addressSlot),
    destinationSlot = instruction.destinationSlot,
    fieldIndex = instruction.fieldIndex,
    signed = true,
    fieldUnpacker = fieldUnpacker,
)

internal fun StructGetUnsignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.StructGetUnsignedS,
) = StructGetUnsignedExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun StructGetUnsignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.StructGetUnsignedS,
    crossinline fieldUnpacker: FieldUnpacker,
) = executePackedStructGet(
    vstack = vstack,
    store = store,
    address = structAddress(vstack, instruction.addressSlot),
    destinationSlot = instruction.destinationSlot,
    fieldIndex = instruction.fieldIndex,
    signed = false,
    fieldUnpacker = fieldUnpacker,
)

internal inline fun StructNewExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.StructNewS,
) = executeStructNew(
    vstack = vstack,
    store = store,
    fieldSlots = instruction.fieldSlots,
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
    structType = instruction.structType,
)

internal inline fun StructNewDefaultExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.StructNewDefaultS,
) = executeStructNewDefault(
    vstack = vstack,
    store = store,
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
    structType = instruction.structType,
    fields = instruction.fields,
)

internal inline fun StructSetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.StructSetI,
) = executeStructSet(
    store = store,
    value = instruction.value,
    address = structAddress(vstack, instruction.addressSlot),
    fieldIndex = instruction.fieldIndex,
)

internal inline fun StructSetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.StructSetS,
) = executeStructSet(
    store = store,
    value = vstack.getFrameSlot(instruction.valueSlot),
    address = structAddress(vstack, instruction.addressSlot),
    fieldIndex = instruction.fieldIndex,
)

private fun executeArrayCopy(
    store: Store,
    elementsToCopy: Int,
    sourceOffset: Int,
    sourceAddress: io.github.charlietap.chasm.runtime.address.Address.Array,
    destinationOffset: Int,
    destinationAddress: io.github.charlietap.chasm.runtime.address.Address.Array,
) {
    val source = store.array(sourceAddress)
    val destination = store.array(destinationAddress)

    try {
        source.fields.copyInto(destination.fields, destinationOffset, sourceOffset, sourceOffset + elementsToCopy)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}

private fun executeArrayFill(
    store: Store,
    elementsToFill: Int,
    fillValue: Long,
    arrayElementOffset: Int,
    address: io.github.charlietap.chasm.runtime.address.Address.Array,
) {
    val arrayInstance = store.array(address)
    try {
        arrayInstance.fields.fill(fillValue, arrayElementOffset, arrayElementOffset + elementsToFill)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}

private fun executeArrayGet(
    vstack: ValueStack,
    store: Store,
    fieldIndex: Int,
    address: io.github.charlietap.chasm.runtime.address.Address.Array,
    destinationSlot: Int,
) {
    val arrayInstance = store.array(address)
    val fieldValue = arrayInstance.field(fieldIndex)
    vstack.setFrameSlot(destinationSlot, fieldValue)
}

private inline fun executePackedArrayGet(
    vstack: ValueStack,
    store: Store,
    fieldIndex: Int,
    address: io.github.charlietap.chasm.runtime.address.Address.Array,
    destinationSlot: Int,
    signed: Boolean,
    crossinline fieldUnpacker: FieldUnpacker,
) {
    val arrayInstance = store.array(address)
    val (packed, type) = arrayInstance.packedField(fieldIndex)
    vstack.setFrameSlot(destinationSlot, fieldUnpacker(packed, type, signed))
}

private fun executeArrayLen(
    vstack: ValueStack,
    store: Store,
    address: io.github.charlietap.chasm.runtime.address.Address.Array,
    destinationSlot: Int,
) {
    val arrayInstance = store.array(address)
    vstack.setFrameSlot(destinationSlot, arrayInstance.fields.size.toLong())
}

private fun executeArrayNew(
    vstack: ValueStack,
    store: Store,
    size: Int,
    value: Long,
    destinationSlot: Int,
    rtt: RTT,
    arrayType: ArrayType,
) {
    val fields = LongArray(size) { value }
    val instance = ArrayInstance(rtt, arrayType, fields)
    val address = store.allocateArray(instance)
    vstack.setFrameSlot(destinationSlot, ReferenceValue.Array(address).toLong())
}

private fun executeArrayNewFixed(
    vstack: ValueStack,
    store: Store,
    valueSlots: List<Int>,
    destinationSlot: Int,
    rtt: RTT,
    arrayType: ArrayType,
    size: Int,
) {
    val fields = LongArray(size)
    var index = 0
    while (index < size) {
        fields[index] = vstack.getFrameSlot(valueSlots[index])
        index++
    }

    val instance = ArrayInstance(rtt, arrayType, fields)
    val address = store.allocateArray(instance)
    vstack.setFrameSlot(destinationSlot, ReferenceValue.Array(address).toLong())
}

private fun executeArraySet(
    store: Store,
    value: Long,
    fieldIndex: Int,
    address: io.github.charlietap.chasm.runtime.address.Address.Array,
) {
    val arrayInstance = store.array(address)
    try {
        arrayInstance.fields[fieldIndex] = value
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}

private fun executeStructGet(
    vstack: ValueStack,
    store: Store,
    address: io.github.charlietap.chasm.runtime.address.Address.Struct,
    destinationSlot: Int,
    fieldIndex: Int,
) {
    val structInstance = store.struct(address)
    val fieldValue = structInstance.field(Index.FieldIndex(fieldIndex))
    vstack.setFrameSlot(destinationSlot, fieldValue)
}

private inline fun executePackedStructGet(
    vstack: ValueStack,
    store: Store,
    address: io.github.charlietap.chasm.runtime.address.Address.Struct,
    destinationSlot: Int,
    fieldIndex: Int,
    signed: Boolean,
    crossinline fieldUnpacker: FieldUnpacker,
) {
    val structInstance = store.struct(address)
    val (packed, type) = structInstance.packedField(Index.FieldIndex(fieldIndex))
    vstack.setFrameSlot(destinationSlot, fieldUnpacker(packed, type, signed))
}

private fun executeStructNew(
    vstack: ValueStack,
    store: Store,
    fieldSlots: List<Int>,
    destinationSlot: Int,
    rtt: RTT,
    structType: StructType,
) {
    val size = structType.fields.size
    val fields = LongArray(size)
    var index = 0
    while (index < size) {
        fields[index] = vstack.getFrameSlot(fieldSlots[index])
        index++
    }

    val instance = StructInstance(rtt, structType, fields)
    val address = store.allocateStruct(instance)
    vstack.setFrameSlot(destinationSlot, ReferenceValue.Struct(address).toLong())
}

private fun executeStructNewDefault(
    vstack: ValueStack,
    store: Store,
    destinationSlot: Int,
    rtt: RTT,
    structType: StructType,
    fields: LongArray,
) {
    val instance = StructInstance(rtt, structType, fields)
    val address = store.allocateStruct(instance)
    vstack.setFrameSlot(destinationSlot, ReferenceValue.Struct(address).toLong())
}

private fun executeStructSet(
    store: Store,
    value: Long,
    address: io.github.charlietap.chasm.runtime.address.Address.Struct,
    fieldIndex: Int,
) {
    val structInstance = store.struct(address)
    structInstance.fields[fieldIndex] = value
}

private inline fun arrayAddress(
    vstack: ValueStack,
    addressSlot: Int,
) = vstack.getFrameSlot(addressSlot).toArrayAddress()

private inline fun structAddress(
    vstack: ValueStack,
    addressSlot: Int,
) = vstack.getFrameSlot(addressSlot).toStructAddress()
