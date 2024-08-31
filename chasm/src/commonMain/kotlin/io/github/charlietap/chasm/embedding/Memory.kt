package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.MemoryType
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.transform.Mapper
import io.github.charlietap.chasm.embedding.transform.MemoryTypeMapper
import io.github.charlietap.chasm.executor.instantiator.allocation.memory.MemoryAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.memory.MemoryAllocatorImpl
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.ast.type.MemoryType as InternalMemoryType

fun memory(
    store: Store,
    type: MemoryType,
): Memory = memory(
    store = store,
    type = type,
    allocator = ::MemoryAllocatorImpl,
    memoryTypeMapper = MemoryTypeMapper.instance,
)

internal fun memory(
    store: Store,
    type: MemoryType,
    allocator: MemoryAllocator,
    memoryTypeMapper: Mapper<MemoryType, InternalMemoryType>,
): Memory {
    val memoryType = memoryTypeMapper.map(type)
    return Memory(ExternalValue.Memory(allocator(store.store, memoryType)))
}
