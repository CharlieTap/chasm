package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.executor.instantiator.allocation.table.TableAllocator
import io.github.charlietap.chasm.executor.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.TableType

fun table(
    store: Store,
    type: TableType,
    value: ReferenceValue,
): Table = table(
    store = store,
    type = type,
    value = value,
    allocator = ::TableAllocator,
)

internal fun table(
    store: Store,
    type: TableType,
    value: ReferenceValue,
    allocator: TableAllocator,
): Table {
    return Table(ExternalValue.Table(allocator(store.store, type, value.toLongFromBoxed())))
}
