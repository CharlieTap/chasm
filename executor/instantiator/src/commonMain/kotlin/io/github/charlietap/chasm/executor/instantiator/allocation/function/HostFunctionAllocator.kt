package io.github.charlietap.chasm.executor.instantiator.allocation.function

import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.HostFunction
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.type.ext.definedType

typealias HostFunctionAllocator = (Store, FunctionType, HostFunction) -> Address.Function

fun HostFunctionAllocator(
    store: Store,
    functionType: FunctionType,
    function: HostFunction,
): Address.Function {

    val type = functionType.definedType()
    val instance = FunctionInstance.HostFunction(type, function)

    store.functions.add(instance)

    return Address.Function(store.functions.size - 1)
}
