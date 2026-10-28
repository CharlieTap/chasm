package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.executeMemoryLoad
import io.github.charlietap.chasm.memory.read.F32Reader
import io.github.charlietap.chasm.memory.read.F64Reader
import io.github.charlietap.chasm.memory.read.I3216SReader
import io.github.charlietap.chasm.memory.read.I3216UReader
import io.github.charlietap.chasm.memory.read.I328SReader
import io.github.charlietap.chasm.memory.read.I328UReader
import io.github.charlietap.chasm.memory.read.I32Reader
import io.github.charlietap.chasm.memory.read.I6416SReader
import io.github.charlietap.chasm.memory.read.I6416UReader
import io.github.charlietap.chasm.memory.read.I6432SReader
import io.github.charlietap.chasm.memory.read.I6432UReader
import io.github.charlietap.chasm.memory.read.I648SReader
import io.github.charlietap.chasm.memory.read.I648UReader
import io.github.charlietap.chasm.memory.read.I64Reader
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.operations.effectiveAddress

fun I32LoadIDispatcher(
    address: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = if (offset == 0) {
    memoryLoadNoOffsetDispatcher(memory, destinationSlot, Int.SIZE_BYTES, { address }) { data, effectiveAddress ->
        I32Reader(data, effectiveAddress).toLong()
    }
} else {
    dispatchInstruction { vstack, context ->
        executeMemoryLoad(vstack, memory, address, offset, destinationSlot, Int.SIZE_BYTES) { effectiveAddress ->
            I32Reader(memory.data, effectiveAddress).toLong()
        }
    }
}

fun I32LoadSDispatcher(
    addressSlot: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = if (offset == 0) {
    memoryLoadNoOffsetDispatcher(memory, destinationSlot, Int.SIZE_BYTES, { vstack ->
        vstack.getFrameSlot(addressSlot).toInt()
    }) { data, address ->
        I32Reader(data, address).toLong()
    }
} else {
    dispatchInstruction { vstack, context ->
        executeMemoryLoad(vstack, memory, vstack.getFrameSlot(addressSlot).toInt(), offset, destinationSlot, Int.SIZE_BYTES) { effectiveAddress ->
            I32Reader(memory.data, effectiveAddress).toLong()
        }
    }
}

fun I64LoadIDispatcher(
    address: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = if (offset == 0) {
    memoryLoadNoOffsetDispatcher(memory, destinationSlot, Long.SIZE_BYTES, { address }) { data, effectiveAddress ->
        I64Reader(data, effectiveAddress)
    }
} else {
    dispatchInstruction { vstack, context ->
        executeMemoryLoad(vstack, memory, address, offset, destinationSlot, Long.SIZE_BYTES) { effectiveAddress ->
            I64Reader(memory.data, effectiveAddress)
        }
    }
}

fun I64LoadSDispatcher(
    addressSlot: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, vstack.getFrameSlot(addressSlot).toInt(), offset, destinationSlot, Long.SIZE_BYTES) { effectiveAddress ->
        I64Reader(memory.data, effectiveAddress)
    }
}

fun F32LoadIDispatcher(
    address: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, address, offset, destinationSlot, Float.SIZE_BYTES) { effectiveAddress ->
        F32Reader(memory.data, effectiveAddress).toRawBits().toLong()
    }
}

fun F32LoadSDispatcher(
    addressSlot: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, vstack.getFrameSlot(addressSlot).toInt(), offset, destinationSlot, Float.SIZE_BYTES) { effectiveAddress ->
        F32Reader(memory.data, effectiveAddress).toRawBits().toLong()
    }
}

fun F64LoadIDispatcher(
    address: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, address, offset, destinationSlot, Double.SIZE_BYTES) { effectiveAddress ->
        F64Reader(memory.data, effectiveAddress).toRawBits()
    }
}

fun F64LoadSDispatcher(
    addressSlot: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, vstack.getFrameSlot(addressSlot).toInt(), offset, destinationSlot, Double.SIZE_BYTES) { effectiveAddress ->
        F64Reader(memory.data, effectiveAddress).toRawBits()
    }
}

fun I32Load8SIDispatcher(
    address: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, address, offset, destinationSlot, Byte.SIZE_BYTES) { effectiveAddress ->
        I328SReader(memory.data, effectiveAddress).toLong()
    }
}

fun I32Load8SSDispatcher(
    addressSlot: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, vstack.getFrameSlot(addressSlot).toInt(), offset, destinationSlot, Byte.SIZE_BYTES) { effectiveAddress ->
        I328SReader(memory.data, effectiveAddress).toLong()
    }
}

fun I32Load8UIDispatcher(
    address: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, address, offset, destinationSlot, Byte.SIZE_BYTES) { effectiveAddress ->
        I328UReader(memory.data, effectiveAddress).toLong()
    }
}

fun I32Load8USDispatcher(
    addressSlot: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = if (offset == 0) {
    memoryLoadNoOffsetDispatcher(memory, destinationSlot, Byte.SIZE_BYTES, { vstack ->
        vstack.getFrameSlot(addressSlot).toInt()
    }) { data, address ->
        I328UReader(data, address).toLong()
    }
} else {
    dispatchInstruction { vstack, context ->
        executeMemoryLoad(vstack, memory, vstack.getFrameSlot(addressSlot).toInt(), offset, destinationSlot, Byte.SIZE_BYTES) { effectiveAddress ->
            I328UReader(memory.data, effectiveAddress).toLong()
        }
    }
}

fun I32Load16SIDispatcher(
    address: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, address, offset, destinationSlot, Short.SIZE_BYTES) { effectiveAddress ->
        I3216SReader(memory.data, effectiveAddress).toLong()
    }
}

