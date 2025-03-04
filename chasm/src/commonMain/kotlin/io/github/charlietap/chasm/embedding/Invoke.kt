package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.value.ExecutionValue

fun invoke(
    store: Store,
    instance: Instance,
    name: String,
    args: List<ExecutionValue> = emptyList(),
): ChasmResult<List<ExecutionValue>, ChasmError.ExecutionError> = invoke(
    store = store,
    instance = instance,
    name = name,
    args = args,
    invoker = ::FunctionInvoker,
)

internal fun invoke(
    store: Store,
    instance: Instance,
    name: String,
    args: List<ExecutionValue>,
    invoker: FunctionInvoker,
): ChasmResult<List<ExecutionValue>, ChasmError.ExecutionError> {

    if (instance.instance.deallocated) {
        return Error(ChasmError.ExecutionError(InvocationError.InvocationOfADeinstantiatedInstance.toString()))
    }

    val extern = instance.instance.exports
        .firstOrNull { export ->
            export.name.name == name
        }?.value

    val address = (extern as? ExternalValue.Function)?.address ?: return Error(
        ChasmError.ExecutionError(InvocationError.FunctionNotFound(name).toString()),
    )

    return invoker(instance.config, store.store, address, args)
        .mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)
}
