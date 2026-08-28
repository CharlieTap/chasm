package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32BitFieldExtractDispatcher(
    instruction: NumericInstruction.I32BitFieldExtractS,
): DispatchableInstruction {
    val operandSlot = instruction.operandSlot
    val shift = instruction.shift
    val mask = instruction.mask
    val destinationSlot = instruction.destinationSlot
    return DispatchableInstruction { vstack, _, nextIp ->
        val value = (vstack.getFrameSlot(operandSlot).toInt() ushr shift) and mask
        vstack.setFrameSlot(destinationSlot, value.toLong())
        nextIp
    }
}
