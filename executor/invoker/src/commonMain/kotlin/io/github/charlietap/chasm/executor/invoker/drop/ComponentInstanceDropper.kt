package io.github.charlietap.chasm.executor.invoker.drop

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.component.error.ComponentInstantiationError
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.store.ComponentStore
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.store.Store

typealias ComponentInstanceDropper = (
    Store,
    ComponentStore,
    ComponentRootAddress,
) -> Result<Unit, ComponentInstanceDropError>

fun ComponentInstanceDropper(
    store: Store,
    componentStore: ComponentStore,
    root: ComponentRootAddress,
): Result<Unit, ComponentInstanceDropError> = ComponentInstanceDropper(
    store = store,
    componentStore = componentStore,
    root = root,
    resourceTableDropper = ::ComponentResourceTableDropper,
    moduleInstanceDropper = ::ModuleInstanceDropper,
    force = false,
)

fun ComponentInstanceTeardown(
    store: Store,
    componentStore: ComponentStore,
    root: ComponentRootAddress,
): Result<Unit, ComponentInstanceDropError> = ComponentInstanceDropper(
    store = store,
    componentStore = componentStore,
    root = root,
    resourceTableDropper = ::ComponentResourceTableDropper,
    moduleInstanceDropper = ::ModuleInstanceTeardown,
    force = true,
)

internal inline fun ComponentInstanceDropper(
    store: Store,
    componentStore: ComponentStore,
    root: ComponentRootAddress,
    crossinline resourceTableDropper: ComponentResourceTableDropper = ::ComponentResourceTableDropper,
    crossinline moduleInstanceDropper: ModuleInstanceDropper,
    force: Boolean = false,
): Result<Unit, ComponentInstanceDropError> {
    val instance = (if (force) componentStore.retainedRoot(root) else componentStore.liveRoot(root)).fold(
        success = { it },
        failure = { return Err(ComponentInstanceDropError.ComponentInvocation(it)) },
    )
    if (componentStore.isRootActive(root)) {
        return Err(
            ComponentInstanceDropError.ComponentInvocation(
                ComponentInvocationError.InstanceActive,
            ),
        )
    }
    if (
        !force &&
        (componentStore.dependantCount(root) != 0 || componentStore.hasOwnedResources(root))
    ) {
        return Err(
            ComponentInstanceDropError.ComponentInvocation(
                ComponentInvocationError.InstanceHasDependants,
            ),
        )
    }

    var failure: ComponentInstanceDropError? = resourceTableDropper(
        instance.config,
        store,
        componentStore,
        root,
        instance.state,
    ).fold(
        success = { null },
        failure = { ComponentInstanceDropError.ComponentInvocation(it) },
    )

    if (!force && componentStore.hasOwnedResources(root)) {
        componentStore.retainLiveRoot(root).fold(
            success = {},
            failure = { error ->
                if (failure == null) failure = ComponentInstanceDropError.ComponentInvocation(error)
            },
        )
        return failure?.let(::Err) ?: Err(
            ComponentInstanceDropError.ComponentInvocation(
                ComponentInvocationError.InstanceHasDependants,
            ),
        )
    }

    instance.allocation.stackFunctions.asReversed().forEach { address ->
        StackFunctionRetirer(store, address)
    }

    instance.state.coreInstances.indices.reversed().forEach { index ->
        val coreInstance = instance.state.coreInstances[index] ?: return@forEach
        moduleInstanceDropper(store, coreInstance).fold(
            success = {},
            failure = { error ->
                if (failure == null) failure = ComponentInstanceDropError.CoreModule(error)
            },
        )
        instance.state.coreInstances[index] = null
    }

    componentStore.resourceTypes.discard(instance.allocation.resourceTypes)
    componentStore.markRootDead(root).fold(
        success = {},
        failure = { error ->
            if (failure == null) failure = ComponentInstanceDropError.ComponentInstantiation(error)
        },
    )

    return failure?.let(::Err) ?: Ok(Unit)
}

sealed interface ComponentInstanceDropError {

    data class ComponentInvocation(
        val error: ComponentInvocationError,
    ) : ComponentInstanceDropError

    data class ComponentInstantiation(
        val error: ComponentInstantiationError,
    ) : ComponentInstanceDropError

    data class CoreModule(
        val error: ModuleTrapError,
    ) : ComponentInstanceDropError
}
