package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.executor.instantiator.allocation.memory.MemoryAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.memory.MemoryAllocatorImpl
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store

fun memory(
    store: Store,
    type: MemoryType,
): ExternalValue.Memory = memory(
    store = store,
    type = type,
    allocator = ::MemoryAllocatorImpl,
)

internal fun memory(
    store: Store,
    type: MemoryType,
    allocator: MemoryAllocator,
): ExternalValue.Memory = ExternalValue.Memory(allocator(store, type))
