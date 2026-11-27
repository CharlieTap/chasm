package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.memory.bulk.MemoryCopyExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.bulk.MemoryFillExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.bulk.MemoryInitExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.bulk.executeMemoryGrow
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance

@InternalChasmApi
fun MemoryGrowIDispatcher(
    pagesToAdd: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
) = dispatchInstruction { vstack, context ->
    executeMemoryGrow(
        vstack = vstack,
        context = context,
        pagesToAdd = pagesToAdd,
        destinationSlot = destinationSlot,
        memory = memory,
    )
}

@InternalChasmApi
fun MemoryGrowSDispatcher(
    pagesToAddSlot: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
) = dispatchInstruction { vstack, context ->
    executeMemoryGrow(
        vstack = vstack,
        context = context,
        pagesToAdd = vstack.getFrameSlot(pagesToAddSlot).toInt(),
        destinationSlot = destinationSlot,
        memory = memory,
    )
}

@InternalChasmApi
fun MemoryInitIiiDispatcher(
    bytesToCopy: Int,
    sourceOffset: Int,
    destinationOffset: Int,
    memory: MemoryInstance,
    data: io.github.charlietap.chasm.runtime.instance.DataInstance,
) = dispatchInstruction { vstack, context ->
    MemoryInitExecutor(
        bytesToCopy = bytesToCopy,
        sourceOffset = sourceOffset,
        destinationOffset = destinationOffset,
        memory = memory,
        data = data,
    )
}

@InternalChasmApi
fun MemoryInitIisDispatcher(
    bytesToCopy: Int,
    sourceOffset: Int,
    destinationOffsetSlot: Int,
    memory: MemoryInstance,
    data: io.github.charlietap.chasm.runtime.instance.DataInstance,
) = dispatchInstruction { vstack, context ->
    MemoryInitExecutor(
        bytesToCopy = bytesToCopy,
        sourceOffset = sourceOffset,
        destinationOffset = vstack.getFrameSlot(destinationOffsetSlot).toInt(),
        memory = memory,
        data = data,
    )
}

@InternalChasmApi
fun MemoryInitIsiDispatcher(
    bytesToCopy: Int,
    sourceOffsetSlot: Int,
    destinationOffset: Int,
    memory: MemoryInstance,
    data: io.github.charlietap.chasm.runtime.instance.DataInstance,
) = dispatchInstruction { vstack, context ->
    MemoryInitExecutor(
        bytesToCopy = bytesToCopy,
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        destinationOffset = destinationOffset,
        memory = memory,
        data = data,
    )
}

@InternalChasmApi
fun MemoryInitIssDispatcher(
    bytesToCopy: Int,
    sourceOffsetSlot: Int,
    destinationOffsetSlot: Int,
    memory: MemoryInstance,
    data: io.github.charlietap.chasm.runtime.instance.DataInstance,
) = dispatchInstruction { vstack, context ->
    MemoryInitExecutor(
        bytesToCopy = bytesToCopy,
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        destinationOffset = vstack.getFrameSlot(destinationOffsetSlot).toInt(),
        memory = memory,
        data = data,
    )
}

@InternalChasmApi
fun MemoryInitSiiDispatcher(
    bytesToCopySlot: Int,
    sourceOffset: Int,
    destinationOffset: Int,
    memory: MemoryInstance,
    data: io.github.charlietap.chasm.runtime.instance.DataInstance,
) = dispatchInstruction { vstack, context ->
    MemoryInitExecutor(
        bytesToCopy = vstack.getFrameSlot(bytesToCopySlot).toInt(),
        sourceOffset = sourceOffset,
        destinationOffset = destinationOffset,
        memory = memory,
        data = data,
    )
}

@InternalChasmApi
fun MemoryInitSisDispatcher(
    bytesToCopySlot: Int,
    sourceOffset: Int,
    destinationOffsetSlot: Int,
    memory: MemoryInstance,
    data: io.github.charlietap.chasm.runtime.instance.DataInstance,
) = dispatchInstruction { vstack, context ->
    MemoryInitExecutor(
        bytesToCopy = vstack.getFrameSlot(bytesToCopySlot).toInt(),
        sourceOffset = sourceOffset,
        destinationOffset = vstack.getFrameSlot(destinationOffsetSlot).toInt(),
        memory = memory,
        data = data,
    )
}

@InternalChasmApi
fun MemoryInitSsiDispatcher(
    bytesToCopySlot: Int,
    sourceOffsetSlot: Int,
    destinationOffset: Int,
    memory: MemoryInstance,
    data: io.github.charlietap.chasm.runtime.instance.DataInstance,
) = dispatchInstruction { vstack, context ->
    MemoryInitExecutor(
        bytesToCopy = vstack.getFrameSlot(bytesToCopySlot).toInt(),
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        destinationOffset = destinationOffset,
        memory = memory,
        data = data,
    )
}

