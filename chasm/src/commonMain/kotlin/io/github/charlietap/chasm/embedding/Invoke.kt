package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.ChasmResult.Error
import io.github.charlietap.chasm.ChasmResult.Success
import io.github.charlietap.chasm.error.ChasmError
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.executor.invoker.FunctionInvokerImpl
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

fun invoke(
    store: Store,
    instance: ModuleInstance,
    name: String,
    args: List<ExecutionValue> = emptyList(),
): ChasmResult<List<ExecutionValue>, ChasmError.ExecutionError> = invoke(
    store = store,
    instance = instance,
    name = name,
    args = args,
    invoker = ::FunctionInvokerImpl,
)

internal fun invoke(
    store: Store,
    instance: ModuleInstance,
    name: String,
    args: List<ExecutionValue>,
    invoker: FunctionInvoker,
): ChasmResult<List<ExecutionValue>, ChasmError.ExecutionError> {
    val extern = instance.exports.firstOrNull { export ->
        export.name.name == name
    }?.value
    val address = (extern as ExternalValue.Function).address

    return invoker(store, address, args)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)
}
