package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.executor.instantiator.allocation.tag.TagAllocator
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.type.TagType

fun tag(
    store: Store,
    type: TagType,
): Tag = tag(
    store = store,
    type = type,
    allocator = ::TagAllocator,
)

internal fun tag(
    store: Store,
    type: TagType,
    allocator: TagAllocator,
): Tag {
    return Tag(ExternalValue.Tag(allocator(store.store, type)))
}
