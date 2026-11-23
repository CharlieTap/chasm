package io.github.charlietap.chasm.runtime.resource.allocation

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
): Address.Memory {
    return MemoryAllocator(
        store = store,
        type = type,
        memoryFactory = ::LinearMemoryFactory,
    )
}

internal inline fun MemoryAllocator(
    store: Store,
    type: MemoryType,
    crossinline memoryFactory: LinearMemoryFactory,
): Address.Memory {
    val pages = LinearMemory.Pages(type.limits.min.toUInt())
    val limit = store.memoryPageLimit
    val effectiveMaximum = minOf(MemoryInstance.declaredMaximumPages(type), limit ?: Int.MAX_VALUE)
    val maximumPages = if (type.limits.max == null && limit == null) {
        null
    } else {
        LinearMemory.Pages(effectiveMaximum.toUInt())
    }
    val memory = memoryFactory(pages, maximumPages)
    val instance = MemoryInstance(type.copy(limits = type.limits.copy()), memory, effectiveMaximum)

    store.memories.add(instance)

    return Address.Memory(store.memories.size - 1)
}
