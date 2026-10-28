package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.executeAnyConvertExtern
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.executeArrayInitData
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.executeArrayInitElement
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.executeArrayNewData
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.executeArrayNewDefault
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.executeArrayNewElement
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.executeExternConvertAny
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.executeI31Get
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.executeRefI31
import io.github.charlietap.chasm.runtime.ext.extendSigned
import io.github.charlietap.chasm.runtime.ext.extendUnsigned
import io.github.charlietap.chasm.runtime.ext.toI31
import io.github.charlietap.chasm.runtime.type.RTT

fun ArrayNewDefaultIDispatcher(
    size: Int,
    destinationSlot: Int,
    rtt: RTT,
    field: Long,
) = dispatchInstruction { vstack, context ->
    executeArrayNewDefault(
        vstack = vstack,
        heap = context.heap,
        context = context,
        size = size,
        destinationSlot = destinationSlot,
        rtt = rtt,
        field = field,
    )
}

fun ArrayNewDefaultSDispatcher(
    sizeSlot: Int,
    destinationSlot: Int,
    rtt: RTT,
    field: Long,
) = dispatchInstruction { vstack, context ->
    executeArrayNewDefault(
        vstack = vstack,
        heap = context.heap,
        context = context,
        size = vstack.getFrameSlot(sizeSlot).toInt(),
        destinationSlot = destinationSlot,
        rtt = rtt,
        field = field,
    )
}

fun ArrayNewDataIiDispatcher(
    sourceOffset: Int,
    arrayLength: Int,
    destinationSlot: Int,
    rtt: RTT,
    dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
    fieldWidthInBytes: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayNewData(
        vstack = vstack,
        heap = context.heap,
        context = context,
        sourceOffset = sourceOffset,
        arrayLength = arrayLength,
        destinationSlot = destinationSlot,
        rtt = rtt,
        dataInstance = dataInstance,
        fieldWidthInBytes = fieldWidthInBytes,
    )
}

fun ArrayNewDataIsDispatcher(
    sourceOffset: Int,
    arrayLengthSlot: Int,
    destinationSlot: Int,
    rtt: RTT,
    dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
    fieldWidthInBytes: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayNewData(
        vstack = vstack,
        heap = context.heap,
        context = context,
        sourceOffset = sourceOffset,
        arrayLength = vstack.getFrameSlot(arrayLengthSlot).toInt(),
        destinationSlot = destinationSlot,
        rtt = rtt,
        dataInstance = dataInstance,
        fieldWidthInBytes = fieldWidthInBytes,
    )
}

fun ArrayNewDataSiDispatcher(
    sourceOffsetSlot: Int,
    arrayLength: Int,
    destinationSlot: Int,
    rtt: RTT,
    dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
    fieldWidthInBytes: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayNewData(
        vstack = vstack,
        heap = context.heap,
        context = context,
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        arrayLength = arrayLength,
        destinationSlot = destinationSlot,
        rtt = rtt,
        dataInstance = dataInstance,
        fieldWidthInBytes = fieldWidthInBytes,
    )
}

fun ArrayNewDataSsDispatcher(
    sourceOffsetSlot: Int,
    arrayLengthSlot: Int,
    destinationSlot: Int,
    rtt: RTT,
    dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
    fieldWidthInBytes: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayNewData(
        vstack = vstack,
        heap = context.heap,
        context = context,
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        arrayLength = vstack.getFrameSlot(arrayLengthSlot).toInt(),
        destinationSlot = destinationSlot,
        rtt = rtt,
        dataInstance = dataInstance,
        fieldWidthInBytes = fieldWidthInBytes,
    )
}

fun ArrayNewElementIiDispatcher(
    sourceOffset: Int,
    arrayLength: Int,
    destinationSlot: Int,
    rtt: RTT,
    elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
) = dispatchInstruction { vstack, context ->
    executeArrayNewElement(
        vstack = vstack,
        heap = context.heap,
        context = context,
        sourceOffset = sourceOffset,
        arrayLength = arrayLength,
        destinationSlot = destinationSlot,
        rtt = rtt,
        elementInstance = elementInstance,
    )
}

