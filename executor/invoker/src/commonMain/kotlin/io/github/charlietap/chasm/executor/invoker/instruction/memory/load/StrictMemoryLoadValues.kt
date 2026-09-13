package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

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

internal inline fun valueI32Load(memory: MemoryInstance, address: Int, offset: Int): Long = memoryLoadValue(memory, address, offset, Int.SIZE_BYTES) { effectiveAddress ->
    I32Reader(memory.data, effectiveAddress).toLong()
}

internal inline fun valueI64Load(memory: MemoryInstance, address: Int, offset: Int): Long = memoryLoadValue(memory, address, offset, Long.SIZE_BYTES) { effectiveAddress ->
    I64Reader(memory.data, effectiveAddress)
}

internal inline fun valueF32Load(memory: MemoryInstance, address: Int, offset: Int): Long = memoryLoadValue(memory, address, offset, Float.SIZE_BYTES) { effectiveAddress ->
    F32Reader(memory.data, effectiveAddress).toRawBits().toLong()
}

internal inline fun valueF64Load(memory: MemoryInstance, address: Int, offset: Int): Long = memoryLoadValue(memory, address, offset, Double.SIZE_BYTES) { effectiveAddress ->
    F64Reader(memory.data, effectiveAddress).toRawBits()
}

internal inline fun valueI32Load8S(memory: MemoryInstance, address: Int, offset: Int): Long = memoryLoadValue(memory, address, offset, Byte.SIZE_BYTES) { effectiveAddress ->
    I328SReader(memory.data, effectiveAddress).toLong()
}

internal inline fun valueI32Load8U(memory: MemoryInstance, address: Int, offset: Int): Long = memoryLoadValue(memory, address, offset, Byte.SIZE_BYTES) { effectiveAddress ->
    I328UReader(memory.data, effectiveAddress).toLong()
}

internal inline fun valueI32Load16S(memory: MemoryInstance, address: Int, offset: Int): Long = memoryLoadValue(memory, address, offset, Short.SIZE_BYTES) { effectiveAddress ->
    I3216SReader(memory.data, effectiveAddress).toLong()
}

internal inline fun valueI32Load16U(memory: MemoryInstance, address: Int, offset: Int): Long = memoryLoadValue(memory, address, offset, Short.SIZE_BYTES) { effectiveAddress ->
    I3216UReader(memory.data, effectiveAddress).toLong()
}

internal inline fun valueI64Load8S(memory: MemoryInstance, address: Int, offset: Int): Long = memoryLoadValue(memory, address, offset, Byte.SIZE_BYTES) { effectiveAddress ->
    I648SReader(memory.data, effectiveAddress)
}

internal inline fun valueI64Load8U(memory: MemoryInstance, address: Int, offset: Int): Long = memoryLoadValue(memory, address, offset, Byte.SIZE_BYTES) { effectiveAddress ->
    I648UReader(memory.data, effectiveAddress)
}

internal inline fun valueI64Load16S(memory: MemoryInstance, address: Int, offset: Int): Long = memoryLoadValue(memory, address, offset, Short.SIZE_BYTES) { effectiveAddress ->
    I6416SReader(memory.data, effectiveAddress)
}

internal inline fun valueI64Load16U(memory: MemoryInstance, address: Int, offset: Int): Long = memoryLoadValue(memory, address, offset, Short.SIZE_BYTES) { effectiveAddress ->
    I6416UReader(memory.data, effectiveAddress)
}

internal inline fun valueI64Load32S(memory: MemoryInstance, address: Int, offset: Int): Long = memoryLoadValue(memory, address, offset, Int.SIZE_BYTES) { effectiveAddress ->
    I6432SReader(memory.data, effectiveAddress)
}

internal inline fun valueI64Load32U(memory: MemoryInstance, address: Int, offset: Int): Long = memoryLoadValue(memory, address, offset, Int.SIZE_BYTES) { effectiveAddress ->
    I6432UReader(memory.data, effectiveAddress)
}
