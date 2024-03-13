package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.executor.instantiator.allocation.table.TableAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.table.TableAllocatorImpl
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

fun table(
    store: Store,
    type: TableType,
    value: ReferenceValue,
): ExternalValue.Table = table(
    store = store,
    type = type,
    value = value,
    allocator = ::TableAllocatorImpl,
)

internal fun table(
    store: Store,
    type: TableType,
    value: ReferenceValue,
    allocator: TableAllocator,
): ExternalValue.Table = ExternalValue.Table(allocator(store, type, value))
