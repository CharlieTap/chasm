package io.github.charlietap.chasm.executor.instantiator.allocation.table

import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.ir.type.TableType

typealias TableAllocator = (Store, TableType, Long) -> Address.Table

fun TableAllocator(
    store: Store,
    type: TableType,
    reference: Long,
): Address.Table {

    val elements = LongArray(type.limits.min.toInt()) { reference }
    val instance = TableInstance(type, elements)

    store.tables.add(instance)

    return Address.Table(store.tables.size - 1)
}
