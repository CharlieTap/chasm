package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction

fun CopySlotDispatcher(
    sourceSlot: Int,
    destinationSlot: Int,
): DispatchableInstruction = DispatchableInstruction { vstack, _, nextIp ->
    vstack.setFrameSlot(destinationSlot, vstack.getFrameSlot(sourceSlot))
    nextIp
}

fun CopySlotSequenceDispatcher(
    sourceSlots: IntArray,
    destinationSlots: IntArray,
): DispatchableInstruction {
    require(sourceSlots.size == destinationSlots.size)
    if (sourceSlots.size == 1) {
        return CopySlotDispatcher(sourceSlots[0], destinationSlots[0])
    }

    return DispatchableInstruction { vstack, _, nextIp ->
        for (index in sourceSlots.indices) {
            vstack.setFrameSlot(destinationSlots[index], vstack.getFrameSlot(sourceSlots[index]))
        }
        nextIp
    }
}

fun CopySlotsDispatcher(
    sourceSlots: IntArray,
    destinationSlots: IntArray,
): DispatchableInstruction {
    if (sourceSlots.size == 1) {
        val sourceSlot = sourceSlots[0]
        val destinationSlot = destinationSlots[0]
        return CopySlotDispatcher(sourceSlot, destinationSlot)
    }

    return DispatchableInstruction { vstack, context, nextIp ->
        run {
            val copiedValues = LongArray(sourceSlots.size) { index ->
                vstack.getFrameSlot(sourceSlots[index])
            }

            for (index in destinationSlots.indices) {
                vstack.setFrameSlot(destinationSlots[index], copiedValues[index])
            }
        }
        nextIp
    }
}
