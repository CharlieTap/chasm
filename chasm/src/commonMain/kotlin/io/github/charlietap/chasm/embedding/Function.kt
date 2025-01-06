package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.FunctionType
import io.github.charlietap.chasm.embedding.shapes.HostFunction
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.transform.FunctionTypeMapper
import io.github.charlietap.chasm.embedding.transform.HostFunctionMapper
import io.github.charlietap.chasm.embedding.transform.Mapper
import io.github.charlietap.chasm.executor.instantiator.allocation.function.HostFunctionAllocator
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.ast.type.FunctionType as InternalFunctionType
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
    functionTypeMapper = FunctionTypeMapper.instance,
    hostFunctionMapper = HostFunctionMapper,
)

internal fun function(
    store: Store,
    type: FunctionType,
    function: HostFunction,
    allocator: HostFunctionAllocator,
    functionTypeMapper: Mapper<FunctionType, InternalFunctionType>,
    hostFunctionMapper: Mapper<HostFunction, InternalHostFunction>,
): Function {
    val functionType = functionTypeMapper.map(type)
    val hostFunction = hostFunctionMapper.map(function)
    return Function(ExternalValue.Function(allocator(store.store, functionType, hostFunction)))
}
