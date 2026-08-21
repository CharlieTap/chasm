package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.executor.instantiator.allocation.tag.TagAllocator
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.ext.definedType

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

    val definedType = type.functionType.definedType()
    val rtt = store.store.heap.registerRuntimeType(definedType)

    return Tag(
        reference = ExternalValue.Tag(allocator(store.store, rtt, type)),
        store = store.store,
    )
}
