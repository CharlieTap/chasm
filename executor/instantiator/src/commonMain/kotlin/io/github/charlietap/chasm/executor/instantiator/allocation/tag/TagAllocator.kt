package io.github.charlietap.chasm.executor.instantiator.allocation.tag

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.TagInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.TagType

typealias TagAllocator = (Store, TagType) -> Address.Tag

fun TagAllocator(
    store: Store,
    type: TagType,
): Address.Tag {

    val instance = TagInstance(type)

    store.tags.add(instance)

    return Address.Tag(store.tags.size - 1)
}