fun I32Load16SSDispatcher(
    addressSlot: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = if (offset == 0) {
    memoryLoadNoOffsetDispatcher(memory, destinationSlot, Short.SIZE_BYTES, { vstack ->
        vstack.getFrameSlot(addressSlot).toInt()
    }) { data, address ->
        I3216SReader(data, address).toLong()
    }
} else {
    dispatchInstruction { vstack, context ->
        executeMemoryLoad(vstack, memory, vstack.getFrameSlot(addressSlot).toInt(), offset, destinationSlot, Short.SIZE_BYTES) { effectiveAddress ->
            I3216SReader(memory.data, effectiveAddress).toLong()
        }
    }
}

fun I32Load16UIDispatcher(
    address: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, address, offset, destinationSlot, Short.SIZE_BYTES) { effectiveAddress ->
        I3216UReader(memory.data, effectiveAddress).toLong()
    }
}

fun I32Load16USDispatcher(
    addressSlot: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = if (offset == 0) {
    memoryLoadNoOffsetDispatcher(memory, destinationSlot, Short.SIZE_BYTES, { vstack ->
        vstack.getFrameSlot(addressSlot).toInt()
    }) { data, address ->
        I3216UReader(data, address).toLong()
    }
} else {
    dispatchInstruction { vstack, context ->
        executeMemoryLoad(vstack, memory, vstack.getFrameSlot(addressSlot).toInt(), offset, destinationSlot, Short.SIZE_BYTES) { effectiveAddress ->
            I3216UReader(memory.data, effectiveAddress).toLong()
        }
    }
}

fun I64Load8SIDispatcher(
    address: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, address, offset, destinationSlot, Byte.SIZE_BYTES) { effectiveAddress ->
        I648SReader(memory.data, effectiveAddress)
    }
}

fun I64Load8SSDispatcher(
    addressSlot: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, vstack.getFrameSlot(addressSlot).toInt(), offset, destinationSlot, Byte.SIZE_BYTES) { effectiveAddress ->
        I648SReader(memory.data, effectiveAddress)
    }
}

fun I64Load8UIDispatcher(
    address: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, address, offset, destinationSlot, Byte.SIZE_BYTES) { effectiveAddress ->
        I648UReader(memory.data, effectiveAddress)
    }
}

fun I64Load8USDispatcher(
    addressSlot: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, vstack.getFrameSlot(addressSlot).toInt(), offset, destinationSlot, Byte.SIZE_BYTES) { effectiveAddress ->
        I648UReader(memory.data, effectiveAddress)
    }
}

fun I64Load16SIDispatcher(
    address: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, address, offset, destinationSlot, Short.SIZE_BYTES) { effectiveAddress ->
        I6416SReader(memory.data, effectiveAddress)
    }
}

fun I64Load16SSDispatcher(
    addressSlot: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, vstack.getFrameSlot(addressSlot).toInt(), offset, destinationSlot, Short.SIZE_BYTES) { effectiveAddress ->
        I6416SReader(memory.data, effectiveAddress)
    }
}

fun I64Load16UIDispatcher(
    address: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, address, offset, destinationSlot, Short.SIZE_BYTES) { effectiveAddress ->
        I6416UReader(memory.data, effectiveAddress)
    }
}

fun I64Load16USDispatcher(
    addressSlot: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, vstack.getFrameSlot(addressSlot).toInt(), offset, destinationSlot, Short.SIZE_BYTES) { effectiveAddress ->
        I6416UReader(memory.data, effectiveAddress)
    }
}

fun I64Load32SIDispatcher(
    address: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, address, offset, destinationSlot, Int.SIZE_BYTES) { effectiveAddress ->
        I6432SReader(memory.data, effectiveAddress)
    }
}

fun I64Load32SSDispatcher(
    addressSlot: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, vstack.getFrameSlot(addressSlot).toInt(), offset, destinationSlot, Int.SIZE_BYTES) { effectiveAddress ->
        I6432SReader(memory.data, effectiveAddress)
    }
}

fun I64Load32UIDispatcher(
    address: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, address, offset, destinationSlot, Int.SIZE_BYTES) { effectiveAddress ->
        I6432UReader(memory.data, effectiveAddress)
    }
}

fun I64Load32USDispatcher(
    addressSlot: Int,
    destinationSlot: Int,
    memory: MemoryInstance,
    offset: Int,
) = dispatchInstruction { vstack, context ->
    executeMemoryLoad(vstack, memory, vstack.getFrameSlot(addressSlot).toInt(), offset, destinationSlot, Int.SIZE_BYTES) { effectiveAddress ->
        I6432UReader(memory.data, effectiveAddress)
    }
}

fun MemorySizeSDispatcher(
    destinationSlot: Int,
    memory: MemoryInstance,
) = dispatchInstruction { vstack, context ->
    run {
        vstack.setFrameSlot(destinationSlot, memory.type.limits.min.toLong())
    }
}
