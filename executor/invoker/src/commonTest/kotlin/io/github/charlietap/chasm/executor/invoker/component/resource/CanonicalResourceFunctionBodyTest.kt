package io.github.charlietap.chasm.executor.invoker.component.resource

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.RawFunctionInvoker
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.component.address.componentRootAddress
import io.github.charlietap.chasm.fixture.runtime.component.index.runtimeComponentInstanceIndex
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentInstanceStates
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentRuntimeState
import io.github.charlietap.chasm.fixture.runtime.component.instance.runtimeComponentInstance
import io.github.charlietap.chasm.fixture.runtime.component.resource.guestRuntimeResourceType
import io.github.charlietap.chasm.fixture.runtime.component.resource.hostRuntimeResourceType
import io.github.charlietap.chasm.fixture.runtime.component.store.componentStore
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CanonicalResourceFunctionBodyTest {

    @Test
    fun `a trapping destructor poisons its component instance and unwinds the call scope`() {
        val store = store()
        val componentStore = componentStore()
        val states = componentInstanceStates(parents = intArrayOf(-1))
        val state = componentRuntimeState(states = states)
        val destructorInstance = moduleInstance()
        val root = componentStore.reserveRoot(state)
        componentStore.publishRoot(root, runtimeComponentInstance(state = state))
        val type = componentStore.resourceTypes.define(
            guestRuntimeResourceType(
                root = root,
                destructor = functionAddress(),
                destructorInstance = destructorInstance,
            ),
        )
        var invokedInstance: io.github.charlietap.chasm.runtime.instance.ModuleInstance? = null
        val coreInvoker: RawFunctionInvoker = { _, _, instance, _, _, _, _ ->
            invokedInstance = instance
            Err(InvocationError.Unreachable)
        }

        val exception = assertFailsWith<InvocationException> {
            dropResourceRepresentation(
                componentStore = componentStore,
                root = root,
                state = state,
                address = type,
                representation = 42,
                store = store,
                coreInvoker = coreInvoker,
                config = runtimeConfig(),
            )
        }
        val actual = TrappingDestructorObservation(
            error = exception.error,
            poisoned = states.poisoned.single(),
            activeCall = componentStore.currentCallScopeOrNull() != null,
            usesDeclaringInstance = invokedInstance === destructorInstance,
        )

        val expected = TrappingDestructorObservation(
            error = InvocationError.ComponentFunctionError(
                ComponentInvocationError.CoreTrap(InvocationError.Unreachable).toString(),
            ),
            poisoned = true,
            activeCall = false,
            usesDeclaringInstance = true,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `a destructor does not reuse a caller index from another component root`() {
        val store = store()
        val componentStore = componentStore()
        val providerStates = componentInstanceStates(parents = intArrayOf(-1))
        val providerState = componentRuntimeState(states = providerStates)
        val provider = componentStore.reserveRoot(providerState)
        componentStore.publishRoot(provider, runtimeComponentInstance(state = providerState))
        val consumerState = componentRuntimeState()
        val consumer = componentStore.reserveRoot(consumerState)
        componentStore.publishRoot(consumer, runtimeComponentInstance(state = consumerState))
        val type = componentStore.resourceTypes.define(
            guestRuntimeResourceType(
                root = provider,
                destructor = functionAddress(),
            ),
        )
        var invocation: DestructorInvocationObservation? = null
        val coreInvoker: RawFunctionInvoker = { _, _, _, _, values, valueCount, _ ->
            invocation = DestructorInvocationObservation(valueCount, values[0])
            Ok(0)
        }
        componentStore.enterCall(
            root = consumer,
            callee = runtimeComponentInstanceIndex(5),
        )

        dropResourceRepresentation(
            componentStore = componentStore,
            root = consumer,
            state = consumerState,
            address = type,
            representation = 42,
            store = store,
            coreInvoker = coreInvoker,
            config = runtimeConfig(),
        )
        val actual = CrossRootDestructorObservation(
            invocation = invocation,
            activeRoot = componentStore.currentCallScope().root,
            providerCanEnter = providerStates.mayEnter.single(),
        )
        componentStore.exitCall()

        val expected = CrossRootDestructorObservation(
            invocation = DestructorInvocationObservation(valueCount = 1, representation = 42),
            activeRoot = consumer,
            providerCanEnter = true,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `dropping a host resource from guest code invokes its registered destructor`() {
        val store = store()
        val componentStore = componentStore()
        val root = componentRootAddress()
        var destroyed: Any? = null
        val type = componentStore.resourceTypes.define(
            hostRuntimeResourceType { value ->
                destroyed = value
                Ok(Unit)
            },
        )
        val representation = componentStore.hostResourcePayloads().insert("payload")
        componentStore.enterCall(root)

        dropResourceRepresentation(
            componentStore = componentStore,
            root = root,
            state = componentRuntimeState(),
            address = type,
            representation = representation,
            store = store,
            coreInvoker = { _, _, _, _, _, _, _ -> error("host resource does not use a core destructor") },
            config = runtimeConfig(),
        )
        componentStore.exitCall()
        val actual = HostDestructorObservation(
            destroyed = destroyed,
            payloadCount = componentStore.hostResourcePayloads().size,
        )

        val expected = HostDestructorObservation(
            destroyed = "payload",
            payloadCount = 0,
        )
        assertEquals(expected, actual)
    }
}

private data class TrappingDestructorObservation(
    val error: InvocationError,
    val poisoned: Boolean,
    val activeCall: Boolean,
    val usesDeclaringInstance: Boolean,
)

private data class HostDestructorObservation(
    val destroyed: Any?,
    val payloadCount: Int,
)

private data class DestructorInvocationObservation(
    val valueCount: Int,
    val representation: Long,
)

private data class CrossRootDestructorObservation(
    val invocation: DestructorInvocationObservation?,
    val activeRoot: io.github.charlietap.chasm.runtime.address.ComponentRootAddress,
    val providerCanEnter: Boolean,
)
