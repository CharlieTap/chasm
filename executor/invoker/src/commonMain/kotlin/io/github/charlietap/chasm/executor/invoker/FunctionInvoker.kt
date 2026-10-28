@file:OptIn(InternalChasmApi::class)
@file:JvmName("InterpreterFunctionInvokerKt")

package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.executor.invoker.thread.TraceableThreadExecutor
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import kotlin.jvm.JvmName

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
): Result<List<ExecutionValue>, InvocationError> = if (config.debugInfo) {
    FunctionInvoker(
        config = config,
        store = store,
        instance = instance,
        function = function,
        values = values,
        wasmFunctionExecutor = ::TraceableThreadExecutor,
    )
} else {
    FunctionInvoker(
        config = config,
        store = store,
        instance = instance,
        function = function,
        values = values,
        wasmFunctionExecutor = ::ThreadExecutor,
    )
}
