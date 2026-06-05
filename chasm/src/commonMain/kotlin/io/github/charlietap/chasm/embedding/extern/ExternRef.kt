package io.github.charlietap.chasm.embedding.extern

import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.ext.allocateHost
import io.github.charlietap.chasm.runtime.instance.HostInstance
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType

fun externRef(
    store: Store,
    value: Any?,
): ReferenceValue {
    if (value == null) {
        return ReferenceValue.Null(AbstractHeapType.Extern)
    }

    val address = store.store.allocateHost(HostInstance(value))
    return ReferenceValue.Extern(ReferenceValue.Host(address))
}
