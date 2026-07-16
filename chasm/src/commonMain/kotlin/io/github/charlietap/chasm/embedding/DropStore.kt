package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.invoker.drop.ComponentInstanceDropError
import io.github.charlietap.chasm.executor.invoker.drop.ComponentInstanceDropper
import io.github.charlietap.chasm.executor.invoker.drop.ComponentInstanceTeardown
import io.github.charlietap.chasm.executor.invoker.drop.ComponentStoreResourceDropper
import io.github.charlietap.chasm.executor.invoker.drop.MemoryInstanceDropper
import io.github.charlietap.chasm.executor.invoker.drop.ModuleInstanceDropper
import io.github.charlietap.chasm.executor.invoker.drop.ModuleInstanceTeardown
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.store.ComponentRootSlot
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.store.instanceLifetimesOrNull

fun dropStore(
    store: Store,
): ChasmResult<Unit, ChasmError.ExecutionError> {
    return dropStore(
        store = store,
        memoryDropper = ::MemoryInstanceDropper,
        instanceDropper = ::ModuleInstanceTeardown,
        componentInstanceDropper = ::ComponentInstanceTeardown,
        componentStoreResourceDropper = ::ComponentStoreResourceDropper,
    )
}

internal fun dropStore(
    store: Store,
    memoryDropper: MemoryInstanceDropper,
    instanceDropper: ModuleInstanceDropper,
    componentInstanceDropper: ComponentInstanceDropper = ::ComponentInstanceTeardown,
    componentStoreResourceDropper: ComponentStoreResourceDropper = ::ComponentStoreResourceDropper,
): ChasmResult<Unit, ChasmError.ExecutionError> {
    val componentStore = store.components
    if (componentStore?.hasActiveCalls() == true) {
        return Error(
            ChasmError.ExecutionError(
                ComponentInvocationError.InstanceActive.toString(),
            ),
        )
    }
    var resourceFailure: ComponentInvocationError? = null
    var componentFailure: ComponentInstanceDropError? = null

    if (componentStore != null) {
        componentStoreResourceDropper(store.store, componentStore).fold(
            success = {},
            failure = { resourceFailure = it },
        )
        for (index in componentStore.roots.lastIndex downTo 0) {
            val slot = componentStore.roots[index]
            if (slot !is ComponentRootSlot.Live && slot !is ComponentRootSlot.Retained) continue
            componentInstanceDropper(store.store, componentStore, ComponentRootAddress(index)).fold(
                success = {},
                failure = { error ->
                    if (componentFailure == null) componentFailure = error
                },
            )
        }
    }
    componentStore?.deallocateRoots()

    val coreStore = store.store
    val lifetimes = coreStore.instanceLifetimesOrNull()

    lifetimes?.allocatedInstancesNewestFirst()?.forEach { instance ->
        val result = instanceDropper(coreStore, instance)
        if (result.isErr) {
            return result
                .mapError(ModuleTrapError::toString)
                .mapError(ChasmError::ExecutionError)
                .fold(::Success, ::Error)
        }
    }

    coreStore.data.forEach { data ->
        data.bytes = ubyteArrayOf()
    }
    coreStore.data.clear()

    coreStore.exceptions.forEach { exception ->
        exception.fields = longArrayOf()
    }
    coreStore.exceptions.clear()

    coreStore.elements.forEach { element ->
        element.elements = longArrayOf()
    }
    coreStore.elements.clear()

    coreStore.functions.clear()
    coreStore.instructions.clear()

    coreStore.globals.forEach { global ->
        global.value = 0L
    }
    coreStore.globals.clear()

    coreStore.hosts.clear()

    coreStore.memories.forEachIndexed { index, memory ->
        if (lifetimes?.owns(Address.Memory(index)) != true) {
            memoryDropper(memory)
        }
    }
    coreStore.memories.clear()

    coreStore.tables.forEach { table ->
        table.elements = longArrayOf()
    }
    coreStore.tables.clear()

    coreStore.arrays.clear()

    coreStore.structs.clear()

    coreStore.tags.clear()

    coreStore.rttCache.clear()
    coreStore.heap.arrayReferencePool.clear()
    coreStore.heap.structReferencePool.clear()
    coreStore.heap.sizeInBytes = 0
    lifetimes?.clear()
    return resourceFailure
        ?.let(ComponentInvocationError::toString)
        ?.let(ChasmError::ExecutionError)
        ?.let(::Error)
        ?: componentFailure
            ?.let(ComponentInstanceDropError::toString)
            ?.let(ChasmError::ExecutionError)
            ?.let(::Error)
        ?: Success(Unit)
}
