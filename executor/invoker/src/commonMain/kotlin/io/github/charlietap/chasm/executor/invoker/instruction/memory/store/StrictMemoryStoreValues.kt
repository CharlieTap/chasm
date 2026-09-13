package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

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

internal inline fun valueI32Store(memory: MemoryInstance, address: Int, offset: Int, value: Int): Unit = memoryStoreValue(memory, address, offset, Int.SIZE_BYTES) { effectiveAddress ->
    I32Writer(memory.data, effectiveAddress, value)
}

internal inline fun valueI64Store(memory: MemoryInstance, address: Int, offset: Int, value: Long): Unit = memoryStoreValue(memory, address, offset, Long.SIZE_BYTES) { effectiveAddress ->
    I64Writer(memory.data, effectiveAddress, value)
}

internal inline fun valueF32Store(memory: MemoryInstance, address: Int, offset: Int, value: Float): Unit = memoryStoreValue(memory, address, offset, Float.SIZE_BYTES) { effectiveAddress ->
    F32Writer(memory.data, effectiveAddress, value)
}

internal inline fun valueF64Store(memory: MemoryInstance, address: Int, offset: Int, value: Double): Unit = memoryStoreValue(memory, address, offset, Double.SIZE_BYTES) { effectiveAddress ->
    F64Writer(memory.data, effectiveAddress, value)
}

internal inline fun valueI32Store8(memory: MemoryInstance, address: Int, offset: Int, value: Int): Unit = memoryStoreValue(memory, address, offset, Byte.SIZE_BYTES) { effectiveAddress ->
    I32ToI8Writer(memory.data, effectiveAddress, value)
}

internal inline fun valueI32Store16(memory: MemoryInstance, address: Int, offset: Int, value: Int): Unit = memoryStoreValue(memory, address, offset, Short.SIZE_BYTES) { effectiveAddress ->
    I32ToI16Writer(memory.data, effectiveAddress, value)
}

internal inline fun valueI64Store8(memory: MemoryInstance, address: Int, offset: Int, value: Long): Unit = memoryStoreValue(memory, address, offset, Byte.SIZE_BYTES) { effectiveAddress ->
    I64ToI8Writer(memory.data, effectiveAddress, value)
}

internal inline fun valueI64Store16(memory: MemoryInstance, address: Int, offset: Int, value: Long): Unit = memoryStoreValue(memory, address, offset, Short.SIZE_BYTES) { effectiveAddress ->
    I64ToI16Writer(memory.data, effectiveAddress, value)
}

internal inline fun valueI64Store32(memory: MemoryInstance, address: Int, offset: Int, value: Long): Unit = memoryStoreValue(memory, address, offset, Int.SIZE_BYTES) { effectiveAddress ->
    I64ToI32Writer(memory.data, effectiveAddress, value)
}
