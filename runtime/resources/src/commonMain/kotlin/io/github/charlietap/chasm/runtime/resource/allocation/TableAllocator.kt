package io.github.charlietap.chasm.runtime.resource.allocation

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.TableType

@InternalChasmApi
typealias TableAllocator = (Store, TableType, Long) -> Address.Table

@InternalChasmApi
fun TableAllocator(
    store: Store,
    type: TableType,
    reference: Long,
): Address.Table {

    val elements = LongArray(type.limits.min.toInt()) { reference }
    val limit = store.tableElementLimit
    val effectiveMaximum = minOf(TableInstance.declaredMaximumElements(type), limit ?: Int.MAX_VALUE)
    val instance = TableInstance(type.copy(limits = type.limits.copy()), elements, effectiveMaximum)

    store.tables.add(instance)

    return Address.Table(store.tables.size - 1)
}
