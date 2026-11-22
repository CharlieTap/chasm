package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.embedding.diagnostic.invocationError
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.ext.owning
import io.github.charlietap.chasm.embedding.internal.argumentMismatch
import io.github.charlietap.chasm.embedding.internal.exportedFunction
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.PreparedFunction
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.embedding.error.InvocationError as EmbeddingInvocationError

fun prepareFunction(
    store: Store,
    instance: Instance,
    name: String,
): ChasmResult<PreparedFunction, ExecutionError.Rejected> {

    val exported = when (val result = exportedFunction(store, instance, name)) {
        is Success -> result.result
        is Error -> return result
    }

    return Success(
        PreparedFunction { args ->
            invokePrepared(store, instance, exported.address, exported.function, args)
        },
    )
}

private fun invokePrepared(
    store: Store,
    instance: Instance,
    address: Address.Function,
    function: FunctionInstance,
    args: List<ExecutionValue>,
): ChasmResult<List<ExecutionValue>, EmbeddingInvocationError<ReferenceValue.Exception>> {

    if (instance.instance.deallocated) {
        return Error(ExecutionError.Rejected(InvocationError.InvocationOfADeinstantiatedInstance.toString()))
    }

    val runtime = try {

        val runtime = store.owning(instance.store)
        if (runtime.function(address) !== function) {
            throw InvocationException(InvocationError.FunctionLookupFailed(address))
        }

        runtime
    } catch (exception: InvocationException) {
        return Error(ExecutionError.Rejected(exception.error.toString()))
    }

    argumentMismatch(function.functionType, args)?.let { mismatch ->
        return Error(ExecutionError.Rejected(mismatch))
    }

    val result = try {
        FunctionInvoker(instance.config, runtime, instance.instance, function, args)
    } catch (exception: InvocationException) {
        Err(exception.error)
    }

    return result.fold(::Success) { error -> Error(invocationError(store, error)) }
}
