package io.github.charlietap.chasm.executor.instantiator.allocation.function

import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.HostFunction
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store

fun HostFunctionAllocatorImpl(
    store: Store,
    type: FunctionType,
    function: HostFunction,
): Address.Function {

    val instance = FunctionInstance.HostFunction(type, function)

    store.functions.add(instance)

    return Address.Function(store.functions.size - 1)
}
