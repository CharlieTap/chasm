package io.github.charlietap.chasm.embedding.compiled.internal

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedStore
import io.github.charlietap.chasm.runtime.compiled.gc.enterObjects
import io.github.charlietap.chasm.runtime.compiled.gc.takeException
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ExecutionValue

internal fun runStartFunction(
    config: RuntimeConfig,
    store: Store,
    instance: ModuleInstance,
    address: Address.Function,
    arguments: List<ExecutionValue>,
): Result<List<ExecutionValue>, InvocationError> {

    val function = store.functions[address.address]
    val pending = (store.referenceStorage as ManagedStore).pending
    val context = ExecutionContext(ValueStack(1, store.maxStackSlots), store, instance, config)
    val error = enterObjects(context, function, LongArray(0), arrayOfNulls(0), pending)

    if (error == InvocationError.ThrownException) {
        pending.publish(takeException(context))
    }

    return if (error == null) Ok(emptyList()) else Err(error)
}
