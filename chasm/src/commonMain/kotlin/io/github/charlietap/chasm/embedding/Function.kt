package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.executor.instantiator.allocation.function.HostFunctionAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.function.HostFunctionAllocatorImpl
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.instance.HostFunction
import io.github.charlietap.chasm.executor.runtime.store.Store

fun function(
    store: Store,
    type: FunctionType,
    function: HostFunction,
): ExternalValue.Function = function(
    store = store,
    type = type,
    function = function,
    allocator = ::HostFunctionAllocatorImpl,
)

internal fun function(
    store: Store,
    type: FunctionType,
    function: HostFunction,
    allocator: HostFunctionAllocator,
): ExternalValue.Function = ExternalValue.Function(allocator(store, type, function))