fun ArrayNewElementIsDispatcher(
    sourceOffset: Int,
    arrayLengthSlot: Int,
    destinationSlot: Int,
    rtt: RTT,
    elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
) = dispatchInstruction { vstack, context ->
    executeArrayNewElement(
        vstack = vstack,
        heap = context.heap,
        context = context,
        sourceOffset = sourceOffset,
        arrayLength = vstack.getFrameSlot(arrayLengthSlot).toInt(),
        destinationSlot = destinationSlot,
        rtt = rtt,
        elementInstance = elementInstance,
    )
}

fun ArrayNewElementSiDispatcher(
    sourceOffsetSlot: Int,
    arrayLength: Int,
    destinationSlot: Int,
    rtt: RTT,
    elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
) = dispatchInstruction { vstack, context ->
    executeArrayNewElement(
        vstack = vstack,
        heap = context.heap,
        context = context,
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        arrayLength = arrayLength,
        destinationSlot = destinationSlot,
        rtt = rtt,
        elementInstance = elementInstance,
    )
}

fun ArrayNewElementSsDispatcher(
    sourceOffsetSlot: Int,
    arrayLengthSlot: Int,
    destinationSlot: Int,
    rtt: RTT,
    elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
) = dispatchInstruction { vstack, context ->
    executeArrayNewElement(
        vstack = vstack,
        heap = context.heap,
        context = context,
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        arrayLength = vstack.getFrameSlot(arrayLengthSlot).toInt(),
        destinationSlot = destinationSlot,
        rtt = rtt,
        elementInstance = elementInstance,
    )
}

fun ArrayInitDataIiiDispatcher(
    elementsToCopy: Int,
    sourceOffset: Int,
    destinationOffset: Int,
    addressSlot: Int,
    dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
    fieldWidthInBytes: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayInitData(
        heap = context.heap,
        elementsToCopy = elementsToCopy,
        sourceOffset = sourceOffset,
        destinationOffset = destinationOffset,
        address = vstack.getFrameSlot(addressSlot),
        dataInstance = dataInstance,
        fieldWidthInBytes = fieldWidthInBytes,
    )
}

fun ArrayInitDataIisDispatcher(
    elementsToCopy: Int,
    sourceOffset: Int,
    destinationOffsetSlot: Int,
    addressSlot: Int,
    dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
    fieldWidthInBytes: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayInitData(
        heap = context.heap,
        elementsToCopy = elementsToCopy,
        sourceOffset = sourceOffset,
        destinationOffset = vstack.getFrameSlot(destinationOffsetSlot).toInt(),
        address = vstack.getFrameSlot(addressSlot),
        dataInstance = dataInstance,
        fieldWidthInBytes = fieldWidthInBytes,
    )
}

fun ArrayInitDataIsiDispatcher(
    elementsToCopy: Int,
    sourceOffsetSlot: Int,
    destinationOffset: Int,
    addressSlot: Int,
    dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
    fieldWidthInBytes: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayInitData(
        heap = context.heap,
        elementsToCopy = elementsToCopy,
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        destinationOffset = destinationOffset,
        address = vstack.getFrameSlot(addressSlot),
        dataInstance = dataInstance,
        fieldWidthInBytes = fieldWidthInBytes,
    )
}

fun ArrayInitDataIssDispatcher(
    elementsToCopy: Int,
    sourceOffsetSlot: Int,
    destinationOffsetSlot: Int,
    addressSlot: Int,
    dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
    fieldWidthInBytes: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayInitData(
        heap = context.heap,
        elementsToCopy = elementsToCopy,
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        destinationOffset = vstack.getFrameSlot(destinationOffsetSlot).toInt(),
        address = vstack.getFrameSlot(addressSlot),
        dataInstance = dataInstance,
        fieldWidthInBytes = fieldWidthInBytes,
    )
}

