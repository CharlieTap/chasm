package io.github.charlietap.chasm.runtime.component.resource

import io.github.charlietap.chasm.runtime.address.ComponentCallToken
import io.github.charlietap.chasm.runtime.address.RuntimeResourceTypeAddress
import kotlin.jvm.JvmInline

class CanonicalHandleTable {

    private var types = IntArray(INITIAL_CAPACITY)
    private var representations = IntArray(INITIAL_CAPACITY)
    private var flags = ByteArray(INITIAL_CAPACITY)
    private var lendCounts = IntArray(INITIAL_CAPACITY)
    private var borrowTokens = LongArray(INITIAL_CAPACITY)
    private var nextFree = IntArray(INITIAL_CAPACITY)
    private var freeHead = ABSENT_INDEX
    private var nextUnused = 0
    private var cleaning = false

    var size: Int = 0
        private set

    fun insertOwn(
        type: RuntimeResourceTypeAddress,
        representation: Int,
    ): Int {
        val index = allocate()
        types[index] = type.address
        representations[index] = representation
        flags[index] = OWN
        return index + 1
    }

    fun ownRepresentation(
        handle: Int,
        type: RuntimeResourceTypeAddress,
    ): Int = representations[requireEntry(handle, type, OWN)]

    fun representation(
        handle: Int,
        type: RuntimeResourceTypeAddress,
    ): Int {
        val index = requireHandle(handle)
        if (types[index] != type.address) throw ResourceTableException(ResourceTableError.TypeMismatch)
        return representations[index]
    }

    fun removeOwn(
        handle: Int,
        type: RuntimeResourceTypeAddress,
    ): Int {
        val index = requireEntry(handle, type, OWN)
        if (lendCounts[index] != 0) throw ResourceTableException(ResourceTableError.ResourceLent)
        val representation = representations[index]
        release(index)
        return representation
    }

    fun lend(
        handle: Int,
        type: RuntimeResourceTypeAddress,
    ): CanonicalResourceLend {
        val index = requireHandle(handle)
        if (types[index] != type.address) throw ResourceTableException(ResourceTableError.TypeMismatch)
        val ownsLender = when (flags[index]) {
            OWN -> {
                lendCounts[index] += 1
                true
            }
            BORROW -> false
            else -> throw ResourceTableException(ResourceTableError.OwnershipMismatch)
        }
        return CanonicalResourceLend(representations[index], ownsLender)
    }

    fun undoLend(handle: Int) {
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
    ): Int {
        val index = allocate()
        types[index] = type.address
        representations[index] = representation
        borrowTokens[index] = callToken.token.toLong()
        flags[index] = BORROW
        return index + 1
    }

    fun borrowRepresentation(
        handle: Int,
        type: RuntimeResourceTypeAddress,
    ): Int = representations[requireEntry(handle, type, BORROW)]

    fun removeBorrow(
        handle: Int,
        type: RuntimeResourceTypeAddress,
    ): ComponentCallToken {
        val index = requireEntry(handle, type, BORROW)
        val callToken = ComponentCallToken(borrowTokens[index].toULong())
        release(index)
        return callToken
    }

    fun removeBorrowIfPresent(
        handle: Int,
        callToken: ComponentCallToken,
    ): Boolean {
        val index = handle - 1
        if (
            index !in 0 until nextUnused ||
            flags[index] != BORROW ||
            borrowTokens[index] != callToken.token.toLong()
        ) {
            return false
        }
        release(index)
        return true
    }

    fun isBorrow(handle: Int): Boolean {
        val index = handle - 1
        return index in 0 until nextUnused && flags[index] == BORROW
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
        handle: Int,
        type: RuntimeResourceTypeAddress,
        ownership: Byte,
    ): Int {
        val index = requireHandle(handle)
        if (types[index] != type.address) throw ResourceTableException(ResourceTableError.TypeMismatch)
        if (flags[index] != ownership) throw ResourceTableException(ResourceTableError.OwnershipMismatch)
        return index
    }

    private fun requireHandle(handle: Int): Int {
        val index = handle - 1
        if (index !in 0 until nextUnused || flags[index] == FREE) {
            throw ResourceTableException(ResourceTableError.InvalidHandle)
        }
        return index
    }

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
        nextFree = nextFree.copyOf(capacity)
    }
}

@JvmInline
value class CanonicalResourceLend private constructor(private val packed: ULong) {

    constructor(
        representation: Int,
        ownsLender: Boolean,
    ) : this(
        representation.toUInt().toULong() or
            ((if (ownsLender) 1uL else 0uL) shl OWNERSHIP_SHIFT),
    )

    val representation: Int
        get() = (packed and REPRESENTATION_MASK).toUInt().toInt()

    val ownsLender: Boolean
        get() = packed shr OWNERSHIP_SHIFT != 0uL
}

private const val INITIAL_CAPACITY = 8
private const val MAX_HANDLE_COUNT = (1 shl 28) - 1
private const val ABSENT_INDEX = -1
private const val FREE: Byte = 0
private const val OWN: Byte = 1
private const val BORROW: Byte = 2
private const val OWNERSHIP_SHIFT = 32
private const val REPRESENTATION_MASK = 0xffffffffuL
