package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.HostFunction
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.transform.HostFunctionMapper
import io.github.charlietap.chasm.embedding.transform.Mapper
import io.github.charlietap.chasm.executor.instantiator.allocation.function.HostFunctionAllocator
import io.github.charlietap.chasm.executor.runtime.instance.HostFunction as InternalHostFunction

fun function(
    store: Store,
    type: FunctionType,
    function: HostFunction,
): Function = function(
    store = store,
    type = type,
    function = function,
    allocator = ::HostFunctionAllocator,
    hostFunctionMapper = HostFunctionMapper,
)

internal fun function(
    store: Store,
    type: FunctionType,
    function: HostFunction,
    allocator: HostFunctionAllocator,
    hostFunctionMapper: Mapper<HostFunction, InternalHostFunction>,
): Function {
    val hostFunction = hostFunctionMapper.map(function)
    return Function(
        reference = allocator(store.store, type, hostFunction),
    )
}
