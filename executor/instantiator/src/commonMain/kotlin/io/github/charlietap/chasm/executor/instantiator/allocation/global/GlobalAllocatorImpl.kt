package io.github.charlietap.chasm.executor.instantiator.allocation.global

import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.executor.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

internal fun GlobalAllocatorImpl(
    store: Store,
    type: GlobalType,
    value: ExecutionValue,
): Address.Global {

    val instance = GlobalInstance(type, value)

    store.globals.add(instance)

    return Address.Global(store.globals.size - 1)
}
