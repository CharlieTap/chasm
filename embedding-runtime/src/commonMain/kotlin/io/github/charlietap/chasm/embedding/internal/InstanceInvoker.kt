package io.github.charlietap.chasm.embedding.internal

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ExecutionValue

@InternalChasmApi
interface InstanceInvoker {

    fun invoke(
        config: RuntimeConfig,
        store: Store,
        instance: ModuleInstance,
        address: Address.Function,
        values: List<ExecutionValue>,
    ): Result<List<ExecutionValue>, InvocationError>

    fun invoke(
        config: RuntimeConfig,
        store: Store,
        instance: ModuleInstance,
        function: FunctionInstance,
        values: List<ExecutionValue>,
    ): Result<List<ExecutionValue>, InvocationError>
}
