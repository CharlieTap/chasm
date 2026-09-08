package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.function.withHostCallbackScope
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.HostRaisedWasmException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.toExecutionValue
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ExecutionValue

typealias FunctionInvoker = (RuntimeConfig, Store, ModuleInstance, Address.Function, List<ExecutionValue>) -> Result<List<ExecutionValue>, InvocationError>

fun FunctionInvoker(
    config: RuntimeConfig,
    store: Store,
    instance: ModuleInstance,
    address: Address.Function,
    values: List<ExecutionValue>,
): Result<List<ExecutionValue>, InvocationError> = FunctionInvoker(
    config = config,
    store = store,
    instance = instance,
    function = store.function(address),
    values = values,
)

fun FunctionInvoker(
    config: RuntimeConfig,
    store: Store,
    instance: ModuleInstance,
    function: FunctionInstance,
    values: List<ExecutionValue>,
): Result<List<ExecutionValue>, InvocationError> =
    FunctionInvoker(
        config = config,
        store = store,
        instance = instance,
        function = function,
        values = values,
        threadExecutor = ::ThreadExecutor,
    )

@OptIn(UnsafeHostApi::class)
internal inline fun FunctionInvoker(
    config: RuntimeConfig,
    store: Store,
    instance: ModuleInstance,
    address: Address.Function,
    values: List<ExecutionValue>,
    crossinline threadExecutor: ThreadExecutor,
): Result<List<ExecutionValue>, InvocationError> = FunctionInvoker(
    config = config,
    store = store,
    instance = instance,
    function = store.function(address),
    values = values,
    threadExecutor = threadExecutor,
)

@OptIn(UnsafeHostApi::class)
internal inline fun FunctionInvoker(
    config: RuntimeConfig,
    store: Store,
    instance: ModuleInstance,
    function: FunctionInstance,
    values: List<ExecutionValue>,
    crossinline threadExecutor: ThreadExecutor,
): Result<List<ExecutionValue>, InvocationError> {
    store.heap.clearPendingException()
    return when (function) {
        is FunctionInstance.HostFunction -> {
            val resultCount = function.functionType.results.types.size
            val stack = ValueStack(maxOf(values.size, resultCount))
            stack.push(LongArray(values.size) { index -> values[index].toLongFromBoxed() })
            stack.reserveDepth(resultCount)
            val context = ExecutionContext(stack, store, instance, config)
            try {
                context.withHostCallbackScope {
                    context(stack.unsafeElements(), instance, context) {
                        function.function.invoke(0, 0)
                    }
                    Ok(
                        List(resultCount) { index ->
                            stack.unsafeElements()[index].toExecutionValue(function.functionType.results.types[index])
                        },
                    )
                }
            } catch (_: HostRaisedWasmException) {
                Err(InvocationError.ThrownException)
            } catch (e: HostFunctionException) {
                Err(InvocationError.HostFunctionError(e.reason))
            }
        }
        is FunctionInstance.WasmFunction -> binding {
            threadExecutor(config, store, function, values).bind().mapIndexed { idx, result ->
                val expected = function.functionType.results.types[idx]
                result.toExecutionValue(expected)
            }
        }
    }
}
