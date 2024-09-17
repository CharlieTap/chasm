package io.github.charlietap.chasm.executor.instantiator.allocation.function

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias WasmFunctionAllocator = (Store, ModuleInstance, Function) -> Result<Address.Function, InstantiationError.FailedToResolveFunctionType>

internal fun WasmFunctionAllocator(
    store: Store,
    moduleInstance: ModuleInstance,
    function: Function,
): Result<Address.Function, InstantiationError.FailedToResolveFunctionType> {

    val type = moduleInstance.types.getOrNull(function.typeIndex.idx.toInt())
        ?: return Err(InstantiationError.FailedToResolveFunctionType(function.typeIndex))
    val instance = FunctionInstance.WasmFunction(type, moduleInstance, function)

    store.functions.add(instance)

    return Ok(Address.Function(store.functions.size - 1))
}
