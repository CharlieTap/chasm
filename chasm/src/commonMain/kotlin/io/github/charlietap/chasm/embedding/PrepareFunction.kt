package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.PreparedFunction
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.value.ExecutionValue

fun prepareFunction(
    store: Store,
    instance: Instance,
    name: String,
): ChasmResult<PreparedFunction, ChasmError.ExecutionError> {
    if (instance.instance.deallocated) {
        return Error(ChasmError.ExecutionError(InvocationError.InvocationOfADeinstantiatedInstance.toString()))
    }

    val extern = instance.instance.exports.firstOrNull { export ->
        export.name.name == name
    }?.value
    val address = (extern as? ExternalValue.Function)?.address ?: return Error(
        ChasmError.ExecutionError(InvocationError.FunctionNotFound(name).toString()),
    )

    val function = try {
        store.store.function(address)
    } catch (exception: InvocationException) {
        return Error(ChasmError.ExecutionError(exception.error.toString()))
    }

    return Success(
        PreparedFunction { args ->
            invokePrepared(store, instance, address, function, args)
        },
    )
}

private fun invokePrepared(
    store: Store,
    instance: Instance,
    address: Address.Function,
    function: FunctionInstance,
    args: List<ExecutionValue>,
): ChasmResult<List<ExecutionValue>, ChasmError.ExecutionError> {
    if (instance.instance.deallocated) {
        return Error(ChasmError.ExecutionError(InvocationError.InvocationOfADeinstantiatedInstance.toString()))
    }

    val currentFunction = try {
        store.store.function(address)
    } catch (exception: InvocationException) {
        return Error(ChasmError.ExecutionError(exception.error.toString()))
    }
    if (currentFunction !== function) {
        return Error(ChasmError.ExecutionError(InvocationError.FunctionLookupFailed(address).toString()))
    }

    return FunctionInvoker(instance.config, store.store, instance.instance, function, args)
        .mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)
}
