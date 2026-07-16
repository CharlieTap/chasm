package io.github.charlietap.chasm.runtime.component.store

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.fixture.runtime.component.address.componentRootAddress
import io.github.charlietap.chasm.fixture.runtime.component.error.invalidRootTransitionComponentInstantiationError
import io.github.charlietap.chasm.fixture.runtime.component.error.rootDeadComponentInvocationError
import io.github.charlietap.chasm.fixture.runtime.component.error.rootInitializingComponentInvocationError
import io.github.charlietap.chasm.fixture.runtime.component.error.rootProviderUnavailableComponentInstantiationError
import io.github.charlietap.chasm.fixture.runtime.component.error.rootRuntimeStateMismatchComponentInstantiationError
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentAllocation
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentRuntimeState
import io.github.charlietap.chasm.fixture.runtime.component.instance.runtimeComponentInstance
import io.github.charlietap.chasm.fixture.runtime.component.store.componentStore
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentStoreTest {

    @Test
    fun `a reserved root publishes the same runtime state and then becomes live`() {
        val state = componentRuntimeState()
        val instance = runtimeComponentInstance(state = state)
        val subject = componentStore()
        val address = subject.reserveRoot(state)

        val beforePublish = subject.liveRoot(address)
        val publish = subject.publishRoot(address, instance)
        val afterPublish = subject.liveRoot(address)
        val liveSlot = subject.root(address)
        val markDead = subject.markRootDead(address)
        val afterDrop = subject.liveRoot(address)
        val actual = ComponentRootLifecycleObservation(
            beforePublish = beforePublish,
            publish = publish,
            afterPublish = afterPublish,
            publishedSameInstance = (liveSlot as? ComponentRootSlot.Live)?.instance === instance,
            markDead = markDead,
            afterDrop = afterDrop,
        )

        val expected = ComponentRootLifecycleObservation(
            beforePublish = Err(rootInitializingComponentInvocationError(address)),
            publish = Ok(Unit),
            afterPublish = Ok(instance),
            publishedSameInstance = true,
            markDead = Ok(Unit),
            afterDrop = Err(rootDeadComponentInvocationError(address)),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `a dead root remains reserved and cannot be republished`() {
        val state = componentRuntimeState()
        val instance = runtimeComponentInstance(state = state)
        val subject = componentStore()
        val address = subject.reserveRoot(state)
        subject.markRootDead(address)

        val actual = DeadComponentRootObservation(
            publish = subject.publishRoot(address, instance),
            nextAddress = subject.reserveRoot(componentRuntimeState()),
            root = subject.root(address),
        )

        val expected = DeadComponentRootObservation(
            publish = Err(
                invalidRootTransitionComponentInstantiationError(
                    address = address,
                    current = ComponentRootState.Dead,
                    target = ComponentRootState.Live,
                ),
            ),
            nextAddress = componentRootAddress(1),
            root = ComponentRootSlot.Dead,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `publication rejects a runtime instance built from another reserved state`() {
        val state = componentRuntimeState()
        val instance = runtimeComponentInstance(state = componentRuntimeState())
        val subject = componentStore()
        val address = subject.reserveRoot(state)

        val actual = ComponentRootStateMismatchObservation(
            publish = subject.publishRoot(address, instance),
            retainedSameState = (subject.root(address) as? ComponentRootSlot.Initializing)?.state === state,
        )

        val expected = ComponentRootStateMismatchObservation(
            publish = Err(rootRuntimeStateMismatchComponentInstantiationError(address)),
            retainedSameState = true,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `deallocating roots retires initializing and live component state`() {
        val initializingState = componentRuntimeState()
        val liveState = componentRuntimeState()
        val instance = runtimeComponentInstance(state = liveState)
        val subject = componentStore()
        subject.reserveRoot(initializingState)
        val liveAddress = subject.reserveRoot(liveState)
        subject.publishRoot(liveAddress, instance)

        subject.deallocateRoots()
        val actual = DeallocatedComponentRootsObservation(
            statesDeallocated = listOf(initializingState.deallocated, liveState.deallocated),
            roots = subject.roots,
        )

        val expected = DeallocatedComponentRootsObservation(
            statesDeallocated = listOf(true, true),
            roots = listOf(ComponentRootSlot.Dead, ComponentRootSlot.Dead),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `publication retains resource providers until the dependant dies`() {
        val subject = componentStore()
        val providerState = componentRuntimeState()
        val provider = subject.reserveRoot(providerState)
        subject.publishRoot(provider, runtimeComponentInstance(state = providerState))
        val dependantState = componentRuntimeState()
        val dependant = subject.reserveRoot(
            state = dependantState,
            rootProviders = setOf(provider),
        )
        val dependantInstance = runtimeComponentInstance(
            state = dependantState,
            allocation = componentAllocation(rootProviders = setOf(provider)),
        )

        val retainedWhileInitializing = subject.dependantCount(provider)
        val publish = subject.publishRoot(dependant, dependantInstance)
        val retained = subject.dependantCount(provider)
        val drop = subject.markRootDead(dependant)
        val actual = ResourceProviderLifecycleObservation(
            retainedWhileInitializing = retainedWhileInitializing,
            publish = publish,
            retained = retained,
            drop = drop,
            retainedAfterDrop = subject.dependantCount(provider),
        )

        val expected = ResourceProviderLifecycleObservation(
            retainedWhileInitializing = 1,
            publish = Ok(Unit),
            retained = 1,
            drop = Ok(Unit),
            retainedAfterDrop = 0,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `failed publication does not retain resource providers`() {
        val subject = componentStore()
        val providerState = componentRuntimeState()
        val provider = subject.reserveRoot(providerState)
        subject.publishRoot(provider, runtimeComponentInstance(state = providerState))
        subject.markRootDead(provider)
        val dependantState = componentRuntimeState()
        val dependant = subject.reserveRoot(dependantState)
        val dependantInstance = runtimeComponentInstance(
            state = dependantState,
            allocation = componentAllocation(rootProviders = setOf(provider)),
        )

        val actual = FailedResourceProviderPublicationObservation(
            publish = subject.publishRoot(dependant, dependantInstance),
            retained = subject.dependantCount(provider),
            rootState = subject.root(dependant)?.rootState,
        )

        val expected = FailedResourceProviderPublicationObservation(
            publish = Err(rootProviderUnavailableComponentInstantiationError(provider)),
            retained = 0,
            rootState = ComponentRootState.Initializing,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `call scratch is reused by depth without sharing nested frames`() {
        val subject = componentStore()
        val outer = subject.enterCall()
        val nested = subject.enterCall()
        subject.exitCall()
        subject.exitCall()
        val reusedOuter = subject.enterCall()
        subject.exitCall()

        val actual = ScratchReuseObservation(
            nestedIsDistinct = nested !== outer,
            outerWasReused = reusedOuter === outer,
        )

        val expected = ScratchReuseObservation(
            nestedIsDistinct = true,
            outerWasReused = true,
        )
        assertEquals(expected, actual)
    }
}

private data class ComponentRootLifecycleObservation(
    val beforePublish: Any,
    val publish: Any,
    val afterPublish: Any,
    val publishedSameInstance: Boolean,
    val markDead: Any,
    val afterDrop: Any,
)

private data class DeadComponentRootObservation(
    val publish: Any,
    val nextAddress: Any,
    val root: Any?,
)

private data class ComponentRootStateMismatchObservation(
    val publish: Any,
    val retainedSameState: Boolean,
)

private data class DeallocatedComponentRootsObservation(
    val statesDeallocated: List<Boolean>,
    val roots: List<ComponentRootSlot>,
)

private data class ScratchReuseObservation(
    val nestedIsDistinct: Boolean,
    val outerWasReused: Boolean,
)

private data class ResourceProviderLifecycleObservation(
    val retainedWhileInitializing: Int,
    val publish: Any,
    val retained: Int,
    val drop: Any,
    val retainedAfterDrop: Int,
)

private data class FailedResourceProviderPublicationObservation(
    val publish: Any,
    val retained: Int,
    val rootState: ComponentRootState?,
)
