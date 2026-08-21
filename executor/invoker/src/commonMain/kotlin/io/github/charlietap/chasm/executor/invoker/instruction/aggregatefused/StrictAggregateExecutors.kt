package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.FieldUnpacker
import io.github.charlietap.chasm.executor.invoker.type.Caster
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.instruction.AggregateSuperInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.type.PackedType

internal inline fun ArrayCopyExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayCopyIii,
) = executeArrayCopy(
    heap = context.heap,
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
    heap = context.heap,
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
    heap = context.heap,
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
    heap = context.heap,
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
    heap = context.heap,
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
    heap = context.heap,
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
    heap = context.heap,
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
    heap = context.heap,
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
    heap = context.heap,
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
    heap = context.heap,
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
    heap = context.heap,
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
    heap = context.heap,
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
    heap = context.heap,
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
    heap = context.heap,
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
    heap = context.heap,
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
    heap = context.heap,
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
    heap = context.heap,
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
    heap = context.heap,
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
    heap = context.heap,
    context = context,
    instruction = instruction,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun ArrayGetSignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    heap: WasmHeap,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayGetSignedI,
    crossinline fieldUnpacker: FieldUnpacker,
) = executePackedArrayGet(
    vstack = vstack,
    heap = heap,
    fieldIndex = instruction.field,
    address = arrayAddress(vstack, instruction.addressSlot),
    destinationSlot = instruction.destinationSlot,
    signed = true,
    packedType = instruction.packedType,
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
    heap = context.heap,
    context = context,
    instruction = instruction,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun ArrayGetSignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    heap: WasmHeap,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayGetSignedS,
    crossinline fieldUnpacker: FieldUnpacker,
) = executePackedArrayGet(
    vstack = vstack,
    heap = heap,
    fieldIndex = vstack.getFrameSlot(instruction.fieldSlot).toInt(),
    address = arrayAddress(vstack, instruction.addressSlot),
    destinationSlot = instruction.destinationSlot,
    signed = true,
    packedType = instruction.packedType,
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
    heap = context.heap,
    context = context,
    instruction = instruction,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun ArrayGetUnsignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    heap: WasmHeap,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayGetUnsignedI,
    crossinline fieldUnpacker: FieldUnpacker,
) = executePackedArrayGet(
    vstack = vstack,
    heap = heap,
    fieldIndex = instruction.field,
    address = arrayAddress(vstack, instruction.addressSlot),
    destinationSlot = instruction.destinationSlot,
    signed = false,
    packedType = instruction.packedType,
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
    heap = context.heap,
    context = context,
    instruction = instruction,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun ArrayGetUnsignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    heap: WasmHeap,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayGetUnsignedS,
    crossinline fieldUnpacker: FieldUnpacker,
) = executePackedArrayGet(
    vstack = vstack,
    heap = heap,
    fieldIndex = vstack.getFrameSlot(instruction.fieldSlot).toInt(),
    address = arrayAddress(vstack, instruction.addressSlot),
    destinationSlot = instruction.destinationSlot,
    signed = false,
    packedType = instruction.packedType,
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
    heap = context.heap,
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
    heap = context.heap,
    context = context,
    size = instruction.size,
    value = instruction.value,
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
)

internal inline fun ArrayNewExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayNewIs,
) = executeArrayNew(
    vstack = vstack,
    heap = context.heap,
    context = context,
    size = instruction.size,
    value = vstack.getFrameSlot(instruction.valueSlot),
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
)

internal inline fun ArrayNewExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayNewSi,
) = executeArrayNew(
    vstack = vstack,
    heap = context.heap,
    context = context,
    size = vstack.getFrameSlot(instruction.sizeSlot).toInt(),
    value = instruction.value,
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
)

internal inline fun ArrayNewExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayNewSs,
) = executeArrayNew(
    vstack = vstack,
    heap = context.heap,
    context = context,
    size = vstack.getFrameSlot(instruction.sizeSlot).toInt(),
    value = vstack.getFrameSlot(instruction.valueSlot),
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
)

