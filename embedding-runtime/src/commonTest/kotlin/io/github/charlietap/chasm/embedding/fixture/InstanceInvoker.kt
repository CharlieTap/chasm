package io.github.charlietap.chasm.embedding.fixture

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.internal.InstanceInvoker
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ExecutionValue

fun hostFunctionInvoker(): InstanceInvoker = instanceInvoker(
    byAddress = { config, store, instance, address, values ->
        FunctionInvoker(config, store, instance, address, values) { _, _, _, _ -> error("no engine runs Wasm functions here") }
    },
    byFunction = { config, store, instance, function, values ->
        FunctionInvoker(config, store, instance, function, values) { _, _, _, _ -> error("no engine runs Wasm functions here") }
    },
)

fun instanceInvoker(
    byAddress: FunctionInvoker = { _, _, _, _, _ -> error("invoked by address") },
    byFunction: (RuntimeConfig, Store, ModuleInstance, FunctionInstance, List<ExecutionValue>) -> Result<List<ExecutionValue>, InvocationError> =
        { _, _, _, _, _ -> error("invoked by function") },
): InstanceInvoker = object : InstanceInvoker {

    override fun invoke(
        config: RuntimeConfig,
        store: Store,
        instance: ModuleInstance,
        address: Address.Function,
        values: List<ExecutionValue>,
    ): Result<List<ExecutionValue>, InvocationError> = byAddress(config, store, instance, address, values)

    override fun invoke(
        config: RuntimeConfig,
        store: Store,
        instance: ModuleInstance,
        function: FunctionInstance,
        values: List<ExecutionValue>,
    ): Result<List<ExecutionValue>, InvocationError> = byFunction(config, store, instance, function, values)
}
