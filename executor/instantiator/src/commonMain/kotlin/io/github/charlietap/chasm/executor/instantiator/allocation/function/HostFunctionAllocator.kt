package io.github.charlietap.chasm.executor.instantiator.allocation.function

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.HostFunction
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ext.definedType
import io.github.charlietap.chasm.type.factory.RTTFactory

typealias HostFunctionAllocator = (Store, FunctionType, HostFunction) -> ExternalValue.Function

fun HostFunctionAllocator(
    store: Store,
    functionType: FunctionType,
    function: HostFunction,
): ExternalValue.Function =
    HostFunctionAllocator(
        store = store,
        functionType = functionType,
        function = function,
        rttFactory = ::RTTFactory,
    )

fun HostFunctionAllocator(
    store: Store,
    functionType: FunctionType,
    function: HostFunction,
    rttFactory: RTTFactory,
): ExternalValue.Function {

    val type = functionType.definedType()
    val rtt = rttFactory(type, store.rttCache).apply {
        hydrate()
    }

    val instance = FunctionInstance.HostFunction(rtt, functionType, function)

    store.functions.add(instance)
    val address = Address.Function(store.functions.size - 1)

    return ExternalValue.Function(address)
}
