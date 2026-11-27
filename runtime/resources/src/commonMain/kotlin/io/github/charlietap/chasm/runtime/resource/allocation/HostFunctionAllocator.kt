package io.github.charlietap.chasm.runtime.resource.allocation

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.host.RawHostFunction
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ext.definedType

@InternalChasmApi
typealias HostFunctionAllocator = (Store, FunctionType, RawHostFunction) -> ExternalValue.Function

@InternalChasmApi
fun HostFunctionAllocator(
    store: Store,
    functionType: FunctionType,
    function: RawHostFunction,
): ExternalValue.Function {

    val type = functionType.definedType()
    val rtt = store.heap.registerRuntimeType(type)

    val instance = FunctionInstance.HostFunction(rtt, functionType, function)

    store.functions.add(instance)
    val address = Address.Function(store.functions.size - 1)

    return ExternalValue.Function(address)
}
