package io.github.charlietap.chasm.executor.invoker.component.canonical

import io.github.charlietap.chasm.executor.invoker.RawFunctionInvoker
import io.github.charlietap.chasm.executor.invoker.component.exitComponentCall
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.component.address.componentRootAddress
import io.github.charlietap.chasm.fixture.runtime.component.canonical.canonicalValueTupleLayout
import io.github.charlietap.chasm.fixture.runtime.component.canonical.linearMemoryLayout
import io.github.charlietap.chasm.fixture.runtime.component.index.runtimeComponentInstanceIndex
import io.github.charlietap.chasm.fixture.runtime.component.index.runtimeResourceTypeIndex
import io.github.charlietap.chasm.fixture.runtime.component.info.componentRuntimeInfo
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentInstanceStates
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentRuntimeState
import io.github.charlietap.chasm.fixture.runtime.component.resource.hostRuntimeResourceType
import io.github.charlietap.chasm.fixture.runtime.component.store.componentStore
import io.github.charlietap.chasm.fixture.runtime.component.value.ownComponentResourceValue
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalLayoutKind
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalStringEncoding
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.store.identity
import kotlin.test.Test
import kotlin.test.assertEquals

class CanonicalResourceCodecTest {

    @Test
    fun `an owned host resource can be lent to a borrowed component parameter`() {
        val store = store()
        val componentStore = componentStore()
        val resourceType = componentStore.resourceTypes.define(hostRuntimeResourceType())
        val hostHandle = componentStore.hostResourceHandles.insertOwn(resourceType, RESOURCE_REPRESENTATION)
        val owner = runtimeComponentInstanceIndex()
        val states = componentInstanceStates(parents = intArrayOf(-1))
        val state = componentRuntimeState(
            resourceTypes = intArrayOf(resourceType.address),
            states = states,
        )
        val layout = linearMemoryLayout(
            kind = CanonicalLayoutKind.Borrow,
            resourceType = runtimeResourceTypeIndex(),
        )
        val tuple = canonicalValueTupleLayout(layouts = intArrayOf(0))
        val runtimeInfo = componentRuntimeInfo(linearMemoryLayouts = listOf(layout))
        val scope = componentStore.enterCall(componentRootAddress(), callee = owner)
        val context = CanonicalCallContext(
            config = runtimeConfig(),
            store = store,
            componentStore = componentStore,
            root = componentRootAddress(),
            owner = owner,
            runtimeInfo = runtimeInfo,
            state = state,
            encoding = CanonicalStringEncoding.Utf8,
            memorySlot = ABSENT_CANONICAL_SLOT,
            reallocSlot = ABSENT_CANONICAL_SLOT,
            scratch = scope.scratch,
            scope = scope,
            coreInvoker = ::RawFunctionInvoker,
        )
        val resource = ownComponentResourceValue(store.identity(), hostHandle)

        CanonicalComponentValueValidator(runtimeInfo, tuple, listOf(resource))
        val guestHandle = CanonicalResourceLowerer(context, layout, resource)
        val guestTable = states.handleTable(owner)
        guestTable.removeBorrow(guestHandle, resourceType)
        scope.consumeGuestBorrow()
        val cleanupError = exitComponentCall(componentStore, scope)
        val actual = ResourceBorrowObservation(
            representation = componentStore.hostResourceHandles.ownRepresentation(hostHandle, resourceType),
            guestHandleCount = guestTable.size,
            cleanupError = cleanupError,
        )

        val expected = ResourceBorrowObservation(
            representation = RESOURCE_REPRESENTATION,
            guestHandleCount = 0,
            cleanupError = null,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `a nested call can reborrow a borrow owned by its active parent call`() {
        val store = store()
        val componentStore = componentStore()
        val root = componentRootAddress()
        val resourceType = componentStore.resourceTypes.define(hostRuntimeResourceType())
        val owner = runtimeComponentInstanceIndex()
        val states = componentInstanceStates(parents = intArrayOf(-1))
        val state = componentRuntimeState(
            resourceTypes = intArrayOf(resourceType.address),
            states = states,
        )
        val layout = linearMemoryLayout(
            kind = CanonicalLayoutKind.Borrow,
            resourceType = runtimeResourceTypeIndex(),
        )
        val runtimeInfo = componentRuntimeInfo(linearMemoryLayouts = listOf(layout))
        val outerScope = componentStore.enterCall(root, callee = owner)
        val outerCallToken = outerScope.callToken
        val guestTable = states.handleTable(owner)
        val guestHandle = guestTable.insertBorrow(resourceType, RESOURCE_REPRESENTATION, outerScope.callToken)
        outerScope.recordGuestBorrow(guestTable, guestHandle)
        val innerScope = componentStore.enterCall(root, caller = owner, callee = owner)
        val context = CanonicalCallContext(
            config = runtimeConfig(),
            store = store,
            componentStore = componentStore,
            root = root,
            owner = owner,
            runtimeInfo = runtimeInfo,
            state = state,
            encoding = CanonicalStringEncoding.Utf8,
            memorySlot = ABSENT_CANONICAL_SLOT,
            reallocSlot = ABSENT_CANONICAL_SLOT,
            scratch = innerScope.scratch,
            scope = innerScope,
            coreInvoker = ::RawFunctionInvoker,
        )

        val resource = CanonicalResourceLifter(context, layout, guestHandle)
        val innerCleanup = exitComponentCall(componentStore, innerScope)
        val representation = guestTable.borrowRepresentation(guestHandle, resourceType)
        val origin = guestTable.removeBorrow(guestHandle, resourceType)
        outerScope.consumeGuestBorrow()
        val outerCleanup = exitComponentCall(componentStore, outerScope)
        val actual = NestedBorrowObservation(
            resourceIsBorrow = resource is io.github.charlietap.chasm.runtime.value.component.ComponentValue.Resource.Borrow,
            representation = representation,
            origin = origin,
            innerCleanup = innerCleanup,
            outerCleanup = outerCleanup,
        )

        val expected = NestedBorrowObservation(
            resourceIsBorrow = true,
            representation = RESOURCE_REPRESENTATION,
            origin = outerCallToken,
            innerCleanup = null,
            outerCleanup = null,
        )
        assertEquals(expected, actual)
    }
}

private data class ResourceBorrowObservation(
    val representation: Int,
    val guestHandleCount: Int,
    val cleanupError: ComponentInvocationError?,
)

private data class NestedBorrowObservation(
    val resourceIsBorrow: Boolean,
    val representation: Int,
    val origin: io.github.charlietap.chasm.runtime.address.ComponentCallToken,
    val innerCleanup: ComponentInvocationError?,
    val outerCleanup: ComponentInvocationError?,
)

private const val ABSENT_CANONICAL_SLOT = -1
private const val RESOURCE_REPRESENTATION = 42
