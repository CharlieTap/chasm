package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.instruction.admin.CopySlotsInstructionExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun CopySlotDispatcher(
    sourceSlot: Int,
    destinationSlot: Int,
): DispatchableInstruction = DispatchableInstruction { vstack, _, _, _, nextIp ->
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

    return DispatchableInstruction { vstack, _, _, _, nextIp ->
        for (index in sourceSlots.indices) {
            vstack.setFrameSlot(destinationSlots[index], vstack.getFrameSlot(sourceSlots[index]))
        }
        nextIp
    }
}

fun CopySlotsDispatcher(
    instruction: AdminInstruction.CopySlots,
) = CopySlotsDispatcher(
    instruction = instruction,
    executor = ::CopySlotsInstructionExecutor,
)

internal inline fun CopySlotsDispatcher(
    instruction: AdminInstruction.CopySlots,
    crossinline executor: Executor<AdminInstruction.CopySlots>,
): DispatchableInstruction {
    if (instruction.sourceSlots.size == 1) {
        val sourceSlot = instruction.sourceSlots[0]
        val destinationSlot = instruction.destinationSlots[0]
        return CopySlotDispatcher(sourceSlot, destinationSlot)
    }

    return DispatchableInstruction { vstack, cstack, store, context, nextIp ->
        executor(vstack, cstack, store, context, instruction)
        nextIp
    }
}
