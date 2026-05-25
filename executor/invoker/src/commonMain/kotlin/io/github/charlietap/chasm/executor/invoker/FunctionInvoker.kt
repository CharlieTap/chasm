package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.runtime.Configuration
import io.github.charlietap.chasm.runtime.Thread
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.instruction
import io.github.charlietap.chasm.runtime.ext.toExecutionValue
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.HostFunctionContext
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.runtime.stack.StackDepths
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ExecutionValue

typealias FunctionInvoker = (RuntimeConfig, Store, ModuleInstance, Address.Function, List<ExecutionValue>) -> Result<List<ExecutionValue>, InvocationError>

fun FunctionInvoker(
    config: RuntimeConfig,
    store: Store,
    instance: ModuleInstance,
    address: Address.Function,
    values: List<ExecutionValue>,
): Result<List<ExecutionValue>, InvocationError> =
    FunctionInvoker(
        config = config,
        store = store,
        instance = instance,
        address = address,
        values = values,
        threadExecutor = ::ThreadExecutor,
    )

internal inline fun FunctionInvoker(
    config: RuntimeConfig,
    store: Store,
    instance: ModuleInstance,
    address: Address.Function,
    values: List<ExecutionValue>,
    crossinline threadExecutor: ThreadExecutor,
): Result<List<ExecutionValue>, InvocationError> = when (val function = store.function(address)) {
    is FunctionInstance.HostFunction -> {
        val context = HostFunctionContext(config, store, instance)
        try {
            Ok(function.function(context, values))
        } catch (e: HostFunctionException) {
            Err(InvocationError.HostFunctionError(e.reason))
        }
    }
    is FunctionInstance.WasmFunction -> binding {
        val instruction = store.instruction(address)
        val arity = function.functionType.results.types.size

        val thread = Thread(
            frame = ActivationFrame(
                arity = arity,
                instance = function.module,
                depths = StackDepths(0, 0, 0, 0),
            ),
            instructions = arrayOf(instruction),
        )

        val configuration = Configuration(
            store = store,
            thread = thread,
            config = config,
        )

        threadExecutor(configuration, values).bind().mapIndexed { idx, result ->
            val expected = function.functionType.results.types[idx]
            result.toExecutionValue(expected)
        }
    }
}
