package io.github.charlietap.chasm.executor.instantiator.allocation.memory

import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store

internal fun MemoryAllocatorImpl(
    store: Store,
    type: MemoryType,
): Address.Memory = MemoryAllocatorImpl(
    store = store,
    type = type,
    memoryFactory = ::ByteArrayLinearMemoryFactory,
)

internal fun MemoryAllocatorImpl(
    store: Store,
    type: MemoryType,
    memoryFactory: LinearMemoryFactory,
): Address.Memory {
    val memory = memoryFactory(type.limits.min.toInt(), type.limits.max?.toInt())
    val instance = MemoryInstance(type, memory)

    store.memories.add(instance)

    return Address.Memory(store.memories.size - 1)
}
