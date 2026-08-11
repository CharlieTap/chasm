package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction

fun I32ConstDispatcher(instruction: NumericSuperInstruction.I32ConstS): DispatchableInstruction {
    val value = instruction.value.toLong()
    val destinationSlot = instruction.destinationSlot
    return DispatchableInstruction { vstack, _, _, _, nextIp ->
        vstack.setFrameSlot(destinationSlot, value)
        nextIp
    }
}

fun I64ConstDispatcher(instruction: NumericSuperInstruction.I64ConstS): DispatchableInstruction {
    val value = instruction.value
    val destinationSlot = instruction.destinationSlot
    return DispatchableInstruction { vstack, _, _, _, nextIp ->
        vstack.setFrameSlot(destinationSlot, value)
        nextIp
    }
}

fun F32ConstDispatcher(instruction: NumericSuperInstruction.F32ConstS): DispatchableInstruction {
    val bits = instruction.bits.toLong()
    val destinationSlot = instruction.destinationSlot
    return DispatchableInstruction { vstack, _, _, _, nextIp ->
        vstack.setFrameSlot(destinationSlot, bits)
        nextIp
    }
}

fun F64ConstDispatcher(instruction: NumericSuperInstruction.F64ConstS): DispatchableInstruction {
    val bits = instruction.bits
    val destinationSlot = instruction.destinationSlot
    return DispatchableInstruction { vstack, _, _, _, nextIp ->
        vstack.setFrameSlot(destinationSlot, bits)
        nextIp
    }
}
