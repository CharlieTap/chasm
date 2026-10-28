package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.executeMemoryStore
import io.github.charlietap.chasm.memory.write.F32Writer
import io.github.charlietap.chasm.memory.write.F64Writer
import io.github.charlietap.chasm.memory.write.I32ToI16Writer
import io.github.charlietap.chasm.memory.write.I32ToI8Writer
import io.github.charlietap.chasm.memory.write.I32Writer
import io.github.charlietap.chasm.memory.write.I64ToI16Writer
import io.github.charlietap.chasm.memory.write.I64ToI32Writer
import io.github.charlietap.chasm.memory.write.I64ToI8Writer
import io.github.charlietap.chasm.memory.write.I64Writer
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.operations.effectiveAddress

fun I32StoreIiDispatcher(
    value: Int,
    address: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, address, offset, Int.SIZE_BYTES) { effectiveAddress ->
        I32Writer(memory.data, effectiveAddress, value)
    }
}

fun I32StoreIsDispatcher(
    value: Int,
    addressSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = if (offset == 0) {

    memoryStoreNoOffsetDispatcher(
        memory,
        Int.SIZE_BYTES,
        { vstack ->
            vstack.getFrameSlot(addressSlot).toInt()
        },
    ) { _, data, address ->
        I32Writer(data, address, value)
    }
} else {
    dispatchInstruction { vstack, context ->
        executeMemoryStore(memory, vstack.getFrameSlot(addressSlot).toInt(), offset, Int.SIZE_BYTES) { effectiveAddress ->
            I32Writer(memory.data, effectiveAddress, value)
        }
    }
}

fun I32StoreSiDispatcher(
    valueSlot: Int,
    address: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, address, offset, Int.SIZE_BYTES) { effectiveAddress ->
        I32Writer(memory.data, effectiveAddress, vstack.getFrameSlot(valueSlot).toInt())
    }
}

fun I32StoreSsDispatcher(
    valueSlot: Int,
    addressSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = if (offset == 0) {
    memoryStoreNoOffsetDispatcher(
        memory,
        Int.SIZE_BYTES,
        { vstack ->
            vstack.getFrameSlot(addressSlot).toInt()
        },
    ) { vstack, data, address ->
        I32Writer(data, address, vstack.getFrameSlot(valueSlot).toInt())
    }
} else {
    dispatchInstruction { vstack, context ->
        executeMemoryStore(memory, vstack.getFrameSlot(addressSlot).toInt(), offset, Int.SIZE_BYTES) { effectiveAddress ->
            I32Writer(memory.data, effectiveAddress, vstack.getFrameSlot(valueSlot).toInt())
        }
    }
}

fun I64StoreIiDispatcher(
    value: Long,
    address: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, address, offset, Long.SIZE_BYTES) { effectiveAddress ->
        I64Writer(memory.data, effectiveAddress, value)
    }
}

fun I64StoreIsDispatcher(
    value: Long,
    addressSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = if (offset == 0) {
    memoryStoreNoOffsetDispatcher(
        memory,
        Long.SIZE_BYTES,
        { vstack ->
            vstack.getFrameSlot(addressSlot).toInt()
        },
    ) { _, data, address ->
        I64Writer(data, address, value)
    }
} else {
    dispatchInstruction { vstack, context ->
        executeMemoryStore(memory, vstack.getFrameSlot(addressSlot).toInt(), offset, Long.SIZE_BYTES) { effectiveAddress ->
            I64Writer(memory.data, effectiveAddress, value)
        }
    }
}

fun I64StoreSiDispatcher(
    valueSlot: Int,
    address: Int,
    memory: MemoryInstance,
    offset: Int,
) = if (offset == 0) {
    memoryStoreNoOffsetDispatcher(memory, Long.SIZE_BYTES, { address }) { vstack, data, effectiveAddress ->
        I64Writer(data, effectiveAddress, vstack.getFrameSlot(valueSlot))
    }
} else {
    dispatchInstruction { vstack, context ->
        executeMemoryStore(memory, address, offset, Long.SIZE_BYTES) { effectiveAddress ->
            I64Writer(memory.data, effectiveAddress, vstack.getFrameSlot(valueSlot))
        }
    }
}

fun I64StoreSsDispatcher(
    valueSlot: Int,
    addressSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, vstack.getFrameSlot(addressSlot).toInt(), offset, Long.SIZE_BYTES) { effectiveAddress ->
        I64Writer(memory.data, effectiveAddress, vstack.getFrameSlot(valueSlot))
    }
}

fun F32StoreIiDispatcher(
    value: Float,
    address: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, address, offset, Float.SIZE_BYTES) { effectiveAddress ->
        F32Writer(memory.data, effectiveAddress, value)
    }
}

