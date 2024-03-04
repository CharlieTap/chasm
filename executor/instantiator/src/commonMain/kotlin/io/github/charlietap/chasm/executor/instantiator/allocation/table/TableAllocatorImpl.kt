package io.github.charlietap.chasm.executor.instantiator.allocation.table

import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun TableAllocatorImpl(
    store: Store,
    type: TableType,
    reference: ReferenceValue,
): Address.Table {

    val elements = MutableList(type.limits.min.toInt()) { reference }
    val instance = TableInstance(type, elements)

    store.tables.add(instance)

    return Address.Table(store.tables.size - 1)
}
