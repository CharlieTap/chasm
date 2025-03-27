package io.github.charlietap.chasm.executor.instantiator.allocation.function

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.WasmFunctionCallDispatcher
import io.github.charlietap.chasm.ir.module.Function
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.addFunctionAddress
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.ext.functionType
import io.github.charlietap.chasm.runtime.function.Function as RuntimeFunction

internal typealias WasmFunctionAllocator = (ModuleInstance, Function, Store) -> Result<Unit, ModuleTrapError>

internal inline fun WasmFunctionAllocator(
    moduleInstance: ModuleInstance,
    function: Function,
    store: Store,
): Result<Unit, ModuleTrapError> =
    WasmFunctionAllocator(
        moduleInstance = moduleInstance,
        function = function,
        store = store,
        callDispatcher = ::WasmFunctionCallDispatcher,
    )

internal inline fun WasmFunctionAllocator(
    moduleInstance: ModuleInstance,
    function: Function,
    store: Store,
    crossinline callDispatcher: Dispatcher<ControlInstruction.WasmFunctionCall>,
): Result<Unit, ModuleTrapError> = binding {

    val type = moduleInstance.types.getOrNull(function.typeIndex.idx)
        ?: Err(InstantiationError.FailedToResolveFunctionType(function.typeIndex)).bind()
    val runtimeType = moduleInstance.runtimeTypes.getOrNull(function.typeIndex.idx)
        ?: Err(InstantiationError.FailedToResolveFunctionType(function.typeIndex)).bind()
    val functionType = type.functionType()
        ?: Err(InstantiationError.FailedToResolveFunctionType(function.typeIndex)).bind()

    // We create a function instance with a temp inner function that will be replaced after
    // precoding, functions can have instructions which reference functions with higher indices
    // thus all instances must be created prior to precoding
    val instance = FunctionInstance.WasmFunction(
        type = type,
        rtt = runtimeType,
        functionType = functionType,
        module = moduleInstance,
        function = RuntimeFunction.TEMP,
    )
    store.functions.add(instance)
    moduleInstance.addFunctionAddress(Address.Function(store.functions.size - 1))

    // Preallocate the handler
    val instruction = callDispatcher(ControlInstruction.WasmFunctionCall(instance))
    store.instructions.add(instruction)
}
