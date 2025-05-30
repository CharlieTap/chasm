package io.github.charlietap.chasm.executor.instantiator.allocation.memory

import io.github.charlietap.chasm.memory.factory.LinearMemoryFactory
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.MemoryType

typealias MemoryAllocator = (Store, MemoryType) -> Address.Memory

fun MemoryAllocator(
    store: Store,
    type: MemoryType,
): Address.Memory = MemoryAllocator(
    store = store,
    type = type,
    memoryFactory = ::LinearMemoryFactory,
)

internal inline fun MemoryAllocator(
    store: Store,
    type: MemoryType,
    crossinline memoryFactory: LinearMemoryFactory,
): Address.Memory {
    val pages = LinearMemory.Pages(type.limits.min)
    val memory = memoryFactory(pages)
    val instance = MemoryInstance(type, memory)

    store.memories.add(instance)

    return Address.Memory(store.memories.size - 1)
}
