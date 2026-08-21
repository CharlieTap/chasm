package io.github.charlietap.chasm.gc

import kotlin.test.Test
import kotlin.test.assertEquals

class AllocationPreflightTest {

    @Test
    fun `fixed capacity distinguishes growth reuse and exhaustion`() {
        val heap = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 2,
                maximumPageCount = 1,
            ),
        )
        val descriptor = heap.registerStruct(0, 2048, IntArray(0))

        assertEquals(2048, heap.fixedAllocationSlotWords(descriptor))
        assertEquals(AllocationAvailability.GROWABLE, heap.fixedAllocationAvailability(descriptor))

        heap.allocateStruct(descriptor, LongArray(2048))
        assertEquals(AllocationAvailability.EXHAUSTED, heap.fixedAllocationAvailability(descriptor))

        heap.beginCollection()
        heap.finishCollection()
        assertEquals(AllocationAvailability.REUSABLE, heap.fixedAllocationAvailability(descriptor))
    }

    @Test
    fun `array preflight reports exact class and dedicated slot words`() {
        val heap = GarbageCollectedHeap()
        val descriptor = heap.registerArray(0, elementsMayContainReferences = false)

        assertEquals(1, heap.arrayAllocationSlotWords(descriptor, 0))
        assertEquals(97, heap.arrayAllocationSlotWords(descriptor, 96))
        assertEquals(129, heap.arrayAllocationSlotWords(descriptor, 97))
        assertEquals(1024, heap.arrayAllocationSlotWords(descriptor, 1023))
        assertEquals(1025, heap.arrayAllocationSlotWords(descriptor, 1024))
        assertEquals(AllocationAvailability.GROWABLE, heap.arrayAllocationAvailability(descriptor, 1024))
    }
}