fun ArrayInitDataSiiDispatcher(
    elementsToCopySlot: Int,
    sourceOffset: Int,
    destinationOffset: Int,
    addressSlot: Int,
    dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
    fieldWidthInBytes: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayInitData(
        heap = context.heap,
        elementsToCopy = vstack.getFrameSlot(elementsToCopySlot).toInt(),
        sourceOffset = sourceOffset,
        destinationOffset = destinationOffset,
        address = vstack.getFrameSlot(addressSlot),
        dataInstance = dataInstance,
        fieldWidthInBytes = fieldWidthInBytes,
    )
}

fun ArrayInitDataSisDispatcher(
    elementsToCopySlot: Int,
    sourceOffset: Int,
    destinationOffsetSlot: Int,
    addressSlot: Int,
    dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
    fieldWidthInBytes: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayInitData(
        heap = context.heap,
        elementsToCopy = vstack.getFrameSlot(elementsToCopySlot).toInt(),
        sourceOffset = sourceOffset,
        destinationOffset = vstack.getFrameSlot(destinationOffsetSlot).toInt(),
        address = vstack.getFrameSlot(addressSlot),
        dataInstance = dataInstance,
        fieldWidthInBytes = fieldWidthInBytes,
    )
}

fun ArrayInitDataSsiDispatcher(
    elementsToCopySlot: Int,
    sourceOffsetSlot: Int,
    destinationOffset: Int,
    addressSlot: Int,
    dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
    fieldWidthInBytes: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayInitData(
        heap = context.heap,
        elementsToCopy = vstack.getFrameSlot(elementsToCopySlot).toInt(),
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        destinationOffset = destinationOffset,
        address = vstack.getFrameSlot(addressSlot),
        dataInstance = dataInstance,
        fieldWidthInBytes = fieldWidthInBytes,
    )
}

fun ArrayInitDataSssDispatcher(
    elementsToCopySlot: Int,
    sourceOffsetSlot: Int,
    destinationOffsetSlot: Int,
    addressSlot: Int,
    dataInstance: io.github.charlietap.chasm.runtime.instance.DataInstance,
    fieldWidthInBytes: Int,
) = dispatchInstruction { vstack, context ->
    executeArrayInitData(
        heap = context.heap,
        elementsToCopy = vstack.getFrameSlot(elementsToCopySlot).toInt(),
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        destinationOffset = vstack.getFrameSlot(destinationOffsetSlot).toInt(),
        address = vstack.getFrameSlot(addressSlot),
        dataInstance = dataInstance,
        fieldWidthInBytes = fieldWidthInBytes,
    )
}

fun ArrayInitElementIiiDispatcher(
    elementsToCopy: Int,
    sourceOffset: Int,
    destinationOffset: Int,
    addressSlot: Int,
    elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
) = dispatchInstruction { vstack, context ->
    executeArrayInitElement(
        heap = context.heap,
        elementsToCopy = elementsToCopy,
        sourceOffset = sourceOffset,
        destinationOffset = destinationOffset,
        address = vstack.getFrameSlot(addressSlot),
        elementInstance = elementInstance,
    )
}

fun ArrayInitElementIisDispatcher(
    elementsToCopy: Int,
    sourceOffset: Int,
    destinationOffsetSlot: Int,
    addressSlot: Int,
    elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
) = dispatchInstruction { vstack, context ->
    executeArrayInitElement(
        heap = context.heap,
        elementsToCopy = elementsToCopy,
        sourceOffset = sourceOffset,
        destinationOffset = vstack.getFrameSlot(destinationOffsetSlot).toInt(),
        address = vstack.getFrameSlot(addressSlot),
        elementInstance = elementInstance,
    )
}

fun ArrayInitElementIsiDispatcher(
    elementsToCopy: Int,
    sourceOffsetSlot: Int,
    destinationOffset: Int,
    addressSlot: Int,
    elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
) = dispatchInstruction { vstack, context ->
    executeArrayInitElement(
        heap = context.heap,
        elementsToCopy = elementsToCopy,
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        destinationOffset = destinationOffset,
        address = vstack.getFrameSlot(addressSlot),
        elementInstance = elementInstance,
    )
}

