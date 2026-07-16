package io.github.charlietap.chasm.runtime.component.store

class ComponentCallScratch {
    private var mutableSlots = LongArray(INITIAL_SLOT_CAPACITY)
    private var mutableCallSlots = LongArray(INITIAL_SLOT_CAPACITY)
    private var mutableBytes = ByteArray(0)

    fun slots(size: Int): LongArray {
        if (mutableSlots.size < size) {
            mutableSlots = LongArray(grownCapacity(mutableSlots.size, size))
        }
        mutableSlots.fill(0L, toIndex = size)
        return mutableSlots
    }

    fun bytes(size: Int): ByteArray {
        if (mutableBytes.size < size) {
            mutableBytes = ByteArray(grownCapacity(mutableBytes.size, size))
        }
        return mutableBytes
    }

    fun callSlots(size: Int): LongArray {
        if (mutableCallSlots.size < size) {
            mutableCallSlots = LongArray(grownCapacity(mutableCallSlots.size, size))
        }
        mutableCallSlots.fill(0L, toIndex = size)
        return mutableCallSlots
    }
}

private fun grownCapacity(
    current: Int,
    required: Int,
): Int {
    var capacity = maxOf(current, 1)
    while (capacity < required) capacity = capacity shl 1
    return capacity
}

private const val INITIAL_SLOT_CAPACITY = 16
