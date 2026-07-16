package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.thread.RawThreadExecutor
import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.runtime.Configuration
import io.github.charlietap.chasm.runtime.Thread
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.instruction
import io.github.charlietap.chasm.runtime.ext.toExecutionValue
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.HostFunctionContext
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.runtime.stack.StackDepths
import io.github.charlietap.chasm.runtime.store.Store

typealias RawFunctionInvoker = (
    RuntimeConfig,
    Store,
    ModuleInstance,
    Address.Function,
    LongArray,
    Int,
    LongArray,
) -> Result<Int, InvocationError>

fun RawFunctionInvoker(
    config: RuntimeConfig,
    store: Store,
    instance: ModuleInstance,
    address: Address.Function,
    values: LongArray,
    valueCount: Int,
    results: LongArray,
): Result<Int, InvocationError> = try {
    val function = store.function(address)
    if (valueCount != function.functionType.params.types.size) {
        return Err(InvocationError.FunctionInconsistentWithType)
    }
    if (results.size < function.functionType.results.types.size) {
        return Err(InvocationError.ProgramFinishedInconsistentState)
    }
    when (function) {
        is FunctionInstance.HostFunction -> invokeHostFunction(
            config,
            store,
            instance,
            function,
            values,
            valueCount,
            results,
        )
        is FunctionInstance.StackFunction -> invokeRawThread(
            config = config,
            store = store,
            instance = instance,
            address = address,
            arity = function.functionType.results.types.size,
            values = values,
            valueCount = valueCount,
            results = results,
        )
        is FunctionInstance.WasmFunction -> invokeRawThread(
            config = config,
            store = store,
            instance = function.module,
            address = address,
            arity = function.functionType.results.types.size,
            values = values,
            valueCount = valueCount,
            results = results,
        )
    }
} catch (exception: InvocationException) {
    Err(exception.error)
}

private fun invokeHostFunction(
    config: RuntimeConfig,
    store: Store,
    instance: ModuleInstance,
    function: FunctionInstance.HostFunction,
    values: LongArray,
    valueCount: Int,
    destination: LongArray,
): Result<Int, InvocationError> {
    val type = function.functionType
    if (valueCount != type.params.types.size) return Err(InvocationError.FunctionInconsistentWithType)

    val arguments = List(valueCount) { index ->
        values[index].toExecutionValue(type.params.types[index])
    }
    val results = try {
        function.function(HostFunctionContext(config, store, instance), arguments)
    } catch (exception: HostFunctionException) {
        return Err(InvocationError.HostFunctionError(exception.reason))
    }
    if (results.size != type.results.types.size) return Err(InvocationError.FunctionInconsistentWithType)
    if (destination.size < results.size) return Err(InvocationError.ProgramFinishedInconsistentState)

    results.forEachIndexed { index, value -> destination[index] = value.toLongFromBoxed() }
    return Ok(results.size)
}

private fun invokeRawThread(
    config: RuntimeConfig,
    store: Store,
    instance: ModuleInstance,
    address: Address.Function,
    arity: Int,
    values: LongArray,
    valueCount: Int,
    results: LongArray,
): Result<Int, InvocationError> {
    val thread = Thread(
        frame = ActivationFrame(
            arity = arity,
            instance = instance,
            depths = StackDepths(0, 0, 0, 0),
        ),
        instructions = arrayOf(store.instruction(address)),
    )
    return RawThreadExecutor(
        configuration = Configuration(store = store, thread = thread, config = config),
        params = values,
        paramCount = valueCount,
        results = results,
    )
}
