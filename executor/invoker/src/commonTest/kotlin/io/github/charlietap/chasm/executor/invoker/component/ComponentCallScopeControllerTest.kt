package io.github.charlietap.chasm.executor.invoker.component

import io.github.charlietap.chasm.fixture.runtime.component.address.componentRootAddress
import io.github.charlietap.chasm.fixture.runtime.component.address.runtimeResourceTypeAddress
import io.github.charlietap.chasm.fixture.runtime.component.function.componentEntryPolicy
import io.github.charlietap.chasm.fixture.runtime.component.instance.componentInstanceStates
import io.github.charlietap.chasm.fixture.runtime.component.resource.canonicalHandleTable
import io.github.charlietap.chasm.fixture.runtime.component.store.componentStore
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentCallScopeControllerTest {

    @Test
    fun `a lowered guest call enters only the path below its component caller`() {
        val states = componentInstanceStates(parents = intArrayOf(-1, 0))
        val componentStore = componentStore()
        val root = componentRootAddress()
        val parent = RuntimeComponentInstanceIndex(0)
        val child = RuntimeComponentInstanceIndex(1)
        val hostScope = componentStore.enterCall(root, caller = null, callee = parent)
        enterComponentInstance(states, componentEntryPolicy(parent.index), hostScope)

        val guestScope = componentStore.enterCall(root, caller = parent, callee = child)
        enterComponentInstance(states, componentEntryPolicy(child.index), guestScope)
        val duringGuestCall = states.mayEnter.toList()

        exitComponentCall(componentStore, guestScope)
        val afterGuestCall = states.mayEnter.toList()
        exitComponentCall(componentStore, hostScope)
        val afterHostCall = states.mayEnter.toList()

        val actual = EntryObservation(duringGuestCall, afterGuestCall, afterHostCall)
        val expected = EntryObservation(
            duringGuestCall = listOf(false, false),
            afterGuestCall = listOf(false, true),
            afterHostCall = listOf(true, true),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `cleanup retains consumed borrows in its append only journal`() {
        val componentStore = componentStore()
        val scope = componentStore.enterCall()
        val table = canonicalHandleTable()
        val type = runtimeResourceTypeAddress()
        val handle = table.insertBorrow(type, representation = 42, callToken = scope.callToken)
        scope.recordGuestBorrow(table, handle)
        table.removeBorrow(handle, type)
        scope.consumeGuestBorrow()

        val cleanupError = exitComponentCall(componentStore, scope)
        val actual = BorrowCleanupObservation(
            cleanupError = cleanupError,
            tableSize = table.size,
            liveBorrowCount = scope.guestBorrowCount,
            journalCount = scope.guestBorrowJournalCount,
        )

        val expected = BorrowCleanupObservation(
            cleanupError = null,
            tableSize = 0,
            liveBorrowCount = 0,
            journalCount = 0,
        )
        assertEquals(expected, actual)
    }
}

private data class EntryObservation(
    val duringGuestCall: List<Boolean>,
    val afterGuestCall: List<Boolean>,
    val afterHostCall: List<Boolean>,
)

private data class BorrowCleanupObservation(
    val cleanupError: ComponentInvocationError?,
    val tableSize: Int,
    val liveBorrowCount: Int,
    val journalCount: Int,
)
