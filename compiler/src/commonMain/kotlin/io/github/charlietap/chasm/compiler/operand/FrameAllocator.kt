package io.github.charlietap.chasm.compiler.operand

internal class FrameAllocator(
    val temporarySlotBase: Int,
) {

    private var temporaryHeight = 0

    var maxTemporaryHeight: Int = 0
        private set

    val maxSlotExclusive: Int
        get() = temporarySlotBase + maxTemporaryHeight

    fun allocate(): Int {
        val slot = temporarySlotBase + temporaryHeight
        temporaryHeight++
        maxTemporaryHeight = maxOf(maxTemporaryHeight, temporaryHeight)
        return slot
    }

    fun reserve(slot: Int) {
        if (!isTemporary(slot)) return

        temporaryHeight = maxOf(temporaryHeight, slot - temporarySlotBase + 1)
        maxTemporaryHeight = maxOf(maxTemporaryHeight, temporaryHeight)
    }

    fun release(slot: Int) {
        if (!isTemporary(slot)) return
        if (slot == temporarySlotBase + temporaryHeight - 1) {
            temporaryHeight--
        }
    }

    fun rewindTo(highestReferencedSlot: Int) {
        temporaryHeight = if (isTemporary(highestReferencedSlot)) {
            highestReferencedSlot - temporarySlotBase + 1
        } else {
            0
        }
    }

    fun snapshot(): Int = temporaryHeight

    fun restore(temporaryHeight: Int) {
        check(temporaryHeight >= 0)
        this.temporaryHeight = temporaryHeight
    }

    fun isTemporary(slot: Int): Boolean = slot >= temporarySlotBase
}
