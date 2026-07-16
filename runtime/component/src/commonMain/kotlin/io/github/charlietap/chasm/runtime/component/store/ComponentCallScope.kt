package io.github.charlietap.chasm.runtime.component.store

import io.github.charlietap.chasm.runtime.address.ComponentCallToken
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.address.HostResourceHandleId
import io.github.charlietap.chasm.runtime.address.StoreIdentity
import io.github.charlietap.chasm.runtime.component.function.RuntimeComponentHostFunctionContext
import io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex
import io.github.charlietap.chasm.runtime.component.instance.ComponentInstanceStates
import io.github.charlietap.chasm.runtime.component.resource.CanonicalHandleTable

class ComponentCallScope {

    val scratch = ComponentCallScratch()

    private var active = false

    var root: ComponentRootAddress = ComponentRootAddress(ABSENT_ROOT)
        private set

    var callToken: ComponentCallToken = ComponentCallToken(ABSENT_CALL_TOKEN)
        private set

    var callerInstanceIndex: Int = HOST_INSTANCE
        private set

    var calleeInstanceIndex: Int = HOST_INSTANCE
        private set

    val isHostCaller: Boolean
        get() = callerInstanceIndex == HOST_INSTANCE

    val isHostCallee: Boolean
        get() = calleeInstanceIndex == HOST_INSTANCE

    private var guestLenderTables = arrayOfNulls<CanonicalHandleTable>(INITIAL_JOURNAL_CAPACITY)
    private var guestLenderHandles = IntArray(INITIAL_JOURNAL_CAPACITY)
    private var guestBorrowTables = arrayOfNulls<CanonicalHandleTable>(INITIAL_JOURNAL_CAPACITY)
    private var guestBorrowHandles = IntArray(INITIAL_JOURNAL_CAPACITY)
    private var hostLenderHandles = LongArray(INITIAL_JOURNAL_CAPACITY)
    private var hostBorrowHandles = LongArray(INITIAL_JOURNAL_CAPACITY)
    private var enteredInstances = IntArray(INITIAL_JOURNAL_CAPACITY)
    private var enteredInstanceStates = arrayOfNulls<ComponentInstanceStates>(INITIAL_JOURNAL_CAPACITY)

    var guestLenderCount: Int = 0
        private set

    var guestBorrowCount: Int = 0
        private set

    var guestBorrowJournalCount: Int = 0
        private set

    var hostLenderCount: Int = 0
        private set

    var hostBorrowCount: Int = 0
        private set

    var enteredInstanceCount: Int = 0
        private set

    private val mutableHostFunctionContext = RuntimeComponentHostFunctionContext(this)

    fun hostFunctionContext(
        store: StoreIdentity,
        root: ComponentRootAddress,
    ): RuntimeComponentHostFunctionContext = mutableHostFunctionContext.apply {
        configure(store, root)
    }

    fun recordGuestLender(
        table: CanonicalHandleTable,
        handle: Int,
    ) {
        if (guestLenderCount == guestLenderTables.size) growGuestLenders()
        guestLenderTables[guestLenderCount] = table
        guestLenderHandles[guestLenderCount] = handle
        guestLenderCount += 1
    }

    fun guestLenderTable(index: Int): CanonicalHandleTable = requireNotNull(guestLenderTables[index])

    fun guestLenderHandle(index: Int): Int = guestLenderHandles[index]

    fun recordGuestBorrow(
        table: CanonicalHandleTable,
        handle: Int,
    ) {
        if (guestBorrowJournalCount == guestBorrowTables.size) growGuestBorrows()
        guestBorrowTables[guestBorrowJournalCount] = table
        guestBorrowHandles[guestBorrowJournalCount] = handle
        guestBorrowJournalCount += 1
        guestBorrowCount += 1
    }

    fun guestBorrowTable(index: Int): CanonicalHandleTable = requireNotNull(guestBorrowTables[index])

    fun guestBorrowHandle(index: Int): Int = guestBorrowHandles[index]

    fun consumeGuestBorrow() {
        check(guestBorrowCount > 0)
        guestBorrowCount -= 1
    }

    fun recordHostLender(handle: HostResourceHandleId) {
        if (hostLenderCount == hostLenderHandles.size) {
            hostLenderHandles = hostLenderHandles.copyOf(hostLenderHandles.size shl 1)
        }
        hostLenderHandles[hostLenderCount] = handle.id.toLong()
        hostLenderCount += 1
    }

