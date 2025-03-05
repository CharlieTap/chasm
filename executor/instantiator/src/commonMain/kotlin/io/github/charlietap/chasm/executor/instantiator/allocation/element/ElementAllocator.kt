package io.github.charlietap.chasm.executor.instantiator.allocation.element

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.ReferenceType

internal typealias ElementAllocator = (Store, ReferenceType, LongArray) -> Address.Element

internal fun ElementAllocator(
    store: Store,
    type: ReferenceType,
    values: LongArray,
): Address.Element {

    val instance = ElementInstance(type, values)

    store.elements.add(instance)

    return Address.Element(store.elements.size - 1)
}
