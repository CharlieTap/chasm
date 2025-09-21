package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.value.ExecutionValue

fun invoke(
    store: Store,
    instance: Instance,
    function: Function,
    args: List<ExecutionValue> = emptyList(),
): ChasmResult<List<ExecutionValue>, ChasmError.ExecutionError> = invoke(
    store = store,
    instance = instance,
    function = function,
    args = args,
    invoker = ::FunctionInvoker,
)

fun invoke(
    store: Store,
    instance: Instance,
    name: String,
    args: List<ExecutionValue> = emptyList(),
): ChasmResult<List<ExecutionValue>, ChasmError.ExecutionError> {

    val extern = instance.instance.exports
        .firstOrNull { export ->
            export.name.name == name
        }?.value

    val address = (extern as? ExternalValue.Function)?.address ?: return Error(
        ChasmError.ExecutionError(InvocationError.FunctionNotFound(name).toString()),
    )

    return invoke(
        store = store,
        instance = instance,
        address = address,
        args = args,
        invoker = ::FunctionInvoker,
    )
}

internal fun invoke(
    store: Store,
    instance: Instance,
    function: Function,
    args: List<ExecutionValue> = emptyList(),
    invoker: FunctionInvoker,
): ChasmResult<List<ExecutionValue>, ChasmError.ExecutionError> = invoke(
    store = store,
    instance = instance,
    address = function.reference.address,
    args = args,
    invoker = invoker,
)

internal fun invoke(
    store: Store,
    instance: Instance,
    name: String,
    args: List<ExecutionValue> = emptyList(),
    invoker: FunctionInvoker,
): ChasmResult<List<ExecutionValue>, ChasmError.ExecutionError> {

    val extern = instance.instance.exports.firstOrNull { export ->
        export.name.name == name
    }?.value

    val address = (extern as? ExternalValue.Function)?.address ?: return Error(
        ChasmError.ExecutionError(InvocationError.FunctionNotFound(name).toString()),
    )

    return invoke(
        store = store,
        instance = instance,
        address = address,
        args = args,
        invoker = invoker,
    )
}

internal inline fun invoke(
    store: Store,
    instance: Instance,
    address: Address.Function,
    args: List<ExecutionValue>,
    invoker: FunctionInvoker,
): ChasmResult<List<ExecutionValue>, ChasmError.ExecutionError> {

    if (instance.instance.deallocated) {
        return Error(ChasmError.ExecutionError(InvocationError.InvocationOfADeinstantiatedInstance.toString()))
    }

    val validAddresses = instance.instance.exports.mapNotNull { export ->
        (export.value as? ExternalValue.Function)?.address
    }

    if (!validAddresses.contains(address)) {
        return Error(
            ChasmError.ExecutionError(InvocationError.FunctionNotFound("Function with address: ${address.address}").toString()),
        )
    }

    return invoker(instance.config, store.store, address, args)
        .mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)
}
