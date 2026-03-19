package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.executor.instantiator.allocation.tag.TagAllocator
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.ext.definedType
import io.github.charlietap.chasm.type.factory.RTTFactory

fun tag(
    store: Store,
    type: TagType,
): Tag = tag(
    store = store,
    type = type,
    allocator = ::TagAllocator,
    rttFactory = ::RTTFactory,
)

internal fun tag(
    store: Store,
    type: TagType,
    allocator: TagAllocator,
    rttFactory: RTTFactory,
): Tag {

    val definedType = type.functionType.definedType()
    val rtt = rttFactory(definedType, store.store.rttCache).apply {
        hydrate()
    }

    return Tag(ExternalValue.Tag(allocator(store.store, rtt, type)))
}
