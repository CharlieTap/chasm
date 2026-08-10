package io.github.charlietap.chasm.compiler.operand

import kotlin.test.Test
import kotlin.test.assertEquals

class FrameAllocatorTest {

    @Test
    fun reusesReleasedTopSlots() {
        val allocator = FrameAllocator(temporarySlotBase = 3)

        assertEquals(3, allocator.allocate())
        assertEquals(4, allocator.allocate())
        allocator.release(4)

        assertEquals(4, allocator.allocate())
        assertEquals(5, allocator.maxSlotExclusive)
    }

    @Test
    fun rewindsToTheHighestReferencedTemporary() {
        val allocator = FrameAllocator(temporarySlotBase = 2)
        repeat(4) { allocator.allocate() }

        allocator.rewindTo(4)

        assertEquals(5, allocator.allocate())
        assertEquals(6, allocator.maxSlotExclusive)
    }
}
