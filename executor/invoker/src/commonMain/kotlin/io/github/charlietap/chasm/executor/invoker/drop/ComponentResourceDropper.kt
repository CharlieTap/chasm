package io.github.charlietap.chasm.executor.invoker.drop

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.RawFunctionInvoker
import io.github.charlietap.chasm.executor.invoker.component.ComponentCallScopeException
import io.github.charlietap.chasm.executor.invoker.component.resource.dropResourceRepresentation
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.address.RuntimeResourceTypeAddress
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.instance.ComponentRuntimeState
import io.github.charlietap.chasm.runtime.component.resource.ResourceTableException
import io.github.charlietap.chasm.runtime.component.resource.RuntimeResourceType
import io.github.charlietap.chasm.runtime.component.store.ComponentRootSlot
import io.github.charlietap.chasm.runtime.component.store.ComponentStore
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.store.identity
import io.github.charlietap.chasm.runtime.value.component.ComponentValue

typealias ComponentResourceDropper = (
    Store,
    ComponentStore,
    ComponentValue.Resource.Own,
) -> Result<Unit, ComponentInvocationError>

fun ComponentResourceDropper(
    store: Store,
    componentStore: ComponentStore,
    resource: ComponentValue.Resource.Own,
): Result<Unit, ComponentInvocationError> = ComponentResourceDropper(
    store = store,
    componentStore = componentStore,
    resource = resource,
    representationDropper = ::ResourceRepresentationDropper,
)

internal inline fun ComponentResourceDropper(
    store: Store,
    componentStore: ComponentStore,
    resource: ComponentValue.Resource.Own,
    crossinline representationDropper: ResourceRepresentationDropper,
): Result<Unit, ComponentInvocationError> {
    if (resource.store !== store.identity()) return Err(ComponentInvocationError.StoreMismatch)

    val typeAddress: RuntimeResourceTypeAddress
    val representation: Int
    try {
        typeAddress = componentStore.hostResourceHandles.ownType(resource.handle)
        representation = componentStore.hostResourceHandles.removeOwn(resource.handle, typeAddress)
    } catch (exception: ResourceTableException) {
        return Err(ComponentInvocationError.InvalidCanonicalValue(exception.error.name))
    }

    return when (val type = componentStore.resourceTypes[typeAddress]) {
        null -> Err(ComponentInvocationError.MissingCanonicalDependency("resource type"))
        is RuntimeResourceType.Host -> {
            try {
                val payloads = componentStore.hostResourcePayloadsOrNull()
                    ?: return Err(ComponentInvocationError.MissingCanonicalDependency("host resource payload"))
                type.destructor(payloads.remove(representation))
            } catch (exception: ResourceTableException) {
                Err(ComponentInvocationError.InvalidCanonicalValue(exception.error.name))
            }
        }
        is RuntimeResourceType.Guest -> {
            val state = componentStore.runtimeState(type.root).fold(
                success = { it },
                failure = { return Err(it) },
            )
            val config = componentStore.runtimeConfig(type.root).fold(
                success = { it },
                failure = { return Err(it) },
            )
            try {
                representationDropper(
                    componentStore,
                    type.root,
                    state,
                    typeAddress,
                    representation,
                    store,
                    config,
                )
                if (
                    componentStore.root(type.root) is ComponentRootSlot.Retained &&
                    !componentStore.hasOwnedResources(type.root)
                ) {
                    ComponentInstanceTeardown(store, componentStore, type.root).fold(
                        success = { Ok(Unit) },
                        failure = { error ->
                            Err(ComponentInvocationError.InstanceTeardownFailure(error.toString()))
                        },
                    )
                } else {
                    Ok(Unit)
                }
            } catch (exception: InvocationException) {
                Err(ComponentInvocationError.CoreTrap(exception.error))
            } catch (exception: ComponentCallScopeException) {
                Err(exception.error)
            } catch (exception: ResourceTableException) {
                Err(ComponentInvocationError.InvalidCanonicalValue(exception.error.name))
            }
        }
    }
}

internal typealias ResourceRepresentationDropper = (
    ComponentStore,
    ComponentRootAddress,
    ComponentRuntimeState,
    RuntimeResourceTypeAddress,
    Int,
    Store,
    RuntimeConfig,
) -> Unit

internal fun ResourceRepresentationDropper(
    componentStore: ComponentStore,
    root: ComponentRootAddress,
    state: ComponentRuntimeState,
    type: RuntimeResourceTypeAddress,
    representation: Int,
    store: Store,
    config: RuntimeConfig,
) {
    dropResourceRepresentation(
        componentStore = componentStore,
        root = root,
        state = state,
        address = type,
        representation = representation,
        store = store,
        coreInvoker = ::RawFunctionInvoker,
        config = config,
    )
}
