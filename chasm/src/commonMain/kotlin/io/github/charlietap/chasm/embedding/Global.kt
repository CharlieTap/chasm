package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.executor.instantiator.allocation.global.GlobalAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.global.GlobalAllocatorImpl
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

fun global(
    store: Store,
    type: GlobalType,
    value: ExecutionValue,
): ExternalValue.Global = global(
    store = store,
    type = type,
    value = value,
    allocator = ::GlobalAllocatorImpl,
)

internal fun global(
    store: Store,
    type: GlobalType,
    value: ExecutionValue,
    allocator: GlobalAllocator,
): ExternalValue.Global = ExternalValue.Global(allocator(store, type, value))
