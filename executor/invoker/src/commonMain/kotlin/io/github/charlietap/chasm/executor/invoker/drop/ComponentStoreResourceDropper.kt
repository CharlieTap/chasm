package io.github.charlietap.chasm.executor.invoker.drop

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.executor.invoker.component.ComponentCallScopeException
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.resource.ResourceTableException
import io.github.charlietap.chasm.runtime.component.resource.RuntimeResourceType
import io.github.charlietap.chasm.runtime.component.store.ComponentStore
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.store.Store

typealias ComponentStoreResourceDropper = (
    Store,
    ComponentStore,
) -> Result<Unit, ComponentInvocationError>

fun ComponentStoreResourceDropper(
    store: Store,
    componentStore: ComponentStore,
): Result<Unit, ComponentInvocationError> = ComponentStoreResourceDropper(
    store = store,
    componentStore = componentStore,
    representationDropper = ::ResourceRepresentationDropper,
)

internal inline fun ComponentStoreResourceDropper(
    store: Store,
    componentStore: ComponentStore,
    crossinline representationDropper: ResourceRepresentationDropper,
): Result<Unit, ComponentInvocationError> {
    var failure: ComponentInvocationError? = null

    componentStore.hostResourceHandles.cleanup cleanup@{ typeAddress, representation ->
        when (val type = componentStore.resourceTypes[typeAddress]) {
            null -> if (failure == null) {
                failure = ComponentInvocationError.MissingCanonicalDependency("resource type")
            }
            is RuntimeResourceType.Host -> try {
                val payloads = componentStore.hostResourcePayloadsOrNull()
                if (payloads == null) {
                    if (failure == null) {
                        failure = ComponentInvocationError.MissingCanonicalDependency("host resource payload")
                    }
                    return@cleanup
                }
                type.destructor(payloads.remove(representation)).fold(
                    success = {},
                    failure = { error -> if (failure == null) failure = error },
                )
            } catch (exception: ResourceTableException) {
                if (failure == null) {
                    failure = ComponentInvocationError.InvalidCanonicalValue(exception.error.name)
                }
            }
            is RuntimeResourceType.Guest -> componentStore.runtimeState(type.root).fold(
                success = { state ->
                    val config = componentStore.runtimeConfig(type.root).fold(
                        success = { it },
                        failure = { error ->
                            if (failure == null) failure = error
                            return@cleanup
                        },
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
                    } catch (exception: InvocationException) {
                        if (failure == null) failure = ComponentInvocationError.CoreTrap(exception.error)
                    } catch (exception: ComponentCallScopeException) {
                        if (failure == null) failure = exception.error
                    } catch (exception: ResourceTableException) {
                        if (failure == null) {
                            failure = ComponentInvocationError.InvalidCanonicalValue(exception.error.name)
                        }
                    }
                },
                failure = { error ->
                    if (failure == null) failure = error
                },
            )
        }
    }

    return failure?.let(::Err) ?: Ok(Unit)
}
