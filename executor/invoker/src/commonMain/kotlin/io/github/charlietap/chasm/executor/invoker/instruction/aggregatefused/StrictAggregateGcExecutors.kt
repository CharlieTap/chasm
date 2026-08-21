package io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.extendSigned
import io.github.charlietap.chasm.runtime.ext.extendUnsigned
import io.github.charlietap.chasm.runtime.ext.isExternReference
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.ext.toExternReference
import io.github.charlietap.chasm.runtime.ext.toI31
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.ext.toReferenceValue
import io.github.charlietap.chasm.runtime.ext.wrapI31
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.instruction.AggregateSuperInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType

internal inline fun ArrayNewDefaultExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayNewDefaultI,
) = executeArrayNewDefault(
    vstack = vstack,
    heap = context.heap,
    context = context,
    size = instruction.size,
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
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
    heap = context.heap,
    context = context,
    size = vstack.getFrameSlot(instruction.sizeSlot).toInt(),
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
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
    heap = context.heap,
    context = context,
    sourceOffset = instruction.sourceOffset,
    arrayLength = instruction.arrayLength,
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
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
    heap = context.heap,
    context = context,
    sourceOffset = instruction.sourceOffset,
    arrayLength = vstack.getFrameSlot(instruction.arrayLengthSlot).toInt(),
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
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
    heap = context.heap,
    context = context,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    arrayLength = instruction.arrayLength,
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
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
    heap = context.heap,
    context = context,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    arrayLength = vstack.getFrameSlot(instruction.arrayLengthSlot).toInt(),
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
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
    heap = context.heap,
    context = context,
    sourceOffset = instruction.sourceOffset,
    arrayLength = instruction.arrayLength,
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
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
    heap = context.heap,
    context = context,
    sourceOffset = instruction.sourceOffset,
    arrayLength = vstack.getFrameSlot(instruction.arrayLengthSlot).toInt(),
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
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
    heap = context.heap,
    context = context,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    arrayLength = instruction.arrayLength,
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
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
    heap = context.heap,
    context = context,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    arrayLength = vstack.getFrameSlot(instruction.arrayLengthSlot).toInt(),
    destinationSlot = instruction.destinationSlot,
    rtt = instruction.rtt,
    elementInstance = instruction.elementInstance,
)

internal inline fun ArrayInitDataExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitDataIii,
) = executeArrayInitData(
    heap = context.heap,
    elementsToCopy = instruction.elementsToCopy,
    sourceOffset = instruction.sourceOffset,
    destinationOffset = instruction.destinationOffset,
    address = vstack.getFrameSlot(instruction.addressSlot),
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
    heap = context.heap,
    elementsToCopy = instruction.elementsToCopy,
    sourceOffset = instruction.sourceOffset,
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    address = vstack.getFrameSlot(instruction.addressSlot),
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
    heap = context.heap,
    elementsToCopy = instruction.elementsToCopy,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = instruction.destinationOffset,
    address = vstack.getFrameSlot(instruction.addressSlot),
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
    heap = context.heap,
    elementsToCopy = instruction.elementsToCopy,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    address = vstack.getFrameSlot(instruction.addressSlot),
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
    heap = context.heap,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    sourceOffset = instruction.sourceOffset,
    destinationOffset = instruction.destinationOffset,
    address = vstack.getFrameSlot(instruction.addressSlot),
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
    heap = context.heap,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    sourceOffset = instruction.sourceOffset,
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    address = vstack.getFrameSlot(instruction.addressSlot),
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
    heap = context.heap,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = instruction.destinationOffset,
    address = vstack.getFrameSlot(instruction.addressSlot),
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
    heap = context.heap,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    address = vstack.getFrameSlot(instruction.addressSlot),
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
    heap = context.heap,
    elementsToCopy = instruction.elementsToCopy,
    sourceOffset = instruction.sourceOffset,
    destinationOffset = instruction.destinationOffset,
    address = vstack.getFrameSlot(instruction.addressSlot),
    elementInstance = instruction.elementInstance,
)

internal inline fun ArrayInitElementExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitElementIis,
) = executeArrayInitElement(
    heap = context.heap,
    elementsToCopy = instruction.elementsToCopy,
    sourceOffset = instruction.sourceOffset,
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    address = vstack.getFrameSlot(instruction.addressSlot),
    elementInstance = instruction.elementInstance,
)

internal inline fun ArrayInitElementExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitElementIsi,
) = executeArrayInitElement(
    heap = context.heap,
    elementsToCopy = instruction.elementsToCopy,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = instruction.destinationOffset,
    address = vstack.getFrameSlot(instruction.addressSlot),
    elementInstance = instruction.elementInstance,
)

internal inline fun ArrayInitElementExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitElementIss,
) = executeArrayInitElement(
    heap = context.heap,
    elementsToCopy = instruction.elementsToCopy,
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    address = vstack.getFrameSlot(instruction.addressSlot),
    elementInstance = instruction.elementInstance,
)

internal inline fun ArrayInitElementExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitElementSii,
) = executeArrayInitElement(
    heap = context.heap,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    sourceOffset = instruction.sourceOffset,
    destinationOffset = instruction.destinationOffset,
    address = vstack.getFrameSlot(instruction.addressSlot),
    elementInstance = instruction.elementInstance,
)

internal inline fun ArrayInitElementExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitElementSis,
) = executeArrayInitElement(
    heap = context.heap,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    sourceOffset = instruction.sourceOffset,
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    address = vstack.getFrameSlot(instruction.addressSlot),
    elementInstance = instruction.elementInstance,
)

internal inline fun ArrayInitElementExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitElementSsi,
) = executeArrayInitElement(
    heap = context.heap,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = instruction.destinationOffset,
    address = vstack.getFrameSlot(instruction.addressSlot),
    elementInstance = instruction.elementInstance,
)

internal inline fun ArrayInitElementExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateSuperInstruction.ArrayInitElementSss,
) = executeArrayInitElement(
    heap = context.heap,
    elementsToCopy = vstack.getFrameSlot(instruction.elementsToCopySlot).toInt(),
    sourceOffset = vstack.getFrameSlot(instruction.sourceOffsetSlot).toInt(),
    destinationOffset = vstack.getFrameSlot(instruction.destinationOffsetSlot).toInt(),
    address = vstack.getFrameSlot(instruction.addressSlot),
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

private fun executeArrayNewData(
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

private fun executeArrayNewElement(
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

private fun executeArrayInitData(
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

private fun executeArrayInitElement(
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
