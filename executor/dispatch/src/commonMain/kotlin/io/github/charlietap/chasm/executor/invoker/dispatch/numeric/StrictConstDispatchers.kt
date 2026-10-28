package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32ConstDispatcher(instruction: NumericInstruction.I32ConstS): DispatchableInstruction {
    val value = instruction.value.toLong()
    val destinationSlot = instruction.destinationSlot
    return DispatchableInstruction { vstack, _, nextIp ->
        vstack.setFrameSlot(destinationSlot, value)
        nextIp
    }
}

fun I64ConstDispatcher(instruction: NumericInstruction.I64ConstS): DispatchableInstruction {
    val value = instruction.value
    val destinationSlot = instruction.destinationSlot
    return DispatchableInstruction { vstack, _, nextIp ->
        vstack.setFrameSlot(destinationSlot, value)
        nextIp
    }
}

fun F32ConstDispatcher(instruction: NumericInstruction.F32ConstS): DispatchableInstruction {
    val bits = instruction.bits.toLong()
    val destinationSlot = instruction.destinationSlot
    return DispatchableInstruction { vstack, _, nextIp ->
        vstack.setFrameSlot(destinationSlot, bits)
        nextIp
    }
}

fun F64ConstDispatcher(instruction: NumericInstruction.F64ConstS): DispatchableInstruction {
    val bits = instruction.bits
    val destinationSlot = instruction.destinationSlot
    return DispatchableInstruction { vstack, _, nextIp ->
        vstack.setFrameSlot(destinationSlot, bits)
        nextIp
    }
}
