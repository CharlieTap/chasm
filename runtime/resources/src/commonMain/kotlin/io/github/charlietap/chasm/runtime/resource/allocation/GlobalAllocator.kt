package io.github.charlietap.chasm.runtime.resource.allocation

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.GlobalType

@InternalChasmApi
typealias GlobalAllocator = (Store, GlobalType, Long) -> Address.Global

@InternalChasmApi
fun GlobalAllocator(
    store: Store,
    type: GlobalType,
    value: Long,
): Address.Global {

    val instance = GlobalInstance(type, value)
    store.globals.add(instance)

    return Address.Global(store.globals.size - 1)
}
