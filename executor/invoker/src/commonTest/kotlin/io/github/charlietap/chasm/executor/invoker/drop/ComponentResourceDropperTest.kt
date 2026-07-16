package io.github.charlietap.chasm.executor.invoker.drop

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentRuntimeState
import io.github.charlietap.chasm.fixture.runtime.component.instance.runtimeComponentInstance
import io.github.charlietap.chasm.fixture.runtime.component.resource.guestRuntimeResourceType
import io.github.charlietap.chasm.fixture.runtime.component.resource.hostRuntimeResourceType
import io.github.charlietap.chasm.fixture.runtime.component.store.componentStore
import io.github.charlietap.chasm.fixture.runtime.component.value.ownComponentResourceValue
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.store.identity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ComponentResourceDropperTest {

    @Test
    fun `dropping an owned host resource releases its handle and payload`() {
        val store = store()
        val componentStore = componentStore()
        var destroyed: Any? = null
        val type = componentStore.resourceTypes.define(
            hostRuntimeResourceType { value ->
                destroyed = value
                Ok(Unit)
            },
        )
        val payloads = componentStore.hostResourcePayloads()
        val representation = payloads.insert("payload")
        val handle = componentStore.hostResourceHandles.insertOwn(type, representation)
        val resource = ownComponentResourceValue(store.identity(), handle)
        val representationDropper: ResourceRepresentationDropper = { _, _, _, _, _, _, _ ->
            error("host resource disposal must not invoke a guest destructor")
        }

        val result = ComponentResourceDropper(
            store = store,
            componentStore = componentStore,
            resource = resource,
            representationDropper = representationDropper,
        )
        val actual = HostResourceDropObservation(
            result = result,
            handles = componentStore.hostResourceHandles.size,
            payloads = payloads.size,
            destroyed = destroyed,
        )

        val expected = HostResourceDropObservation(
            result = Ok(Unit),
            handles = 0,
            payloads = 0,
            destroyed = "payload",
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `dropping an owned guest resource removes the handle before invoking its destructor`() {
        val store = store()
        val componentStore = componentStore()
        val state = componentRuntimeState()
        val config = runtimeConfig(debugInfo = true)
        val root = componentStore.reserveRoot(state, config)
        componentStore.publishRoot(root, runtimeComponentInstance(config = config, state = state))
        val type = componentStore.resourceTypes.define(guestRuntimeResourceType(root = root))
        val handle = componentStore.hostResourceHandles.insertOwn(type, 42)
        val resource = ownComponentResourceValue(store.identity(), handle)
        var destructorCalls = 0
        var destructor: GuestDestructorObservation? = null
        val representationDropper: ResourceRepresentationDropper = {
            actualComponentStore,
            actualRoot,
            actualState,
            actualType,
            representation,
            actualStore,
            actualConfig,
            ->
            destructor = GuestDestructorObservation(
                componentStore = actualComponentStore,
                root = actualRoot,
                state = actualState,
                type = actualType,
                representation = representation,
                store = actualStore,
                config = actualConfig,
                liveHandles = componentStore.hostResourceHandles.size,
            )
            destructorCalls += 1
        }

        val first = ComponentResourceDropper(
            store = store,
            componentStore = componentStore,
            resource = resource,
            representationDropper = representationDropper,
        )
        val second = ComponentResourceDropper(
            store = store,
            componentStore = componentStore,
            resource = resource,
            representationDropper = representationDropper,
        )
        val actual = GuestResourceDropObservation(
            first = first,
            second = second,
            destructorCalls = destructorCalls,
            destructor = destructor,
            activeScope = componentStore.currentCallScopeOrNull() != null,
        )

        val expected = GuestResourceDropObservation(
            first = Ok(Unit),
            second = Err(ComponentInvocationError.InvalidCanonicalValue("InvalidHandle")),
            destructorCalls = 1,
            destructor = GuestDestructorObservation(
                componentStore = componentStore,
                root = root,
                state = state,
                type = type,
                representation = 42,
                store = store,
                config = config,
                liveHandles = 0,
            ),
            activeScope = false,
        )
        assertEquals(expected, actual)
    }
}

private data class HostResourceDropObservation(
    val result: Any,
    val handles: Int,
    val payloads: Int,
    val destroyed: Any?,
)

private data class GuestResourceDropObservation(
    val first: Any,
    val second: Any,
    val destructorCalls: Int,
    val destructor: GuestDestructorObservation?,
    val activeScope: Boolean,
)

private data class GuestDestructorObservation(
    val componentStore: io.github.charlietap.chasm.runtime.component.store.ComponentStore,
    val root: io.github.charlietap.chasm.runtime.address.ComponentRootAddress,
    val state: io.github.charlietap.chasm.runtime.component.instance.ComponentRuntimeState,
    val type: io.github.charlietap.chasm.runtime.address.RuntimeResourceTypeAddress,
    val representation: Int,
    val store: io.github.charlietap.chasm.runtime.store.Store,
    val config: io.github.charlietap.chasm.config.RuntimeConfig,
    val liveHandles: Int,
)
