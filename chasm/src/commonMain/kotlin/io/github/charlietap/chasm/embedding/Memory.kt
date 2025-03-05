package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.instantiator.allocation.memory.MemoryAllocator
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.type.MemoryType

fun memory(
    store: Store,
    type: MemoryType,
): Memory = memory(
    store = store,
    type = type,
    allocator = ::MemoryAllocator,
)

internal fun memory(
    store: Store,
    type: MemoryType,
    allocator: MemoryAllocator,
): Memory {
    return Memory(ExternalValue.Memory(allocator(store.store, type)))
}
