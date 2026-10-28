package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.isExternReference
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.ext.toExternReference
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.ext.toReferenceValue
import io.github.charlietap.chasm.runtime.ext.wrapI31
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType

internal fun executeArrayNewDefault(
    vstack: ValueStack,
    heap: WasmHeap,
    context: ExecutionContext,
    size: Int,
    destinationSlot: Int,
    rtt: RTT,
    field: Long,
) {
    try {
        vstack.setFrameSlot(destinationSlot, heap.allocateArrayFilled(context, rtt, size, field))
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}

internal fun executeArrayNewData(
    vstack: ValueStack,
    heap: WasmHeap,
    context: ExecutionContext,
    sourceOffset: Int,
    arrayLength: Int,
    destinationSlot: Int,
    rtt: RTT,
    dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
    fieldWidthInBytes: Int,
) {
    val reference = try {
        heap.allocateArrayFromData(
            context,
            rtt,
            dataInstance.bytes,
            sourceOffset,
            arrayLength,
            fieldWidthInBytes,
        )
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
    vstack.setFrameSlot(destinationSlot, reference)
}

internal fun executeArrayNewElement(
    vstack: ValueStack,
    heap: WasmHeap,
    context: ExecutionContext,
    sourceOffset: Int,
    arrayLength: Int,
    destinationSlot: Int,
    rtt: RTT,
    elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
) {
    val reference = try {
        heap.allocateArrayFromElements(
            context,
            rtt,
            elementInstance.elements,
            sourceOffset,
            arrayLength,
        )
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
    vstack.setFrameSlot(destinationSlot, reference)
}

internal fun executeArrayInitData(
    heap: WasmHeap,
    elementsToCopy: Int,
    sourceOffset: Int,
    destinationOffset: Int,
    address: Long,
    dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
    fieldWidthInBytes: Int,
) {
    try {
        heap.initializeArrayFromData(
            address,
            destinationOffset,
            dataInstance.bytes,
            sourceOffset,
            elementsToCopy,
            fieldWidthInBytes,
        )
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}

internal fun executeArrayInitElement(
    heap: WasmHeap,
    elementsToCopy: Int,
    sourceOffset: Int,
    destinationOffset: Int,
    address: Long,
    elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
) {
    try {
        heap.initializeArrayFromElements(
            address,
            destinationOffset,
            elementInstance.elements,
            sourceOffset,
            elementsToCopy,
        )
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}

internal inline fun executeRefI31(
    vstack: ValueStack,
    value: Int,
    destinationSlot: Int,
) {
    vstack.setFrameSlot(destinationSlot, value.wrapI31().toLong())
}

internal inline fun executeI31Get(
    vstack: ValueStack,
    value: UInt,
    destinationSlot: Int,
    crossinline extender: (UInt) -> Int,
) {
    vstack.setFrameSlot(destinationSlot, extender(value).toLong())
}

internal inline fun executeAnyConvertExtern(
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

internal inline fun executeExternConvertAny(
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