@InternalChasmApi
fun MemoryInitSssDispatcher(
    bytesToCopySlot: Int,
    sourceOffsetSlot: Int,
    destinationOffsetSlot: Int,
    memory: MemoryInstance,
    data: io.github.charlietap.chasm.runtime.instance.DataInstance,
) = dispatchInstruction { vstack, context ->
    MemoryInitExecutor(
        bytesToCopy = vstack.getFrameSlot(bytesToCopySlot).toInt(),
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        destinationOffset = vstack.getFrameSlot(destinationOffsetSlot).toInt(),
        memory = memory,
        data = data,
    )
}

@InternalChasmApi
fun MemoryCopyIiiDispatcher(
    bytesToCopy: Int,
    sourceOffset: Int,
    destinationOffset: Int,
    srcMemory: MemoryInstance,
    dstMemory: MemoryInstance,
) = dispatchInstruction { vstack, context ->
    MemoryCopyExecutor(
        bytesToCopy = bytesToCopy,
        sourceOffset = sourceOffset,
        destinationOffset = destinationOffset,
        srcMemory = srcMemory,
        dstMemory = dstMemory,
    )
}

@InternalChasmApi
fun MemoryCopyIisDispatcher(
    bytesToCopy: Int,
    sourceOffset: Int,
    destinationOffsetSlot: Int,
    srcMemory: MemoryInstance,
    dstMemory: MemoryInstance,
) = dispatchInstruction { vstack, context ->
    MemoryCopyExecutor(
        bytesToCopy = bytesToCopy,
        sourceOffset = sourceOffset,
        destinationOffset = vstack.getFrameSlot(destinationOffsetSlot).toInt(),
        srcMemory = srcMemory,
        dstMemory = dstMemory,
    )
}

@InternalChasmApi
fun MemoryCopyIsiDispatcher(
    bytesToCopy: Int,
    sourceOffsetSlot: Int,
    destinationOffset: Int,
    srcMemory: MemoryInstance,
    dstMemory: MemoryInstance,
) = dispatchInstruction { vstack, context ->
    MemoryCopyExecutor(
        bytesToCopy = bytesToCopy,
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        destinationOffset = destinationOffset,
        srcMemory = srcMemory,
        dstMemory = dstMemory,
    )
}

@InternalChasmApi
fun MemoryCopyIssDispatcher(
    bytesToCopy: Int,
    sourceOffsetSlot: Int,
    destinationOffsetSlot: Int,
    srcMemory: MemoryInstance,
    dstMemory: MemoryInstance,
) = dispatchInstruction { vstack, context ->
    MemoryCopyExecutor(
        bytesToCopy = bytesToCopy,
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        destinationOffset = vstack.getFrameSlot(destinationOffsetSlot).toInt(),
        srcMemory = srcMemory,
        dstMemory = dstMemory,
    )
}

@InternalChasmApi
fun MemoryCopySiiDispatcher(
    bytesToCopySlot: Int,
    sourceOffset: Int,
    destinationOffset: Int,
    srcMemory: MemoryInstance,
    dstMemory: MemoryInstance,
) = dispatchInstruction { vstack, context ->
    MemoryCopyExecutor(
        bytesToCopy = vstack.getFrameSlot(bytesToCopySlot).toInt(),
        sourceOffset = sourceOffset,
        destinationOffset = destinationOffset,
        srcMemory = srcMemory,
        dstMemory = dstMemory,
    )
}

@InternalChasmApi
fun MemoryCopySisDispatcher(
    bytesToCopySlot: Int,
    sourceOffset: Int,
    destinationOffsetSlot: Int,
    srcMemory: MemoryInstance,
    dstMemory: MemoryInstance,
) = dispatchInstruction { vstack, context ->
    MemoryCopyExecutor(
        bytesToCopy = vstack.getFrameSlot(bytesToCopySlot).toInt(),
        sourceOffset = sourceOffset,
        destinationOffset = vstack.getFrameSlot(destinationOffsetSlot).toInt(),
        srcMemory = srcMemory,
        dstMemory = dstMemory,
    )
}

@InternalChasmApi
fun MemoryCopySsiDispatcher(
    bytesToCopySlot: Int,
    sourceOffsetSlot: Int,
    destinationOffset: Int,
    srcMemory: MemoryInstance,
    dstMemory: MemoryInstance,
) = dispatchInstruction { vstack, context ->
    MemoryCopyExecutor(
        bytesToCopy = vstack.getFrameSlot(bytesToCopySlot).toInt(),
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        destinationOffset = destinationOffset,
        srcMemory = srcMemory,
        dstMemory = dstMemory,
    )
}

