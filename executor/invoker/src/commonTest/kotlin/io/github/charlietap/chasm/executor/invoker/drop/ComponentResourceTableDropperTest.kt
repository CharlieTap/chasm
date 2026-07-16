package io.github.charlietap.chasm.executor.invoker.drop

import com.github.michaelbull.result.Err
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.component.address.runtimeResourceTypeAddress
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentInstanceStates
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentRuntimeState
import io.github.charlietap.chasm.fixture.runtime.component.resource.canonicalHandleTable
import io.github.charlietap.chasm.fixture.runtime.component.store.componentStore
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.instance.ComponentRuntimeState
import io.github.charlietap.chasm.runtime.component.store.ComponentStore
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.store.Store
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentResourceTableDropperTest {

    @Test
    fun `drains guest handle tables in reverse instance order after a destructor traps`() {
        val firstType = runtimeResourceTypeAddress()
        val secondType = runtimeResourceTypeAddress(1)
        val firstTable = canonicalHandleTable().apply { insertOwn(firstType, 10) }
        val secondTable = canonicalHandleTable().apply { insertOwn(secondType, 20) }
        val states = componentInstanceStates(
            parents = intArrayOf(-1, -1),
            handleTables = arrayOf(firstTable, secondTable),
        )
        val state = componentRuntimeState(states = states)
        val store = store()
        val componentStore = componentStore()
        val root = componentStore.reserveRoot(state)
        val dropped = mutableListOf<ResourceDropCall>()
        val representationDropper: ResourceRepresentationDropper = {
            actualComponentStore,
            actualRoot,
            actualState,
            type,
            representation,
            actualStore,
            actualConfig,
            ->
            dropped += ResourceDropCall(
                componentStore = actualComponentStore,
                root = actualRoot,
                state = actualState,
                type = type.address,
                representation = representation,
                store = actualStore,
                config = actualConfig,
            )
            if (representation == 20) throw InvocationException(InvocationError.Unreachable)
        }
        val config = runtimeConfig()

        val result = ComponentResourceTableDropper(
            config = config,
            store = store,
            componentStore = componentStore,
            root = root,
            state = state,
            representationDropper = representationDropper,
        )
        val actual = ResourceTableDropObservation(
            result = result,
            dropped = dropped,
            tableSizes = listOf(firstTable.size, secondTable.size),
            retainedTables = states.handleTables.count { it != null },
            activeScope = componentStore.currentCallScopeOrNull() != null,
        )

        val expected = ResourceTableDropObservation(
            result = Err(ComponentInvocationError.CoreTrap(InvocationError.Unreachable)),
            dropped = listOf(
                ResourceDropCall(componentStore, root, state, 1, 20, store, config),
                ResourceDropCall(componentStore, root, state, 0, 10, store, config),
            ),
            tableSizes = listOf(0, 0),
            retainedTables = 0,
            activeScope = false,
        )
        assertEquals(expected, actual)
    }
}

private data class ResourceDropCall(
    val componentStore: ComponentStore,
    val root: ComponentRootAddress,
    val state: ComponentRuntimeState,
    val type: Int,
    val representation: Int,
    val store: Store,
    val config: RuntimeConfig,
)

private data class ResourceTableDropObservation(
    val result: Any,
    val dropped: List<ResourceDropCall>,
    val tableSizes: List<Int>,
    val retainedTables: Int,
    val activeScope: Boolean,
)