internal inline fun ArrayNewFixedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayNewFixedS,
) = executeArrayNewFixed(
    vstack = vstack,
    heap = context.heap,
    context = context,
    firstElementSlot = instruction.firstElementSlot,
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
    size = instruction.size,
)

internal inline fun ArraySetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArraySetIi,
) = executeArraySet(
    heap = context.heap,
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
    heap = context.heap,
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
    heap = context.heap,
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
    heap = context.heap,
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
    heap = context.heap,
    reference = structReference(vstack, instruction.addressSlot),
    destinationSlot = instruction.destinationSlot,
    fieldIndex = instruction.fieldIndex,
)

internal inline fun RefCastStructGetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.RefCastStructGetS,
) {
    val reference = vstack.getFrameSlot(instruction.referenceSlot)
    if (!Caster(reference, instruction.typeTest, store)) {
        throw InvocationException(InvocationError.FailedToCastReference)
    }
    vstack.setFrameSlot(
        instruction.destinationSlot,
        context.heap.getStructFieldTrusted(reference, instruction.fieldIndex),
    )
}

internal inline fun StructGetStructGetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.StructGetStructGetS,
) {
    val reference = structField(
        heap = context.heap,
        reference = vstack.getFrameSlot(instruction.addressSlot),
        fieldIndex = instruction.firstFieldIndex,
    )
    vstack.setFrameSlot(
        instruction.destinationSlot,
        structField(context.heap, reference, instruction.secondFieldIndex),
    )
}

internal inline fun LocalSetStructGetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.LocalSetStructGetS,
) {
    val reference = vstack.getFrameSlot(instruction.sourceSlot)
    vstack.setFrameSlot(instruction.localSlot, reference)
    vstack.setFrameSlot(
        instruction.destinationSlot,
        structField(context.heap, reference, instruction.fieldIndex),
    )
}

internal fun StructGetSignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.StructGetSignedS,
) = StructGetSignedExecutor(
    vstack = vstack,
    cstack = cstack,
    heap = context.heap,
    context = context,
    instruction = instruction,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun StructGetSignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    heap: WasmHeap,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.StructGetSignedS,
    crossinline fieldUnpacker: FieldUnpacker,
) = executePackedStructGet(
    vstack = vstack,
    heap = heap,
    reference = structReference(vstack, instruction.addressSlot),
    destinationSlot = instruction.destinationSlot,
    fieldIndex = instruction.fieldIndex,
    packedType = instruction.packedType,
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
    heap = context.heap,
    context = context,
    instruction = instruction,
    fieldUnpacker = ::FieldUnpacker,
)

internal inline fun StructGetUnsignedExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    heap: WasmHeap,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.StructGetUnsignedS,
    crossinline fieldUnpacker: FieldUnpacker,
) = executePackedStructGet(
    vstack = vstack,
    heap = heap,
    reference = structReference(vstack, instruction.addressSlot),
    destinationSlot = instruction.destinationSlot,
    fieldIndex = instruction.fieldIndex,
    packedType = instruction.packedType,
    signed = false,
    fieldUnpacker = fieldUnpacker,
)

internal inline fun StructNewExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.StructNewS,
) {
    context.heap.allocateStructFromFrame(
        context = context,
        runtimeType = instruction.rtt,
        firstFieldSlot = instruction.firstFieldSlot,
        destinationSlot = instruction.destinationSlot,
    )
}

internal inline fun StructNewDefaultExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.StructNewDefaultS,
) {
    vstack.setFrameSlot(
        instruction.destinationSlot,
        context.heap.allocateStruct(context, instruction.rtt, instruction.fields),
    )
}

internal inline fun StructSetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.StructSetI,
) = executeStructSet(
    heap = context.heap,
    value = instruction.value,
    reference = structReference(vstack, instruction.addressSlot),
    fieldIndex = instruction.fieldIndex,
)

internal inline fun StructSetExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.StructSetS,
) = executeStructSet(
    heap = context.heap,
    value = vstack.getFrameSlot(instruction.valueSlot),
    reference = structReference(vstack, instruction.addressSlot),
    fieldIndex = instruction.fieldIndex,
)

