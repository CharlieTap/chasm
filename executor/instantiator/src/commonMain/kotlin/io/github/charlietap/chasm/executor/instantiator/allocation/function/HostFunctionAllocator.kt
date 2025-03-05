package io.github.charlietap.chasm.executor.instantiator.allocation.function

import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.HostFunctionCallDispatcher
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.HostFunction
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ext.definedType

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
        callDispatcher = ::HostFunctionCallDispatcher,
    )

fun HostFunctionAllocator(
    store: Store,
    functionType: FunctionType,
    function: HostFunction,
    callDispatcher: Dispatcher<ControlInstruction.HostFunctionCall>,
): ExternalValue.Function {

    val type = functionType.definedType()
    val instance = FunctionInstance.HostFunction(type, functionType, function)

    store.functions.add(instance)
    val address = Address.Function(store.functions.size - 1)

    val instruction = callDispatcher(ControlInstruction.HostFunctionCall(instance))
    store.instructions.add(instruction)

    return ExternalValue.Function(address)
}
