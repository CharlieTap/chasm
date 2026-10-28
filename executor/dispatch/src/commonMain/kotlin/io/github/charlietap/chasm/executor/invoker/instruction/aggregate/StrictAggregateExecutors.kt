package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.type.RTT

internal fun executeArrayCopy(
    heap: WasmHeap,
    elementsToCopy: Int,
    sourceOffset: Int,
    sourceAddress: Long,
    destinationOffset: Int,
    destinationAddress: Long,
) {
    try {
        heap.copyArrayChecked(sourceAddress, sourceOffset, destinationAddress, destinationOffset, elementsToCopy)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}

internal fun executeArrayFill(
    heap: WasmHeap,
    elementsToFill: Int,
    fillValue: Long,
    arrayElementOffset: Int,
    address: Long,
) {
    try {
        heap.fillArrayChecked(address, arrayElementOffset, elementsToFill, fillValue)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}

internal fun executeArrayGet(
    vstack: ValueStack,
    heap: WasmHeap,
    fieldIndex: Int,
    address: Long,
    destinationSlot: Int,
) {
    vstack.setFrameSlot(destinationSlot, heap.getArrayElementTrusted(address, fieldIndex))
}

internal fun executeArrayLen(
    vstack: ValueStack,
    heap: WasmHeap,
    address: Long,
    destinationSlot: Int,
) {
    vstack.setFrameSlot(destinationSlot, heap.arrayLengthTrusted(address).toLong())
}

internal fun executeArrayNew(
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

internal fun executeArrayNewFixed(
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

internal fun executeArraySet(
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

internal fun executeStructGet(
    vstack: ValueStack,
    heap: WasmHeap,
    reference: Long,
    destinationSlot: Int,
    fieldIndex: Int,
) {
    vstack.setFrameSlot(destinationSlot, heap.getStructFieldTrusted(reference, fieldIndex))
}

internal fun structField(
    heap: WasmHeap,
    reference: Long,
    fieldIndex: Int,
): Long = heap.getStructFieldTrusted(reference, fieldIndex)

internal fun executeStructSet(
    heap: WasmHeap,
    value: Long,
    reference: Long,
    fieldIndex: Int,
) {
    heap.setStructFieldTrusted(reference, fieldIndex, value)
}

internal inline fun arrayAddress(
    vstack: ValueStack,
    addressSlot: Int,
): Long = vstack.getFrameSlot(addressSlot)

internal inline fun structReference(
    vstack: ValueStack,
    addressSlot: Int,
): Long = vstack.getFrameSlot(addressSlot)
