package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.embedding.diagnostic.invocationError
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.ext.owning
import io.github.charlietap.chasm.embedding.internal.argumentMismatch
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.embedding.error.InvocationError as EmbeddingInvocationError
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

fun invoke(
    store: Store,
    instance: Instance,
    function: Function,
    args: List<ExecutionValue> = [],
): ChasmResult<List<ExecutionValue>, EmbeddingInvocationError<ReferenceValue.Exception>> {
    return invoke(
        store = store,
        instance = instance,
        function = function,
        args = args,
        invoker = ::FunctionInvoker,
    )
}

fun invoke(
    store: Store,
    instance: Instance,
    name: String,
    args: List<ExecutionValue> = [],
): ChasmResult<List<ExecutionValue>, EmbeddingInvocationError<ReferenceValue.Exception>> {

    if (instance.instance.deallocated) {
        return Error(ExecutionError.Rejected(InvocationError.InvocationOfADeinstantiatedInstance.toString()))
    }

    val extern = instance.exportsByName[name]

    val address = (extern as? ExternalValue.Function)?.address ?: return Error(
        ExecutionError.Rejected(InvocationError.FunctionNotFound(name).toString()),
    )

    return invokeExported(
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
    args: List<ExecutionValue> = [],
    invoker: FunctionInvoker,
): ChasmResult<List<ExecutionValue>, EmbeddingInvocationError<ReferenceValue.Exception>> {
    return invoke(
        store = store,
        instance = instance,
        address = function.reference.address,
        args = args,
        invoker = invoker,
        handleStore = function.store,
    )
}

internal fun invoke(
    store: Store,
    instance: Instance,
    name: String,
    args: List<ExecutionValue> = [],
    invoker: FunctionInvoker,
): ChasmResult<List<ExecutionValue>, EmbeddingInvocationError<ReferenceValue.Exception>> {

    if (instance.instance.deallocated) {
        return Error(ExecutionError.Rejected(InvocationError.InvocationOfADeinstantiatedInstance.toString()))
    }

    val extern = instance.exportsByName[name]

    val address = (extern as? ExternalValue.Function)?.address ?: return Error(
        ExecutionError.Rejected(InvocationError.FunctionNotFound(name).toString()),
    )

    return invokeExported(
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
    handleStore: RuntimeStore = instance.store,
): ChasmResult<List<ExecutionValue>, EmbeddingInvocationError<ReferenceValue.Exception>> {

    if (instance.instance.deallocated) {
        return Error(ExecutionError.Rejected(InvocationError.InvocationOfADeinstantiatedInstance.toString()))
    }

    val functionNotExported = instance.instance.exports.none { export ->
        (export.value as? ExternalValue.Function)?.address == address
    }
    if (functionNotExported) {
        return Error(
            ExecutionError.Rejected(InvocationError.FunctionNotFound("Function with address: ${address.address}").toString()),
        )
    }

    return invokeExported(store, instance, address, args, invoker, handleStore)
}

private inline fun invokeExported(
    store: Store,
    instance: Instance,
    address: Address.Function,
    args: List<ExecutionValue>,
    invoker: FunctionInvoker,
    handleStore: RuntimeStore = instance.store,
): ChasmResult<List<ExecutionValue>, EmbeddingInvocationError<ReferenceValue.Exception>> {

    val (runtime, function) = try {
        store.owning(handleStore)
        val runtime = store.owning(instance.store)
        runtime to runtime.function(address)
    } catch (exception: InvocationException) {
        return Error(ExecutionError.Rejected(exception.error.toString()))
    }

    argumentMismatch(function.functionType, args)?.let { mismatch ->
        return Error(ExecutionError.Rejected(mismatch))
    }

    val result = try {
        invoker(instance.config, runtime, instance.instance, address, args)
    } catch (exception: InvocationException) {
        Err(exception.error)
    }

    return result.fold(::Success) { error -> Error(invocationError(store, error)) }
}
