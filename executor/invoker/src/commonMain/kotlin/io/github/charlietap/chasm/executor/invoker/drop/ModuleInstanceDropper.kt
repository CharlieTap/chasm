package io.github.charlietap.chasm.executor.invoker.drop

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.data
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.global
import io.github.charlietap.chasm.runtime.ext.memory
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.function.Expression
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.store.instanceLifetimes
import io.github.charlietap.chasm.runtime.value.ExecutionValue

typealias ModuleInstanceDropper = (Store, ModuleInstance) -> Result<Unit, ModuleTrapError>

fun ModuleInstanceDropper(
    store: Store,
    instance: ModuleInstance,
): Result<Unit, ModuleTrapError> =
    ModuleInstanceDropper(
        store = store,
        instance = instance,
        memoryDropper = ::MemoryInstanceDropper,
        release = Release.Drop,
    )

fun ModuleInstanceRollback(
    store: Store,
    instance: ModuleInstance,
): Result<Unit, ModuleTrapError> =
    ModuleInstanceDropper(
        store = store,
        instance = instance,
        memoryDropper = ::MemoryInstanceDropper,
        release = Release.Rollback,
    )

fun ModuleInstanceTeardown(
    store: Store,
    instance: ModuleInstance,
): Result<Unit, ModuleTrapError> =
    ModuleInstanceDropper(
        store = store,
        instance = instance,
        memoryDropper = ::MemoryInstanceDropper,
        release = Release.Teardown,
    )

internal inline fun ModuleInstanceDropper(
    store: Store,
    instance: ModuleInstance,
    crossinline memoryDropper: MemoryInstanceDropper,
    release: Release,
): Result<Unit, ModuleTrapError> = binding {
    val lifetimes = store.instanceLifetimes()
    val allocation = when (release) {
        Release.Drop -> lifetimes.prepareDrop(instance)
        Release.Rollback -> lifetimes.prepareRollback(instance)
        Release.Teardown -> lifetimes.prepareTeardown(instance)
    }.bind()
        ?: return@binding Unit

    allocation.dataAddresses.asReversed().forEach { address ->
        store.data(address).bytes = ubyteArrayOf()
    }
    allocation.elementAddresses.asReversed().forEach { address ->
        store.element(address).elements = longArrayOf()
    }
    allocation.globalAddresses.asReversed().forEach { address ->
        store.global(address).value = ExecutionValue.Uninitialised.toLongFromBoxed()
    }
    allocation.memoryAddresses.asReversed().forEach { address ->
        memoryDropper(store.memory(address))
    }
    allocation.tableAddresses.asReversed().forEach { address ->
        store.table(address).elements = longArrayOf()
    }
    allocation.functionAddresses.asReversed().forEach { address ->
        val function = store.function(address)
        if (function is FunctionInstance.WasmFunction) {
            function.function = function.function.copy(
                body = Expression(arrayOf(DEAD_FUNCTION)),
            )
        }
    }
    allocation.instructionAddresses.asReversed().forEach { address ->
        store.instructions[address.address] = DEAD_FUNCTION
    }

    instance.dataAddresses.clear()
    instance.elemAddresses.clear()
    instance.exports.clear()
    instance.functionAddresses.clear()
    instance.globalAddresses.clear()
    instance.memAddresses.clear()
    instance.tableAddresses.clear()
    instance.tagAddresses.clear()

    lifetimes.completeDrop(instance)
}

private val DEAD_FUNCTION = DispatchableInstruction { _, _, _, _ ->
    throw InvocationException(InvocationError.InvocationOfADeinstantiatedInstance)
}

internal enum class Release {
    Drop,
    Rollback,
    Teardown,
}