    fun hostLenderHandle(index: Int): HostResourceHandleId = HostResourceHandleId(hostLenderHandles[index].toULong())

    fun recordHostBorrow(handle: HostResourceHandleId) {
        if (hostBorrowCount == hostBorrowHandles.size) {
            hostBorrowHandles = hostBorrowHandles.copyOf(hostBorrowHandles.size shl 1)
        }
        hostBorrowHandles[hostBorrowCount] = handle.id.toLong()
        hostBorrowCount += 1
    }

    fun hostBorrowHandle(index: Int): HostResourceHandleId = HostResourceHandleId(hostBorrowHandles[index].toULong())

    fun consumeHostBorrow(handle: HostResourceHandleId) {
        var index = 0
        while (index < hostBorrowCount && hostBorrowHandles[index].toULong() != handle.id) {
            index += 1
        }
        if (index == hostBorrowCount) return
        hostBorrowCount -= 1
        hostBorrowHandles[index] = hostBorrowHandles[hostBorrowCount]
    }

    fun recordEnteredInstance(
        states: ComponentInstanceStates,
        instance: RuntimeComponentInstanceIndex,
    ) {
        if (enteredInstanceCount == enteredInstances.size) {
            enteredInstances = enteredInstances.copyOf(enteredInstances.size shl 1)
            enteredInstanceStates = enteredInstanceStates.copyOf(enteredInstanceStates.size shl 1)
        }
        enteredInstanceStates[enteredInstanceCount] = states
        enteredInstances[enteredInstanceCount] = instance.index
        enteredInstanceCount += 1
    }

    fun enteredInstance(index: Int): RuntimeComponentInstanceIndex = RuntimeComponentInstanceIndex(enteredInstances[index])

    fun enteredInstanceStates(index: Int): ComponentInstanceStates = requireNotNull(enteredInstanceStates[index])

    fun clearJournals() {
        guestLenderTables.fill(null, toIndex = guestLenderCount)
        guestBorrowTables.fill(null, toIndex = guestBorrowJournalCount)
        guestLenderCount = 0
        guestBorrowCount = 0
        guestBorrowJournalCount = 0
        hostLenderCount = 0
        hostBorrowCount = 0
        enteredInstanceStates.fill(null, toIndex = enteredInstanceCount)
        enteredInstanceCount = 0
    }

    internal fun enter(
        root: ComponentRootAddress,
        caller: RuntimeComponentInstanceIndex?,
        callee: RuntimeComponentInstanceIndex?,
        callToken: ComponentCallToken,
    ) {
        check(!active)
        active = true
        this.root = root
        this.callToken = callToken
        callerInstanceIndex = caller?.index ?: HOST_INSTANCE
        calleeInstanceIndex = callee?.index ?: HOST_INSTANCE
    }

    internal fun exit() {
        check(active)
        check(journalsEmpty())
        root = ComponentRootAddress(ABSENT_ROOT)
        callToken = ComponentCallToken(ABSENT_CALL_TOKEN)
        callerInstanceIndex = HOST_INSTANCE
        calleeInstanceIndex = HOST_INSTANCE
        active = false
    }

    private fun journalsEmpty(): Boolean =
        guestLenderCount == 0 &&
            guestBorrowCount == 0 &&
            guestBorrowJournalCount == 0 &&
            hostLenderCount == 0 &&
            hostBorrowCount == 0 &&
            enteredInstanceCount == 0

    private fun growGuestLenders() {
        val capacity = guestLenderTables.size shl 1
        guestLenderTables = guestLenderTables.copyOf(capacity)
        guestLenderHandles = guestLenderHandles.copyOf(capacity)
    }

    private fun growGuestBorrows() {
        val capacity = guestBorrowTables.size shl 1
        guestBorrowTables = guestBorrowTables.copyOf(capacity)
        guestBorrowHandles = guestBorrowHandles.copyOf(capacity)
    }
}

private const val INITIAL_JOURNAL_CAPACITY = 8
private const val HOST_INSTANCE = -1
private const val ABSENT_ROOT = -1
private const val ABSENT_CALL_TOKEN = 0uL
