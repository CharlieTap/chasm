package io.github.charlietap.chasm.executor.instantiator.allocation.function

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.predecoding.FunctionPredecoder
import io.github.charlietap.chasm.executor.instantiator.predecoding.Predecoder
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.function.Function as RuntimeFunction

internal typealias WasmFunctionAllocator = (InstantiationContext, ModuleInstance, Function) -> Result<Address.Function, ModuleTrapError>

internal fun WasmFunctionAllocator(
    context: InstantiationContext,
    moduleInstance: ModuleInstance,
    function: Function,
): Result<Address.Function, ModuleTrapError> =
    WasmFunctionAllocator(
        context = context,
        moduleInstance = moduleInstance,
        function = function,
        functionPredecoder = ::FunctionPredecoder,
    )

internal inline fun WasmFunctionAllocator(
    context: InstantiationContext,
    moduleInstance: ModuleInstance,
    function: Function,
    crossinline functionPredecoder: Predecoder<Function, RuntimeFunction>,
): Result<Address.Function, ModuleTrapError> = binding {

    val store = context.store
    val type = moduleInstance.types.getOrNull(function.typeIndex.idx.toInt())
        ?: Err(InstantiationError.FailedToResolveFunctionType(function.typeIndex)).bind()
    val predecoded = functionPredecoder(context, function).bind()
    val instance = FunctionInstance.WasmFunction(type, moduleInstance, predecoded)

    store.functions.add(instance)

    Address.Function(store.functions.size - 1)
}
