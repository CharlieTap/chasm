package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.arrayAddress
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.executeArrayCopy
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.executeArrayFill
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.executeArrayGet
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.executeArrayLen
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.executeArrayNew
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.executeArrayNewFixed
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.executeArraySet
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.executeStructGet
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.executeStructSet
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.structField
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.structReference
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.operations.Caster
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest

fun ArrayCopyIiiDispatcher(
    elementsToCopy: Int,
    sourceOffset: Int,
    destinationOffset: Int,
    sourceAddressSlot: Int,
    destinationAddressSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayCopy(
        heap = context.heap,
        elementsToCopy = elementsToCopy,
        sourceOffset = sourceOffset,
        sourceAddress = arrayAddress(vstack, sourceAddressSlot),
        destinationOffset = destinationOffset,
        destinationAddress = arrayAddress(vstack, destinationAddressSlot),
    )
}

fun ArrayCopyIisDispatcher(
    elementsToCopy: Int,
    sourceOffset: Int,
    destinationOffsetSlot: Int,
    sourceAddressSlot: Int,
    destinationAddressSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayCopy(
        heap = context.heap,
        elementsToCopy = elementsToCopy,
        sourceOffset = sourceOffset,
        sourceAddress = arrayAddress(vstack, sourceAddressSlot),
        destinationOffset = vstack.getFrameSlot(destinationOffsetSlot).toInt(),
        destinationAddress = arrayAddress(vstack, destinationAddressSlot),
    )
}

fun ArrayCopyIsiDispatcher(
    elementsToCopy: Int,
    sourceOffsetSlot: Int,
    destinationOffset: Int,
    sourceAddressSlot: Int,
    destinationAddressSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayCopy(
        heap = context.heap,
        elementsToCopy = elementsToCopy,
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        sourceAddress = arrayAddress(vstack, sourceAddressSlot),
        destinationOffset = destinationOffset,
        destinationAddress = arrayAddress(vstack, destinationAddressSlot),
    )
}

fun ArrayCopyIssDispatcher(
    elementsToCopy: Int,
    sourceOffsetSlot: Int,
    destinationOffsetSlot: Int,
    sourceAddressSlot: Int,
    destinationAddressSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayCopy(
        heap = context.heap,
        elementsToCopy = elementsToCopy,
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        sourceAddress = arrayAddress(vstack, sourceAddressSlot),
        destinationOffset = vstack.getFrameSlot(destinationOffsetSlot).toInt(),
        destinationAddress = arrayAddress(vstack, destinationAddressSlot),
    )
}

fun ArrayCopySiiDispatcher(
    elementsToCopySlot: Int,
    sourceOffset: Int,
    destinationOffset: Int,
    sourceAddressSlot: Int,
    destinationAddressSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayCopy(
        heap = context.heap,
        elementsToCopy = vstack.getFrameSlot(elementsToCopySlot).toInt(),
        sourceOffset = sourceOffset,
        sourceAddress = arrayAddress(vstack, sourceAddressSlot),
        destinationOffset = destinationOffset,
        destinationAddress = arrayAddress(vstack, destinationAddressSlot),
    )
}

fun ArrayCopySisDispatcher(
    elementsToCopySlot: Int,
    sourceOffset: Int,
    destinationOffsetSlot: Int,
    sourceAddressSlot: Int,
    destinationAddressSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayCopy(
        heap = context.heap,
        elementsToCopy = vstack.getFrameSlot(elementsToCopySlot).toInt(),
        sourceOffset = sourceOffset,
        sourceAddress = arrayAddress(vstack, sourceAddressSlot),
        destinationOffset = vstack.getFrameSlot(destinationOffsetSlot).toInt(),
        destinationAddress = arrayAddress(vstack, destinationAddressSlot),
    )
}

