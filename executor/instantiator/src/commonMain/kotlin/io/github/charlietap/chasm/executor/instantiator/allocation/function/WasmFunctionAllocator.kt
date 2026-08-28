package io.github.charlietap.chasm.executor.instantiator.allocation.function

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.addFunctionAddress
import io.github.charlietap.chasm.runtime.function.WasmFunctionCallStrategy
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.ext.functionType

internal typealias WasmFunctionAllocator = (Module, ModuleInstance, Function, Store) -> Result<Unit, ModuleTrapError>

internal fun WasmFunctionAllocator(
    module: Module,
    moduleInstance: ModuleInstance,
    function: Function,
    store: Store,
): Result<Unit, ModuleTrapError> = binding {

    val typeIndex = function.typeIndex.toInt()
    val type = module.definedTypes.getOrNull(typeIndex)
        ?: Err(InstantiationError.FailedToResolveFunctionType(function.typeIndex)).bind()
    val rtt = moduleInstance.runtimeTypes[typeIndex]
    val functionType = type.functionType()
        ?: Err(InstantiationError.FailedToResolveFunctionType(function.typeIndex)).bind()

    // Function bodies may reference functions with higher indices, so allocate every
    // function instance before compiling any body.
    val instance = FunctionInstance.WasmFunction(
        rtt = rtt,
        functionType = functionType,
        module = moduleInstance,
        callStrategy = WasmFunctionCallStrategy(
            interfaceSlotCount = maxOf(functionType.params.types.size, functionType.results.types.size),
        ),
    )
    store.functions.add(instance)
    moduleInstance.addFunctionAddress(Address.Function(store.functions.size - 1))
}
