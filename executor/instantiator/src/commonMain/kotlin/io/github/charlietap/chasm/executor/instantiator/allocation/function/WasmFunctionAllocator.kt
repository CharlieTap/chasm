package io.github.charlietap.chasm.executor.instantiator.allocation.function

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ir.module.Function
import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.addFunctionAddress
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.ext.functionType
import io.github.charlietap.chasm.runtime.function.Function as RuntimeFunction

internal typealias WasmFunctionAllocator = (Module, ModuleInstance, Function, Store) -> Result<Unit, ModuleTrapError>

internal fun WasmFunctionAllocator(
    module: Module,
    moduleInstance: ModuleInstance,
    function: Function,
    store: Store,
): Result<Unit, ModuleTrapError> = binding {

    val type = module.definedTypes.getOrNull(function.typeIndex.idx)
        ?: Err(InstantiationError.FailedToResolveFunctionType(function.typeIndex)).bind()
    val runtimeType = moduleInstance.runtimeTypes.getOrNull(function.typeIndex.idx)?.apply {
        hydrate()
    } ?: Err(InstantiationError.FailedToResolveFunctionType(function.typeIndex)).bind()
    val functionType = type.functionType()
        ?: Err(InstantiationError.FailedToResolveFunctionType(function.typeIndex)).bind()

    // We create a function instance with a temp inner function that will be replaced after
    // precoding, functions can have instructions which reference functions with higher indices
    // thus all instances must be created prior to precoding
    val instance = FunctionInstance.WasmFunction(
        rtt = runtimeType,
        functionType = functionType,
        module = moduleInstance,
        function = RuntimeFunction.TEMP,
    )
    store.functions.add(instance)
    moduleInstance.addFunctionAddress(Address.Function(store.functions.size - 1))
}
