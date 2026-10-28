package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction

fun I32BitFieldExtractSDispatcher(
    operandSlot: Int,
    shift: Int,
    mask: Int,
    destinationSlot: Int,
): DispatchableInstruction {
    return DispatchableInstruction { vstack, _, nextIp ->
        val value = (vstack.getFrameSlot(operandSlot).toInt() ushr shift) and mask
        vstack.setFrameSlot(destinationSlot, value.toLong())
        nextIp
    }
}