fun F32StoreIsDispatcher(
    value: Float,
    addressSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, vstack.getFrameSlot(addressSlot).toInt(), offset, Float.SIZE_BYTES) { effectiveAddress ->
        F32Writer(memory.data, effectiveAddress, value)
    }
}

fun F32StoreSiDispatcher(
    valueSlot: Int,
    address: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, address, offset, Float.SIZE_BYTES) { effectiveAddress ->
        F32Writer(memory.data, effectiveAddress, Float.fromBits(vstack.getFrameSlot(valueSlot).toInt()))
    }
}

fun F32StoreSsDispatcher(
    valueSlot: Int,
    addressSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, vstack.getFrameSlot(addressSlot).toInt(), offset, Float.SIZE_BYTES) { effectiveAddress ->
        F32Writer(memory.data, effectiveAddress, Float.fromBits(vstack.getFrameSlot(valueSlot).toInt()))
    }
}

fun F64StoreIiDispatcher(
    value: Double,
    address: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, address, offset, Double.SIZE_BYTES) { effectiveAddress ->
        F64Writer(memory.data, effectiveAddress, value)
    }
}

fun F64StoreIsDispatcher(
    value: Double,
    addressSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, vstack.getFrameSlot(addressSlot).toInt(), offset, Double.SIZE_BYTES) { effectiveAddress ->
        F64Writer(memory.data, effectiveAddress, value)
    }
}

fun F64StoreSiDispatcher(
    valueSlot: Int,
    address: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, address, offset, Double.SIZE_BYTES) { effectiveAddress ->
        F64Writer(memory.data, effectiveAddress, Double.fromBits(vstack.getFrameSlot(valueSlot)))
    }
}

fun F64StoreSsDispatcher(
    valueSlot: Int,
    addressSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, vstack.getFrameSlot(addressSlot).toInt(), offset, Double.SIZE_BYTES) { effectiveAddress ->
        F64Writer(memory.data, effectiveAddress, Double.fromBits(vstack.getFrameSlot(valueSlot)))
    }
}

fun I32Store8IiDispatcher(
    value: Int,
    address: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, address, offset, Byte.SIZE_BYTES) { effectiveAddress ->
        I32ToI8Writer(memory.data, effectiveAddress, value)
    }
}

fun I32Store8IsDispatcher(
    value: Int,
    addressSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = if (offset == 0) {
    memoryStoreNoOffsetDispatcher(
        memory,
        Byte.SIZE_BYTES,
        { vstack ->
            vstack.getFrameSlot(addressSlot).toInt()
        },
    ) { _, data, address ->
        I32ToI8Writer(data, address, value)
    }
} else {
    dispatchInstruction { vstack, context ->
        executeMemoryStore(memory, vstack.getFrameSlot(addressSlot).toInt(), offset, Byte.SIZE_BYTES) { effectiveAddress ->
            I32ToI8Writer(memory.data, effectiveAddress, value)
        }
    }
}

fun I32Store8SiDispatcher(
    valueSlot: Int,
    address: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, address, offset, Byte.SIZE_BYTES) { effectiveAddress ->
        I32ToI8Writer(memory.data, effectiveAddress, vstack.getFrameSlot(valueSlot).toInt())
    }
}

fun I32Store8SsDispatcher(
    valueSlot: Int,
    addressSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = if (offset == 0) {
    memoryStoreNoOffsetDispatcher(
        memory,
        Byte.SIZE_BYTES,
        { vstack ->
            vstack.getFrameSlot(addressSlot).toInt()
        },
    ) { vstack, data, address ->
        I32ToI8Writer(data, address, vstack.getFrameSlot(valueSlot).toInt())
    }
} else {
    dispatchInstruction { vstack, context ->
        executeMemoryStore(memory, vstack.getFrameSlot(addressSlot).toInt(), offset, Byte.SIZE_BYTES) { effectiveAddress ->
            I32ToI8Writer(memory.data, effectiveAddress, vstack.getFrameSlot(valueSlot).toInt())
        }
    }
}

fun I32Store16IiDispatcher(
    value: Int,
    address: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, address, offset, Short.SIZE_BYTES) { effectiveAddress ->
        I32ToI16Writer(memory.data, effectiveAddress, value)
    }
}

fun I32Store16IsDispatcher(
    value: Int,
    addressSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = if (offset == 0) {
    memoryStoreNoOffsetDispatcher(
        memory,
        Short.SIZE_BYTES,
        { vstack ->
            vstack.getFrameSlot(addressSlot).toInt()
        },
    ) { _, data, address ->
        I32ToI16Writer(data, address, value)
    }
} else {
    dispatchInstruction { vstack, context ->
        executeMemoryStore(memory, vstack.getFrameSlot(addressSlot).toInt(), offset, Short.SIZE_BYTES) { effectiveAddress ->
            I32ToI16Writer(memory.data, effectiveAddress, value)
        }
    }
}

fun I32Store16SiDispatcher(
    valueSlot: Int,
    address: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, address, offset, Short.SIZE_BYTES) { effectiveAddress ->
        I32ToI16Writer(memory.data, effectiveAddress, vstack.getFrameSlot(valueSlot).toInt())
    }
}

