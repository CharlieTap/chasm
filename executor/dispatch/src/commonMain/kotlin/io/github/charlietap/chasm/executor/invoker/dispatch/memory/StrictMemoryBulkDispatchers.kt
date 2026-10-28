package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.memory.bulk.MemoryCopyExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.bulk.MemoryFillExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.bulk.MemoryInitExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.bulk.executeMemoryGrow
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance

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
