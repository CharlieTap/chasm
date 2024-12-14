package io.github.charlietap.chasm.executor.instantiator.allocation.function

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.addFunctionAddress
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.function.Function as RuntimeFunction

internal typealias WasmFunctionAllocator = (InstantiationContext, ModuleInstance, Function) -> Result<Unit, ModuleTrapError>

internal inline fun WasmFunctionAllocator(
    context: InstantiationContext,
    moduleInstance: ModuleInstance,
    function: Function,
): Result<Unit, ModuleTrapError> = binding {

    val store = context.store
    val type = moduleInstance.types.getOrNull(function.typeIndex.idx.toInt())
        ?: Err(InstantiationError.FailedToResolveFunctionType(function.typeIndex)).bind()

    // We create a function instance with a temp inner function that will be replaced after
    // precoding, functions can have instructions which reference functions with higher indices
    // thus all instances must be created prior to precoding
    val instance = FunctionInstance.WasmFunction(type, moduleInstance, RuntimeFunction.TEMP)
    store.functions.add(instance)
    moduleInstance.addFunctionAddress(Address.Function(store.functions.size - 1))
}