fun ArrayCopySsiDispatcher(
    elementsToCopySlot: Int,
    sourceOffsetSlot: Int,
    destinationOffset: Int,
    sourceAddressSlot: Int,
    destinationAddressSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayCopy(
        heap = context.heap,
        elementsToCopy = vstack.getFrameSlot(elementsToCopySlot).toInt(),
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        sourceAddress = arrayAddress(vstack, sourceAddressSlot),
        destinationOffset = destinationOffset,
        destinationAddress = arrayAddress(vstack, destinationAddressSlot),
    )
}

fun ArrayCopySssDispatcher(
    elementsToCopySlot: Int,
    sourceOffsetSlot: Int,
    destinationOffsetSlot: Int,
    sourceAddressSlot: Int,
    destinationAddressSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayCopy(
        heap = context.heap,
        elementsToCopy = vstack.getFrameSlot(elementsToCopySlot).toInt(),
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        sourceAddress = arrayAddress(vstack, sourceAddressSlot),
        destinationOffset = vstack.getFrameSlot(destinationOffsetSlot).toInt(),
        destinationAddress = arrayAddress(vstack, destinationAddressSlot),
    )
}

fun ArrayFillIiiDispatcher(
    elementsToFill: Int,
    fillValue: Long,
    arrayElementOffset: Int,
    addressSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayFill(
        heap = context.heap,
        elementsToFill = elementsToFill,
        fillValue = fillValue,
        arrayElementOffset = arrayElementOffset,
        address = arrayAddress(vstack, addressSlot),
    )
}

fun ArrayFillIisDispatcher(
    elementsToFill: Int,
    fillValue: Long,
    arrayElementOffsetSlot: Int,
    addressSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayFill(
        heap = context.heap,
        elementsToFill = elementsToFill,
        fillValue = fillValue,
        arrayElementOffset = vstack.getFrameSlot(arrayElementOffsetSlot).toInt(),
        address = arrayAddress(vstack, addressSlot),
    )
}

fun ArrayFillIsiDispatcher(
    elementsToFill: Int,
    fillValueSlot: Int,
    arrayElementOffset: Int,
    addressSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayFill(
        heap = context.heap,
        elementsToFill = elementsToFill,
        fillValue = vstack.getFrameSlot(fillValueSlot),
        arrayElementOffset = arrayElementOffset,
        address = arrayAddress(vstack, addressSlot),
    )
}

fun ArrayFillIssDispatcher(
    elementsToFill: Int,
    fillValueSlot: Int,
    arrayElementOffsetSlot: Int,
    addressSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayFill(
        heap = context.heap,
        elementsToFill = elementsToFill,
        fillValue = vstack.getFrameSlot(fillValueSlot),
        arrayElementOffset = vstack.getFrameSlot(arrayElementOffsetSlot).toInt(),
        address = arrayAddress(vstack, addressSlot),
    )
}

fun ArrayFillSiiDispatcher(
    elementsToFillSlot: Int,
    fillValue: Long,
    arrayElementOffset: Int,
    addressSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayFill(
        heap = context.heap,
        elementsToFill = vstack.getFrameSlot(elementsToFillSlot).toInt(),
        fillValue = fillValue,
        arrayElementOffset = arrayElementOffset,
        address = arrayAddress(vstack, addressSlot),
    )
}

fun ArrayFillSisDispatcher(
    elementsToFillSlot: Int,
    fillValue: Long,
    arrayElementOffsetSlot: Int,
    addressSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayFill(
        heap = context.heap,
        elementsToFill = vstack.getFrameSlot(elementsToFillSlot).toInt(),
        fillValue = fillValue,
        arrayElementOffset = vstack.getFrameSlot(arrayElementOffsetSlot).toInt(),
        address = arrayAddress(vstack, addressSlot),
    )
}

fun ArrayFillSsiDispatcher(
    elementsToFillSlot: Int,
    fillValueSlot: Int,
    arrayElementOffset: Int,
    addressSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayFill(
        heap = context.heap,
        elementsToFill = vstack.getFrameSlot(elementsToFillSlot).toInt(),
        fillValue = vstack.getFrameSlot(fillValueSlot),
        arrayElementOffset = arrayElementOffset,
        address = arrayAddress(vstack, addressSlot),
    )
}