fun ArrayInitElementIssDispatcher(
    elementsToCopy: Int,
    sourceOffsetSlot: Int,
    destinationOffsetSlot: Int,
    addressSlot: Int,
    elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
) = dispatchInstruction { vstack, context ->
    executeArrayInitElement(
        heap = context.heap,
        elementsToCopy = elementsToCopy,
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        destinationOffset = vstack.getFrameSlot(destinationOffsetSlot).toInt(),
        address = vstack.getFrameSlot(addressSlot),
        elementInstance = elementInstance,
    )
}

fun ArrayInitElementSiiDispatcher(
    elementsToCopySlot: Int,
    sourceOffset: Int,
    destinationOffset: Int,
    addressSlot: Int,
    elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
) = dispatchInstruction { vstack, context ->
    executeArrayInitElement(
        heap = context.heap,
        elementsToCopy = vstack.getFrameSlot(elementsToCopySlot).toInt(),
        sourceOffset = sourceOffset,
        destinationOffset = destinationOffset,
        address = vstack.getFrameSlot(addressSlot),
        elementInstance = elementInstance,
    )
}

fun ArrayInitElementSisDispatcher(
    elementsToCopySlot: Int,
    sourceOffset: Int,
    destinationOffsetSlot: Int,
    addressSlot: Int,
    elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
) = dispatchInstruction { vstack, context ->
    executeArrayInitElement(
        heap = context.heap,
        elementsToCopy = vstack.getFrameSlot(elementsToCopySlot).toInt(),
        sourceOffset = sourceOffset,
        destinationOffset = vstack.getFrameSlot(destinationOffsetSlot).toInt(),
        address = vstack.getFrameSlot(addressSlot),
        elementInstance = elementInstance,
    )
}

fun ArrayInitElementSsiDispatcher(
    elementsToCopySlot: Int,
    sourceOffsetSlot: Int,
    destinationOffset: Int,
    addressSlot: Int,
    elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
) = dispatchInstruction { vstack, context ->
    executeArrayInitElement(
        heap = context.heap,
        elementsToCopy = vstack.getFrameSlot(elementsToCopySlot).toInt(),
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        destinationOffset = destinationOffset,
        address = vstack.getFrameSlot(addressSlot),
        elementInstance = elementInstance,
    )
}

fun ArrayInitElementSssDispatcher(
    elementsToCopySlot: Int,
    sourceOffsetSlot: Int,
    destinationOffsetSlot: Int,
    addressSlot: Int,
    elementInstance: io.github.charlietap.chasm.runtime.instance.ElementInstance,
) = dispatchInstruction { vstack, context ->
    executeArrayInitElement(
        heap = context.heap,
        elementsToCopy = vstack.getFrameSlot(elementsToCopySlot).toInt(),
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        destinationOffset = vstack.getFrameSlot(destinationOffsetSlot).toInt(),
        address = vstack.getFrameSlot(addressSlot),
        elementInstance = elementInstance,
    )
}

fun RefI31IDispatcher(
    value: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeRefI31(
        vstack = vstack,
        value = value,
        destinationSlot = destinationSlot,
    )
}

fun RefI31SDispatcher(
    valueSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeRefI31(
        vstack = vstack,
        value = vstack.getFrameSlot(valueSlot).toInt(),
        destinationSlot = destinationSlot,
    )
}

fun I31GetSignedSDispatcher(
    valueSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI31Get(
        vstack = vstack,
        value = vstack.getFrameSlot(valueSlot).toI31(),
        destinationSlot = destinationSlot,
        extender = UInt::extendSigned,
    )
}

fun I31GetUnsignedSDispatcher(
    valueSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI31Get(
        vstack = vstack,
        value = vstack.getFrameSlot(valueSlot).toI31(),
        destinationSlot = destinationSlot,
        extender = UInt::extendUnsigned,
    )
}

fun AnyConvertExternSDispatcher(
    valueSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeAnyConvertExtern(
        vstack = vstack,
        referenceValue = vstack.getFrameSlot(valueSlot),
        destinationSlot = destinationSlot,
    )
}

fun ExternConvertAnySDispatcher(
    valueSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeExternConvertAny(
        vstack = vstack,
        referenceValue = vstack.getFrameSlot(valueSlot),
        destinationSlot = destinationSlot,
    )
}
