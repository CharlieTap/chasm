package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction

fun I32ConstSDispatcher(
    value: Int,
    destinationSlot: Int,
): DispatchableInstruction {
    val value = value.toLong()
    return DispatchableInstruction { vstack, _, nextIp ->
        vstack.setFrameSlot(destinationSlot, value)
        nextIp
    }
}

fun I64ConstSDispatcher(
    value: Long,
    destinationSlot: Int,
): DispatchableInstruction {
    return DispatchableInstruction { vstack, _, nextIp ->
        vstack.setFrameSlot(destinationSlot, value)
        nextIp
    }
}

fun F32ConstSDispatcher(
    bits: Int,
    destinationSlot: Int,
): DispatchableInstruction {
    val bits = bits.toLong()
    return DispatchableInstruction { vstack, _, nextIp ->
        vstack.setFrameSlot(destinationSlot, bits)
        nextIp
    }
}

fun F64ConstSDispatcher(
    bits: Long,
    destinationSlot: Int,
): DispatchableInstruction {
    return DispatchableInstruction { vstack, _, nextIp ->
        vstack.setFrameSlot(destinationSlot, bits)
        nextIp
    }
}
