package io.github.charlietap.chasm.executor.instantiator.allocation.element

import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.runtime.instance.ElementInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun ElementAllocatorImpl(
    store: Store,
    type: ReferenceType,
    values: List<ReferenceValue>,
): Address.Element {

    val instance = ElementInstance(type, values.toTypedArray())

    store.elements.add(instance)

    return Address.Element(store.elements.size - 1)
}
