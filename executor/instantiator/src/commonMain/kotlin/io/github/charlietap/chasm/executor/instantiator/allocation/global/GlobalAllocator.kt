package io.github.charlietap.chasm.executor.instantiator.allocation.global

import io.github.charlietap.chasm.executor.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.ir.type.GlobalType

typealias GlobalAllocator = (Store, GlobalType, Long) -> Address.Global

fun GlobalAllocator(
    store: Store,
    type: GlobalType,
    value: Long,
): Address.Global {

    val instance = GlobalInstance(type, value)
    store.globals.add(instance)

    return Address.Global(store.globals.size - 1)
}
