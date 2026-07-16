package io.github.charlietap.chasm.executor.invoker.drop

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentRuntimeState
import io.github.charlietap.chasm.fixture.runtime.component.instance.runtimeComponentInstance
import io.github.charlietap.chasm.fixture.runtime.component.resource.guestRuntimeResourceType
import io.github.charlietap.chasm.fixture.runtime.component.resource.hostRuntimeResourceType
import io.github.charlietap.chasm.fixture.runtime.component.store.componentStore
import io.github.charlietap.chasm.fixture.runtime.store
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentStoreResourceDropperTest {

    @Test
    fun `drops store owned resources while guest origins are live`() {
        val store = store()
        val componentStore = componentStore()
        val state = componentRuntimeState()
        val config = runtimeConfig(debugInfo = true)
        val root = componentStore.reserveRoot(state, config)
        componentStore.publishRoot(root, runtimeComponentInstance(config = config, state = state))
        val hostType = componentStore.resourceTypes.define(hostRuntimeResourceType())
        val payloads = componentStore.hostResourcePayloads()
        val hostRepresentation = payloads.insert("payload")
        componentStore.hostResourceHandles.insertOwn(hostType, hostRepresentation)
        val guestType = componentStore.resourceTypes.define(guestRuntimeResourceType(root = root))
        componentStore.hostResourceHandles.insertOwn(guestType, 42)
        val dropped = mutableListOf<DroppedStoreResource>()
        val representationDropper: ResourceRepresentationDropper = { _, actualRoot, _, type, representation, _, actualConfig ->
            dropped += DroppedStoreResource(actualRoot.address, type.address, representation, actualConfig)
        }

        val result = ComponentStoreResourceDropper(
            store = store,
            componentStore = componentStore,
            representationDropper = representationDropper,
        )
        val actual = StoreResourceDropObservation(
            result = result,
            dropped = dropped,
            handles = componentStore.hostResourceHandles.size,
            payloads = payloads.size,
            rootLive = componentStore.liveRoot(root).isOk,
        )

        val expected = StoreResourceDropObservation(
            result = Ok(Unit),
            dropped = listOf(DroppedStoreResource(root = 0, type = 1, representation = 42, config = config)),
            handles = 0,
            payloads = 0,
            rootLive = true,
        )
        assertEquals(expected, actual)
    }
}

private data class DroppedStoreResource(
    val root: Int,
    val type: Int,
    val representation: Int,
    val config: io.github.charlietap.chasm.config.RuntimeConfig,
)

private data class StoreResourceDropObservation(
    val result: Any,
    val dropped: List<DroppedStoreResource>,
    val handles: Int,
    val payloads: Int,
    val rootLive: Boolean,
)
