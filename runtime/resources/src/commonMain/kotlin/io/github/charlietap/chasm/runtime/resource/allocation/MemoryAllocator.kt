package io.github.charlietap.chasm.runtime.resource.allocation

import io.github.charlietap.chasm.config.LinearMemoryConfig
import io.github.charlietap.chasm.memory.factory.LinearMemoryFactory
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.MemoryType

typealias MemoryAllocator = (
    Store,
    MemoryType,
    LinearMemoryConfig,
) -> Address.Memory

fun MemoryAllocator(
    store: Store,
    type: MemoryType,
    config: LinearMemoryConfig = LinearMemoryConfig(),
): Address.Memory = MemoryAllocator(
    store = store,
    type = type,
    config = config,
    memoryFactory = ::LinearMemoryFactory,
)

internal inline fun MemoryAllocator(
    store: Store,
    type: MemoryType,
    config: LinearMemoryConfig,
    crossinline memoryFactory: LinearMemoryFactory,
): Address.Memory {
    val pages = LinearMemory.Pages(type.limits.min.toUInt())
    val limit = store.config.limits.memoryPages
    // A memory never starts above its maximum; the instantiator rejects
    // modules whose memories exceed the limit before reaching here.
    val effectiveMaximum = maxOf(
        type.limits.min.toInt(),
        minOf(MemoryInstance.declaredMaximumPages(type), limit ?: Int.MAX_VALUE),
    )
    val maximumPages = if (type.limits.max == null && limit == null) {
        null
    } else {
        LinearMemory.Pages(effectiveMaximum.toUInt())
    }
    val memory = memoryFactory(pages, maximumPages, config)
    val instance = MemoryInstance(type, memory, effectiveMaximum)

    store.memories.add(instance)

    return Address.Memory(store.memories.size - 1)
}