fun I32Store16SsDispatcher(
    valueSlot: Int,
    addressSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = if (offset == 0) {
    memoryStoreNoOffsetDispatcher(
        memory,
        Short.SIZE_BYTES,
        { vstack ->
            vstack.getFrameSlot(addressSlot).toInt()
        },
    ) { vstack, data, address ->
        I32ToI16Writer(data, address, vstack.getFrameSlot(valueSlot).toInt())
    }
} else {
    dispatchInstruction { vstack, context ->
        executeMemoryStore(memory, vstack.getFrameSlot(addressSlot).toInt(), offset, Short.SIZE_BYTES) { effectiveAddress ->
            I32ToI16Writer(memory.data, effectiveAddress, vstack.getFrameSlot(valueSlot).toInt())
        }
    }
}

fun I64Store8IiDispatcher(
    value: Long,
    address: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, address, offset, Byte.SIZE_BYTES) { effectiveAddress ->
        I64ToI8Writer(memory.data, effectiveAddress, value)
    }
}

fun I64Store8IsDispatcher(
    value: Long,
    addressSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, vstack.getFrameSlot(addressSlot).toInt(), offset, Byte.SIZE_BYTES) { effectiveAddress ->
        I64ToI8Writer(memory.data, effectiveAddress, value)
    }
}

fun I64Store8SiDispatcher(
    valueSlot: Int,
    address: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, address, offset, Byte.SIZE_BYTES) { effectiveAddress ->
        I64ToI8Writer(memory.data, effectiveAddress, vstack.getFrameSlot(valueSlot))
    }
}

fun I64Store8SsDispatcher(
    valueSlot: Int,
    addressSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, vstack.getFrameSlot(addressSlot).toInt(), offset, Byte.SIZE_BYTES) { effectiveAddress ->
        I64ToI8Writer(memory.data, effectiveAddress, vstack.getFrameSlot(valueSlot))
    }
}

fun I64Store16IiDispatcher(
    value: Long,
    address: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, address, offset, Short.SIZE_BYTES) { effectiveAddress ->
        I64ToI16Writer(memory.data, effectiveAddress, value)
    }
}

fun I64Store16IsDispatcher(
    value: Long,
    addressSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, vstack.getFrameSlot(addressSlot).toInt(), offset, Short.SIZE_BYTES) { effectiveAddress ->
        I64ToI16Writer(memory.data, effectiveAddress, value)
    }
}

fun I64Store16SiDispatcher(
    valueSlot: Int,
    address: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, address, offset, Short.SIZE_BYTES) { effectiveAddress ->
        I64ToI16Writer(memory.data, effectiveAddress, vstack.getFrameSlot(valueSlot))
    }
}

fun I64Store16SsDispatcher(
    valueSlot: Int,
    addressSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, vstack.getFrameSlot(addressSlot).toInt(), offset, Short.SIZE_BYTES) { effectiveAddress ->
        I64ToI16Writer(memory.data, effectiveAddress, vstack.getFrameSlot(valueSlot))
    }
}

fun I64Store32IiDispatcher(
    value: Long,
    address: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, address, offset, Int.SIZE_BYTES) { effectiveAddress ->
        I64ToI32Writer(memory.data, effectiveAddress, value)
    }
}

fun I64Store32IsDispatcher(
    value: Long,
    addressSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = if (offset == 0) {
    memoryStoreNoOffsetDispatcher(
        memory,
        Int.SIZE_BYTES,
        { vstack ->
            vstack.getFrameSlot(addressSlot).toInt()
        },
    ) { _, data, address ->
        I64ToI32Writer(data, address, value)
    }
} else {
    dispatchInstruction { vstack, context ->
        executeMemoryStore(memory, vstack.getFrameSlot(addressSlot).toInt(), offset, Int.SIZE_BYTES) { effectiveAddress ->
            I64ToI32Writer(memory.data, effectiveAddress, value)
        }
    }
}

fun I64Store32SiDispatcher(
    valueSlot: Int,
    address: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, address, offset, Int.SIZE_BYTES) { effectiveAddress ->
        I64ToI32Writer(memory.data, effectiveAddress, vstack.getFrameSlot(valueSlot))
    }
}

fun I64Store32SsDispatcher(
    valueSlot: Int,
    addressSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryStore(memory, vstack.getFrameSlot(addressSlot).toInt(), offset, Int.SIZE_BYTES) { effectiveAddress ->
        I64ToI32Writer(memory.data, effectiveAddress, vstack.getFrameSlot(valueSlot))
    }
}
