package io.github.charlietap.chasm.runtime.component.resource

import io.github.charlietap.chasm.runtime.address.ComponentCallToken
import io.github.charlietap.chasm.runtime.address.HostResourceHandleId
import io.github.charlietap.chasm.runtime.address.RuntimeResourceTypeAddress

class HostResourceHandleTable(
    private val onOwnInserted: (RuntimeResourceTypeAddress) -> Unit = {},
    private val onOwnRemoved: (RuntimeResourceTypeAddress) -> Unit = {},
) {

    private var types = IntArray(INITIAL_CAPACITY)
    private var representations = IntArray(INITIAL_CAPACITY)
    private var flags = ByteArray(INITIAL_CAPACITY)
    private var lendCounts = IntArray(INITIAL_CAPACITY)
    private var borrowTokens = LongArray(INITIAL_CAPACITY)
    private var generations = IntArray(INITIAL_CAPACITY)
    private var nextFree = IntArray(INITIAL_CAPACITY)
    private var freeHead = ABSENT_INDEX
    private var nextUnused = 0
    private var cleaning = false

    var size: Int = 0
        private set

    fun insertOwn(
        type: RuntimeResourceTypeAddress,
        representation: Int,
    ): HostResourceHandleId {
        val index = allocate()
        types[index] = type.address
        representations[index] = representation
        flags[index] = OWN
        onOwnInserted(type)
        return id(index)
    }

    fun ownRepresentation(
        handle: HostResourceHandleId,
        type: RuntimeResourceTypeAddress,
    ): Int = representations[requireEntry(handle, type, OWN)]

    fun ownType(handle: HostResourceHandleId): RuntimeResourceTypeAddress {
        val index = requireHandle(handle)
        if (flags[index] != OWN) throw ResourceTableException(ResourceTableError.OwnershipMismatch)
        return RuntimeResourceTypeAddress(types[index])
    }

    fun removeOwn(
        handle: HostResourceHandleId,
        type: RuntimeResourceTypeAddress,
    ): Int {
        val index = requireEntry(handle, type, OWN)
        if (lendCounts[index] != 0) throw ResourceTableException(ResourceTableError.ResourceLent)
        val representation = representations[index]
        release(index)
        onOwnRemoved(type)
        return representation
    }

    fun lend(
        handle: HostResourceHandleId,
        type: RuntimeResourceTypeAddress,
    ): Int {
        val index = requireEntry(handle, type, OWN)
        lendCounts[index] += 1
        return representations[index]
    }

    fun undoLend(handle: HostResourceHandleId) {
        val index = requireHandle(handle)
        if (flags[index] != OWN || lendCounts[index] == 0) {
            throw ResourceTableException(ResourceTableError.OwnershipMismatch)
        }
        lendCounts[index] -= 1
    }

    fun insertBorrow(
        type: RuntimeResourceTypeAddress,
        representation: Int,
        callToken: ComponentCallToken,
    ): HostResourceHandleId {
        val index = allocate()
        types[index] = type.address
        representations[index] = representation
        borrowTokens[index] = callToken.token.toLong()
        flags[index] = BORROW
        return id(index)
    }

    fun borrowRepresentation(
        handle: HostResourceHandleId,
        type: RuntimeResourceTypeAddress,
        callToken: ComponentCallToken,
    ): Int {
        val index = requireEntry(handle, type, BORROW)
        requireCallToken(index, callToken)
        return representations[index]
    }

    fun removeBorrow(
        handle: HostResourceHandleId,
        type: RuntimeResourceTypeAddress,
        callToken: ComponentCallToken,
    ): Int {
        val index = requireEntry(handle, type, BORROW)
        requireCallToken(index, callToken)
        val representation = representations[index]
        release(index)
        return representation
    }

    fun removeBorrow(
        handle: HostResourceHandleId,
        callToken: ComponentCallToken,
    ): Int {
        val index = requireHandle(handle)
        if (flags[index] != BORROW) throw ResourceTableException(ResourceTableError.OwnershipMismatch)
        requireCallToken(index, callToken)
        val representation = representations[index]
        release(index)
        return representation
    }

    fun isBorrow(
        handle: HostResourceHandleId,
        callToken: ComponentCallToken,
    ): Boolean {
        val index = index(handle) ?: return false
        return flags[index] == BORROW && borrowTokens[index] == callToken.token.toLong()
    }

    fun hasOwn(predicate: (RuntimeResourceTypeAddress) -> Boolean): Boolean {
        repeat(nextUnused) { index ->
            if (flags[index] == OWN && predicate(RuntimeResourceTypeAddress(types[index]))) return true
        }
        return false
    }

    fun cleanup(onOwn: (RuntimeResourceTypeAddress, Int) -> Unit) {
        if (cleaning) throw ResourceTableException(ResourceTableError.TableUnavailable)
        val ownTypes = IntArray(size)
        val ownRepresentations = IntArray(size)
        var ownCount = 0
        repeat(nextUnused) { index ->
            if (flags[index] == OWN) {
                ownTypes[ownCount] = types[index]
                ownRepresentations[ownCount] = representations[index]
                ownCount += 1
            }
        }
        clear()
        repeat(ownCount) { index ->
            onOwnRemoved(RuntimeResourceTypeAddress(ownTypes[index]))
        }
        cleaning = true
        try {
            repeat(ownCount) { index ->
                onOwn(RuntimeResourceTypeAddress(ownTypes[index]), ownRepresentations[index])
            }
        } finally {
            cleaning = false
        }
    }

    private fun allocate(): Int {
        if (cleaning) throw ResourceTableException(ResourceTableError.TableUnavailable)
        val index = if (freeHead != ABSENT_INDEX) {
            freeHead.also { freeHead = nextFree[it] }
        } else {
            if (nextUnused == MAX_HANDLE_COUNT) {
                throw ResourceTableException(ResourceTableError.CapacityExhausted)
            }
            if (nextUnused == flags.size) grow()
            nextUnused++
            nextUnused - 1
        }
        generations[index] = nextGeneration(generations[index])
        size += 1
        return index
    }

    private fun release(index: Int) {
        flags[index] = FREE
        types[index] = 0
        representations[index] = 0
        lendCounts[index] = 0
        borrowTokens[index] = 0L
        nextFree[index] = freeHead
        freeHead = index
        size -= 1
    }

    private fun requireEntry(
        handle: HostResourceHandleId,
        type: RuntimeResourceTypeAddress,
        ownership: Byte,
    ): Int {
        val index = requireHandle(handle)
        if (types[index] != type.address) throw ResourceTableException(ResourceTableError.TypeMismatch)
        if (flags[index] != ownership) throw ResourceTableException(ResourceTableError.OwnershipMismatch)
        return index
    }

    private fun requireHandle(handle: HostResourceHandleId): Int = index(handle)
        ?: throw ResourceTableException(ResourceTableError.InvalidHandle)

    private fun index(handle: HostResourceHandleId): Int? {
        val encodedIndex = handle.id and INDEX_MASK
        if (encodedIndex == 0uL || encodedIndex > Int.MAX_VALUE.toULong()) return null
        val index = encodedIndex.toInt() - 1
        if (index !in 0 until nextUnused || flags[index] == FREE) return null
        val generation = (handle.id shr GENERATION_SHIFT).toUInt().toInt()
        return index.takeIf { generations[index] == generation }
    }

    private fun requireCallToken(
        index: Int,
        callToken: ComponentCallToken,
    ) {
        if (borrowTokens[index] != callToken.token.toLong()) {
            throw ResourceTableException(ResourceTableError.BorrowScopeMismatch)
        }
    }

    private fun id(index: Int): HostResourceHandleId = HostResourceHandleId(
        (generations[index].toUInt().toULong() shl GENERATION_SHIFT) or (index + 1).toUInt().toULong(),
    )

    private fun clear() {
        flags.fill(FREE, toIndex = nextUnused)
        types.fill(0, toIndex = nextUnused)
        representations.fill(0, toIndex = nextUnused)
        lendCounts.fill(0, toIndex = nextUnused)
        borrowTokens.fill(0L, toIndex = nextUnused)
        freeHead = ABSENT_INDEX
        nextUnused = 0
        size = 0
    }

    private fun grow() {
        val capacity = flags.size shl 1
        types = types.copyOf(capacity)
        representations = representations.copyOf(capacity)
        flags = flags.copyOf(capacity)
        lendCounts = lendCounts.copyOf(capacity)
        borrowTokens = borrowTokens.copyOf(capacity)
        generations = generations.copyOf(capacity)
        nextFree = nextFree.copyOf(capacity)
    }
}

private fun nextGeneration(generation: Int): Int = (generation + 1).let { next ->
    if (next == 0) 1 else next
}

private const val INITIAL_CAPACITY = 8
private const val MAX_HANDLE_COUNT = (1 shl 28) - 1
private const val GENERATION_SHIFT = 32
private const val INDEX_MASK = 0xffffffffuL
private const val ABSENT_INDEX = -1
private const val FREE: Byte = 0
private const val OWN: Byte = 1
private const val BORROW: Byte = 2