private fun executeArrayCopy(
    heap: WasmHeap,
    elementsToCopy: Int,
    sourceOffset: Int,
    sourceAddress: Long,
    destinationOffset: Int,
    destinationAddress: Long,
) {
    try {
        heap.copyArray(sourceAddress, sourceOffset, destinationAddress, destinationOffset, elementsToCopy)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}

private fun executeArrayFill(
    heap: WasmHeap,
    elementsToFill: Int,
    fillValue: Long,
    arrayElementOffset: Int,
    address: Long,
) {
    try {
        heap.fillArray(address, arrayElementOffset, elementsToFill, fillValue)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}

private fun executeArrayGet(
    vstack: ValueStack,
    heap: WasmHeap,
    fieldIndex: Int,
    address: Long,
    destinationSlot: Int,
) {
    vstack.setFrameSlot(destinationSlot, heap.getArrayElementTrusted(address, fieldIndex))
}

private inline fun executePackedArrayGet(
    vstack: ValueStack,
    heap: WasmHeap,
    fieldIndex: Int,
    address: Long,
    destinationSlot: Int,
    signed: Boolean,
    packedType: PackedType,
    crossinline fieldUnpacker: FieldUnpacker,
) {
    val packed = heap.getArrayElementTrusted(address, fieldIndex)
    vstack.setFrameSlot(destinationSlot, fieldUnpacker(packed, packedType, signed))
}

private fun executeArrayLen(
    vstack: ValueStack,
    heap: WasmHeap,
    address: Long,
    destinationSlot: Int,
) {
    vstack.setFrameSlot(destinationSlot, heap.arrayLengthTrusted(address).toLong())
}

private fun executeArrayNew(
    vstack: ValueStack,
    heap: WasmHeap,
    context: ExecutionContext,
    size: Int,
    value: Long,
    destinationSlot: Int,
    rtt: RTT,
) {
    try {
        vstack.setFrameSlot(destinationSlot, heap.allocateArrayFilled(context, rtt, size, value))
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}

private fun executeArrayNewFixed(
    vstack: ValueStack,
    heap: WasmHeap,
    context: ExecutionContext,
    firstElementSlot: Int,
    destinationSlot: Int,
    rtt: RTT,
    size: Int,
) {
    heap.allocateArrayFromFrame(context, rtt, firstElementSlot, size, destinationSlot)
}

private fun executeArraySet(
    heap: WasmHeap,
    value: Long,
    fieldIndex: Int,
    address: Long,
) {
    try {
        heap.setArrayElementTrusted(address, fieldIndex, value)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}

private fun executeStructGet(
    vstack: ValueStack,
    heap: WasmHeap,
    reference: Long,
    destinationSlot: Int,
    fieldIndex: Int,
) {
    vstack.setFrameSlot(destinationSlot, heap.getStructFieldTrusted(reference, fieldIndex))
}

private fun structField(
    heap: WasmHeap,
    reference: Long,
    fieldIndex: Int,
): Long = heap.getStructFieldTrusted(reference, fieldIndex)

private inline fun executePackedStructGet(
    vstack: ValueStack,
    heap: WasmHeap,
    reference: Long,
    destinationSlot: Int,
    fieldIndex: Int,
    packedType: PackedType,
    signed: Boolean,
    crossinline fieldUnpacker: FieldUnpacker,
) {
    val packed = heap.getStructFieldTrusted(reference, fieldIndex)
    vstack.setFrameSlot(destinationSlot, fieldUnpacker(packed, packedType, signed))
}

private fun executeStructSet(
    heap: WasmHeap,
    value: Long,
    reference: Long,
    fieldIndex: Int,
) {
    heap.setStructFieldTrusted(reference, fieldIndex, value)
}

private inline fun arrayAddress(
    vstack: ValueStack,
    addressSlot: Int,
): Long = vstack.getFrameSlot(addressSlot)

private inline fun structReference(
    vstack: ValueStack,
    addressSlot: Int,
): Long = vstack.getFrameSlot(addressSlot)
