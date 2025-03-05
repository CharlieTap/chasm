package io.github.charlietap.chasm.executor.instantiator.allocation.data

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.store.Store

internal typealias DataAllocator = (Store, UByteArray) -> Address.Data

internal fun DataAllocator(
    store: Store,
    data: UByteArray,
): Address.Data {

    val instance = DataInstance(data)

    store.data.add(instance)

    return Address.Data(store.data.size - 1)
}
