package io.github.charlietap.chasm.executor.instantiator.allocation.tag

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.TagInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.RTT
import io.github.charlietap.chasm.type.TagType

typealias TagAllocator = (Store, RTT, TagType) -> Address.Tag

fun TagAllocator(
    store: Store,
    rtt: RTT,
    type: TagType,
): Address.Tag {

    val instance = TagInstance(rtt, type)
    store.tags.add(instance)

    return Address.Tag(store.tags.size - 1)
}
