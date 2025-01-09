package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.instantiator.allocation.global.GlobalAllocator
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

fun global(
    store: Store,
    type: GlobalType,
    value: ExecutionValue,
): Global = global(
    store = store,
    type = type,
    value = value,
    allocator = ::GlobalAllocator,
)

internal fun global(
    store: Store,
    type: GlobalType,
    value: ExecutionValue,
    allocator: GlobalAllocator,
): Global {
    return Global(ExternalValue.Global(allocator(store.store, type, value)))
}
