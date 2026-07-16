package io.github.charlietap.chasm.executor.invoker.drop

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.component.ComponentCallScopeException
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.instance.ComponentRuntimeState
import io.github.charlietap.chasm.runtime.component.resource.ResourceTableException
import io.github.charlietap.chasm.runtime.component.store.ComponentStore
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.store.Store

internal typealias ComponentResourceTableDropper = (
    RuntimeConfig,
    Store,
    ComponentStore,
    ComponentRootAddress,
    ComponentRuntimeState,
) -> Result<Unit, ComponentInvocationError>

fun ComponentResourceTableDropper(
    config: RuntimeConfig,
    store: Store,
    componentStore: ComponentStore,
    root: ComponentRootAddress,
    state: ComponentRuntimeState,
): Result<Unit, ComponentInvocationError> = ComponentResourceTableDropper(
    config = config,
    store = store,
    componentStore = componentStore,
    root = root,
    state = state,
    representationDropper = ::ResourceRepresentationDropper,
)

internal inline fun ComponentResourceTableDropper(
    config: RuntimeConfig,
    store: Store,
    componentStore: ComponentStore,
    root: ComponentRootAddress,
    state: ComponentRuntimeState,
    crossinline representationDropper: ResourceRepresentationDropper,
): Result<Unit, ComponentInvocationError> {
    var failure: ComponentInvocationError? = null

    for (index in state.states.handleTables.lastIndex downTo 0) {
        val table = state.states.handleTables[index] ?: continue
        table.cleanup { type, representation ->
            try {
                representationDropper(
                    componentStore,
                    root,
                    state,
                    type,
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
        }
        state.states.handleTables[index] = null
    }

    return failure?.let(::Err) ?: Ok(Unit)
}