@InternalChasmApi
fun MemoryCopySssDispatcher(
    bytesToCopySlot: Int,
    sourceOffsetSlot: Int,
    destinationOffsetSlot: Int,
    srcMemory: MemoryInstance,
    dstMemory: MemoryInstance,
) = dispatchInstruction { vstack, context ->
    MemoryCopyExecutor(
        bytesToCopy = vstack.getFrameSlot(bytesToCopySlot).toInt(),
        sourceOffset = vstack.getFrameSlot(sourceOffsetSlot).toInt(),
        destinationOffset = vstack.getFrameSlot(destinationOffsetSlot).toInt(),
        srcMemory = srcMemory,
        dstMemory = dstMemory,
    )
}

@InternalChasmApi
fun MemoryFillIiiDispatcher(
    bytesToFill: Int,
    fillValue: Int,
    offset: Int,
    memory: MemoryInstance,
) = dispatchInstruction { vstack, context ->
    MemoryFillExecutor(
        bytesToFill = bytesToFill,
        fillValue = fillValue,
        offset = offset,
        memory = memory,
    )
}

@InternalChasmApi
fun MemoryFillIisDispatcher(
    bytesToFill: Int,
    fillValue: Int,
    offsetSlot: Int,
    memory: MemoryInstance,
) = dispatchInstruction { vstack, context ->
    MemoryFillExecutor(
        bytesToFill = bytesToFill,
        fillValue = fillValue,
        offset = vstack.getFrameSlot(offsetSlot).toInt(),
        memory = memory,
    )
}

@InternalChasmApi
fun MemoryFillIsiDispatcher(
    bytesToFill: Int,
    fillValueSlot: Int,
    offset: Int,
    memory: MemoryInstance,
) = dispatchInstruction { vstack, context ->
    MemoryFillExecutor(
        bytesToFill = bytesToFill,
        fillValue = vstack.getFrameSlot(fillValueSlot).toInt(),
        offset = offset,
        memory = memory,
    )
}

@InternalChasmApi
fun MemoryFillIssDispatcher(
    bytesToFill: Int,
    fillValueSlot: Int,
    offsetSlot: Int,
    memory: MemoryInstance,
) = dispatchInstruction { vstack, context ->
    MemoryFillExecutor(
        bytesToFill = bytesToFill,
        fillValue = vstack.getFrameSlot(fillValueSlot).toInt(),
        offset = vstack.getFrameSlot(offsetSlot).toInt(),
        memory = memory,
    )
}

@InternalChasmApi
fun MemoryFillSiiDispatcher(
    bytesToFillSlot: Int,
    fillValue: Int,
    offset: Int,
    memory: MemoryInstance,
) = dispatchInstruction { vstack, context ->
    MemoryFillExecutor(
        bytesToFill = vstack.getFrameSlot(bytesToFillSlot).toInt(),
        fillValue = fillValue,
        offset = offset,
        memory = memory,
    )
}

@InternalChasmApi
fun MemoryFillSisDispatcher(
    bytesToFillSlot: Int,
    fillValue: Int,
    offsetSlot: Int,
    memory: MemoryInstance,
) = dispatchInstruction { vstack, context ->
    MemoryFillExecutor(
        bytesToFill = vstack.getFrameSlot(bytesToFillSlot).toInt(),
        fillValue = fillValue,
        offset = vstack.getFrameSlot(offsetSlot).toInt(),
        memory = memory,
    )
}

@InternalChasmApi
fun MemoryFillSsiDispatcher(
    bytesToFillSlot: Int,
    fillValueSlot: Int,
    offset: Int,
    memory: MemoryInstance,
) = dispatchInstruction { vstack, context ->
    MemoryFillExecutor(
        bytesToFill = vstack.getFrameSlot(bytesToFillSlot).toInt(),
        fillValue = vstack.getFrameSlot(fillValueSlot).toInt(),
        offset = offset,
        memory = memory,
    )
}

@InternalChasmApi
fun MemoryFillSssDispatcher(
    bytesToFillSlot: Int,
    fillValueSlot: Int,
    offsetSlot: Int,
    memory: MemoryInstance,
) = dispatchInstruction { vstack, context ->
    MemoryFillExecutor(
        bytesToFill = vstack.getFrameSlot(bytesToFillSlot).toInt(),
        fillValue = vstack.getFrameSlot(fillValueSlot).toInt(),
        offset = vstack.getFrameSlot(offsetSlot).toInt(),
        memory = memory,
    )
}
