package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.config.LinearMemoryConfig
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.instantiator.allocation.memory.MemoryAllocator
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.type.MemoryType

fun memory(
    store: Store,
    type: MemoryType,
): Memory = memory(store, type, LinearMemoryConfig())

fun memory(
    store: Store,
    type: MemoryType,
    config: LinearMemoryConfig,
): Memory = memory(
    store = store,
    type = type,
    config = config,
    allocator = ::MemoryAllocator,
)

internal fun memory(
    store: Store,
    type: MemoryType,
    config: LinearMemoryConfig = LinearMemoryConfig(),
    allocator: MemoryAllocator,
): Memory {
    return Memory(
        reference = ExternalValue.Memory(allocator(store.store, type, config)),
        store = store.store,
    )
}
