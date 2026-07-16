package io.github.charlietap.chasm.executor.invoker.component.resource

import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.invoker.RawFunctionInvoker
import io.github.charlietap.chasm.executor.invoker.component.ComponentCallScopeException
import io.github.charlietap.chasm.executor.invoker.component.asCoreTrap
import io.github.charlietap.chasm.executor.invoker.component.enterComponentInstance
import io.github.charlietap.chasm.executor.invoker.component.exitComponentCall
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.address.RuntimeResourceTypeAddress
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.instance.ComponentRuntimeState
import io.github.charlietap.chasm.runtime.component.resource.CanonicalResourceFunction
import io.github.charlietap.chasm.runtime.component.resource.CanonicalResourceFunctionKind
import io.github.charlietap.chasm.runtime.component.resource.ResourceTableException
import io.github.charlietap.chasm.runtime.component.resource.RuntimeResourceType
import io.github.charlietap.chasm.runtime.component.store.ComponentCallScope
import io.github.charlietap.chasm.runtime.component.store.ComponentStore
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.function.StackFunctionBody
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

fun CanonicalResourceFunctionBody(
    componentStore: ComponentStore,
    root: ComponentRootAddress,
    state: ComponentRuntimeState,
    function: CanonicalResourceFunction,
): StackFunctionBody = StackFunctionBody { vstack, _, store, execution ->
    val activeScope = componentStore.currentCallScopeOrNull()
    if (activeScope != null) {
        invokeCanonicalResourceFunction(
            componentStore,
            root,
            state,
            function,
            activeScope,
            vstack,
            store,
            execution.config,
        )
    } else {
        val scope = componentStore.enterCall(root, function.owner, function.owner)
        val cleanupError: ComponentInvocationError?
        try {
            invokeCanonicalResourceFunction(
                componentStore,
                root,
                state,
                function,
                scope,
                vstack,
                store,
                execution.config,
            )
        } finally {
            cleanupError = exitComponentCall(componentStore, scope)
        }
        cleanupError?.asCoreTrap()
    }
}

private inline fun invokeCanonicalResourceFunction(
    componentStore: ComponentStore,
    root: ComponentRootAddress,
    state: ComponentRuntimeState,
    function: CanonicalResourceFunction,
    scope: ComponentCallScope,
    vstack: ValueStack,
    store: Store,
    config: RuntimeConfig,
) {
    val owner = function.owner.index
    if (
        function.kind != CanonicalResourceFunctionKind.ResourceRep &&
        !state.states.mayLeave[owner]
    ) {
        ComponentInvocationError.InvalidCanonicalValue(
            "canonical resource operation may not leave the component instance",
        ).asCoreTrap()
    }

    val address = state.resourceTypes.getOrNull(function.resourceType.index)
        ?.takeIf { value -> value >= 0 }
        ?.let(::RuntimeResourceTypeAddress)
        ?: ComponentInvocationError.MissingCanonicalDependency("resource type").asCoreTrap()
    val table = state.states.handleTable(function.owner)

    try {
        when (function.kind) {
            CanonicalResourceFunctionKind.ResourceNew -> {
                val representation = vstack.getFrameSlot(0).toInt()
                vstack.setFrameSlot(0, table.insertOwn(address, representation).toLong())
            }
            CanonicalResourceFunctionKind.ResourceRep -> {
                val handle = vstack.getFrameSlot(0).toInt()
                vstack.setFrameSlot(0, table.representation(handle, address).toLong())
            }
            CanonicalResourceFunctionKind.ResourceDrop -> {
                val handle = vstack.getFrameSlot(0).toInt()
                if (table.isBorrow(handle)) {
                    val origin = table.removeBorrow(handle, address)
                    val originScope = componentStore.callScope(origin)
                        ?: ComponentInvocationError.InvalidCanonicalValue(
                            "canonical resource borrow has expired",
                        ).asCoreTrap()
                    originScope.consumeGuestBorrow()
                } else {
                    val representation = table.removeOwn(handle, address)
                    dropResourceRepresentation(
                        componentStore = componentStore,
                        root = root,
                        state = state,
                        address = address,
                        representation = representation,
                        store = store,
                        coreInvoker = ::RawFunctionInvoker,
                        config = config,
                    )
                }
            }
        }
    } catch (exception: ResourceTableException) {
        ComponentInvocationError.InvalidCanonicalValue(exception.error.name).asCoreTrap()
    }
}

internal fun dropResourceRepresentation(
    componentStore: ComponentStore,
    root: ComponentRootAddress,
    state: ComponentRuntimeState,
    address: RuntimeResourceTypeAddress,
    representation: Int,
    store: Store,
    coreInvoker: RawFunctionInvoker,
    config: RuntimeConfig,
) {
    when (val type = componentStore.resourceTypes[address]) {
        null -> ComponentInvocationError.MissingCanonicalDependency("resource type").asCoreTrap()
        is RuntimeResourceType.Host -> {
            val payloads = componentStore.hostResourcePayloadsOrNull()
                ?: ComponentInvocationError.MissingCanonicalDependency("host resource payload").asCoreTrap()
            val value = payloads.remove(representation)
            type.destructor(value).fold(
                success = {},
                failure = { error -> error.asCoreTrap() },
            )
        }
        is RuntimeResourceType.Guest -> {
            val destructor = type.destructor ?: return
            val destructorState = componentStore.runtimeState(type.root).fold(
                success = { it },
                failure = { it.asCoreTrap() },
            )
            val caller = if (type.root == root) {
                componentStore.currentCallScopeOrNull()
                    ?.takeIf { scope -> scope.root == type.root }
                    ?.calleeInstanceIndex
                    ?.takeIf { index -> index >= 0 }
                    ?.let { index -> io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex(index) }
            } else {
                null
            }
            val scope = componentStore.enterCall(type.root, caller, type.owner)
            try {
                enterComponentInstance(destructorState.states, caller, type.owner, scope)
                val arguments = scope.scratch.slots(1)
                arguments[0] = representation.toLong()
                coreInvoker(
                    config,
                    store,
                    destructor.instance,
                    destructor.function,
                    arguments,
                    1,
                    scope.scratch.callSlots(0),
                ).fold(
                    success = { resultCount ->
                        if (resultCount != 0) {
                            ComponentInvocationError.InvalidCanonicalValue(
                                "resource destructor must not return values",
                            ).asCoreTrap()
                        }
                    },
                    failure = { error -> ComponentInvocationError.CoreTrap(error).asCoreTrap() },
                )
            } catch (exception: InvocationException) {
                destructorState.states.poisoned[type.owner.index] = true
                throw exception
            } catch (exception: ComponentCallScopeException) {
                destructorState.states.poisoned[type.owner.index] = true
                throw exception
            } finally {
                val cleanupError = exitComponentCall(componentStore, scope)
                if (cleanupError != null) {
                    destructorState.states.poisoned[type.owner.index] = true
                    cleanupError.asCoreTrap()
                }
            }
        }
    }
}