fun ArrayFillSssDispatcher(
    elementsToFillSlot: Int,
    fillValueSlot: Int,
    arrayElementOffsetSlot: Int,
    addressSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayFill(
        heap = context.heap,
        elementsToFill = vstack.getFrameSlot(elementsToFillSlot).toInt(),
        fillValue = vstack.getFrameSlot(fillValueSlot),
        arrayElementOffset = vstack.getFrameSlot(arrayElementOffsetSlot).toInt(),
        address = arrayAddress(vstack, addressSlot),
    )
}

fun ArrayGetIDispatcher(
    addressSlot: Int,
    field: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayGet(
        vstack = vstack,
        heap = context.heap,
        fieldIndex = field,
        address = arrayAddress(vstack, addressSlot),
        destinationSlot = destinationSlot,
    )
}

fun ArrayGetSDispatcher(
    addressSlot: Int,
    fieldSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayGet(
        vstack = vstack,
        heap = context.heap,
        fieldIndex = vstack.getFrameSlot(fieldSlot).toInt(),
        address = arrayAddress(vstack, addressSlot),
        destinationSlot = destinationSlot,
    )
}

fun ArrayLenSDispatcher(
    addressSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayLen(
        vstack = vstack,
        heap = context.heap,
        address = arrayAddress(vstack, addressSlot),
        destinationSlot = destinationSlot,
    )
}

fun ArrayNewIiDispatcher(
    size: Int,
    value: Long,
    destinationSlot: Int,
    rtt: RTT,
) = dispatchInstruction { vstack, context ->
    executeArrayNew(
        vstack = vstack,
        heap = context.heap,
        context = context,
        size = size,
        value = value,
        destinationSlot = destinationSlot,
        rtt = rtt,
    )
}

fun ArrayNewIsDispatcher(
    size: Int,
    valueSlot: Int,
    destinationSlot: Int,
    rtt: RTT,
) = dispatchInstruction { vstack, context ->
    executeArrayNew(
        vstack = vstack,
        heap = context.heap,
        context = context,
        size = size,
        value = vstack.getFrameSlot(valueSlot),
        destinationSlot = destinationSlot,
        rtt = rtt,
    )
}

fun ArrayNewSiDispatcher(
    sizeSlot: Int,
    value: Long,
    destinationSlot: Int,
    rtt: RTT,
) = dispatchInstruction { vstack, context ->
    executeArrayNew(
        vstack = vstack,
        heap = context.heap,
        context = context,
        size = vstack.getFrameSlot(sizeSlot).toInt(),
        value = value,
        destinationSlot = destinationSlot,
        rtt = rtt,
    )
}

fun ArrayNewSsDispatcher(
    sizeSlot: Int,
    valueSlot: Int,
    destinationSlot: Int,
    rtt: RTT,
) = dispatchInstruction { vstack, context ->
    executeArrayNew(
        vstack = vstack,
        heap = context.heap,
        context = context,
        size = vstack.getFrameSlot(sizeSlot).toInt(),
        value = vstack.getFrameSlot(valueSlot),
        destinationSlot = destinationSlot,
        rtt = rtt,
    )
}

fun ArrayNewFixedSDispatcher(
    firstElementSlot: Int,
    destinationSlot: Int,
    rtt: RTT,
    size: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayNewFixed(
        vstack = vstack,
        heap = context.heap,
        context = context,
        firstElementSlot = firstElementSlot,
        destinationSlot = destinationSlot,
        rtt = rtt,
        size = size,
    )
}

fun ArraySetIiDispatcher(
    value: Long,
    field: Int,
    addressSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArraySet(
        heap = context.heap,
        value = value,
        fieldIndex = field,
        address = arrayAddress(vstack, addressSlot),
    )
}

fun ArraySetIsDispatcher(
    value: Long,
    fieldSlot: Int,
    addressSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArraySet(
        heap = context.heap,
        value = value,
        fieldIndex = vstack.getFrameSlot(fieldSlot).toInt(),
        address = arrayAddress(vstack, addressSlot),
    )
}

