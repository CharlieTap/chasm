package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.diagnostic.executionError
import io.github.charlietap.chasm.embedding.diagnostic.executionTrap
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.error.Trap
import io.github.charlietap.chasm.embedding.ext.owning
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

@InternalChasmApi
class ExportedFunction(
    val store: RuntimeStore,
    val instance: ModuleInstance,
    val config: RuntimeConfig,
    val address: Address.Function,
    val function: FunctionInstance,
)

@InternalChasmApi
fun exportedFunction(
    store: Store,
    instance: Instance,
    name: String,
): ChasmResult<ExportedFunction, ChasmError.ExecutionError> {

    if (instance.instance.deallocated) {
        return Error(ChasmError.ExecutionError(InvocationError.InvocationOfADeinstantiatedInstance.toString()))
    }

    val extern = instance.exportsByName[name]
    val address = (extern as? ExternalValue.Function)?.address ?: return Error(
        ChasmError.ExecutionError(InvocationError.FunctionNotFound(name).toString()),
    )

    return try {
        val runtime = store.owning(instance.store)
        Success(ExportedFunction(runtime, instance.instance, instance.config, address, runtime.function(address)))
    } catch (exception: InvocationException) {
        Error(ChasmError.ExecutionError(exception.error.toString()))
    }
}

@InternalChasmApi
fun functionInstance(
    store: Store,
    function: Function,
): FunctionInstance {
    return store.owning(function.store).function(function.reference.address)
}

@InternalChasmApi
fun functionAddress(
    store: Store,
    function: Function,
): Int {

    store.owning(function.store)

    return function.reference.address.address
}

@InternalChasmApi
fun exportedError(
    store: Store,
    error: InvocationError,
): ChasmError.ExecutionError {
    return executionError(store, error)
}

@InternalChasmApi
fun exportedTrap(
    store: Store,
    error: InvocationError,
): Trap {
    return executionTrap(store, error)
}

@InternalChasmApi
fun storeDropped(
    store: Store,
): Boolean {
    return store.isDropped
}
