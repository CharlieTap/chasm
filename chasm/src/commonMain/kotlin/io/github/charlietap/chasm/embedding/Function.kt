package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.instantiator.allocation.function.HostFunctionAllocator
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.type.FunctionType

fun function(
    store: Store,
    type: FunctionType,
    function: HostFunction,
): Function = Function(
    reference = HostFunctionAllocator(store.store, type, function),
    store = store.store,
)

internal fun function(
    store: Store,
    type: FunctionType,
    function: HostFunction,
    allocator: HostFunctionAllocator,
): Function = Function(
    reference = allocator(store.store, type, function),
    store = store.store,
)