fun ArraySetSiDispatcher(
    valueSlot: Int,
    field: Int,
    addressSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArraySet(
        heap = context.heap,
        value = vstack.getFrameSlot(valueSlot),
        fieldIndex = field,
        address = arrayAddress(vstack, addressSlot),
    )
}

fun ArraySetSsDispatcher(
    valueSlot: Int,
    fieldSlot: Int,
    addressSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeArraySet(
        heap = context.heap,
        value = vstack.getFrameSlot(valueSlot),
        fieldIndex = vstack.getFrameSlot(fieldSlot).toInt(),
        address = arrayAddress(vstack, addressSlot),
    )
}

fun StructGetSDispatcher(
    addressSlot: Int,
    destinationSlot: Int,
    fieldIndex: Int,
) = dispatchInstruction { vstack, context ->
    executeStructGet(
        vstack = vstack,
        heap = context.heap,
        reference = structReference(vstack, addressSlot),
        destinationSlot = destinationSlot,
        fieldIndex = fieldIndex,
    )
}

fun RefCastStructGetSDispatcher(
    referenceSlot: Int,
    destinationSlot: Int,
    typeTest: ReferenceTypeTest,
    fieldIndex: Int,
) = dispatchInstruction { vstack, context ->
    run {
        val reference = vstack.getFrameSlot(referenceSlot)
        if (!Caster(reference, typeTest, context)) {
            throw InvocationException(InvocationError.FailedToCastReference)
        }
        vstack.setFrameSlot(
            destinationSlot,
            context.heap.getStructFieldTrusted(reference, fieldIndex),
        )
    }
}

fun StructGetStructGetSDispatcher(
    addressSlot: Int,
    destinationSlot: Int,
    firstFieldIndex: Int,
    secondFieldIndex: Int,
) = dispatchInstruction { vstack, context ->
    run {
        val reference = structField(
            heap = context.heap,
            reference = vstack.getFrameSlot(addressSlot),
            fieldIndex = firstFieldIndex,
        )
        vstack.setFrameSlot(
            destinationSlot,
            structField(context.heap, reference, secondFieldIndex),
        )
    }
}

fun LocalSetStructGetSDispatcher(
    sourceSlot: Int,
    localSlot: Int,
    destinationSlot: Int,
    fieldIndex: Int,
) = dispatchInstruction { vstack, context ->
    run {
        val reference = vstack.getFrameSlot(sourceSlot)
        vstack.setFrameSlot(localSlot, reference)
        vstack.setFrameSlot(
            destinationSlot,
            structField(context.heap, reference, fieldIndex),
        )
    }
}

fun StructNewSDispatcher(
    firstFieldSlot: Int,
    destinationSlot: Int,
    rtt: RTT,
) = dispatchInstruction { vstack, context ->
    run {
        context.heap.allocateStructFromFrame(
            context = context,
            runtimeType = rtt,
            firstFieldSlot = firstFieldSlot,
            destinationSlot = destinationSlot,
        )
    }
}

fun StructNewDefaultSDispatcher(
    destinationSlot: Int,
    rtt: RTT,
    fields: LongArray,
) = dispatchInstruction { vstack, context ->
    run {
        vstack.setFrameSlot(
            destinationSlot,
            context.heap.allocateStruct(context, rtt, fields),
        )
    }
}

fun StructSetIDispatcher(
    value: Long,
    addressSlot: Int,
    fieldIndex: Int,
) = dispatchInstruction { vstack, context ->
    executeStructSet(
        heap = context.heap,
        value = value,
        reference = structReference(vstack, addressSlot),
        fieldIndex = fieldIndex,
    )
}

fun StructSetSDispatcher(
    valueSlot: Int,
    addressSlot: Int,
    fieldIndex: Int,
) = dispatchInstruction { vstack, context ->
    executeStructSet(
        heap = context.heap,
        value = vstack.getFrameSlot(valueSlot),
        reference = structReference(vstack, addressSlot),
        fieldIndex = fieldIndex,
    )
}
