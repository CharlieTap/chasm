package io.github.charlietap.chasm.gc

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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

    @Test
    fun `payload word budget bounds ordinary pages and dedicated arrays`() {
        val heap = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(maximumPayloadWords = 3000L),
        )
        val struct = heap.registerStruct(0, 1, IntArray(0))
        val array = heap.registerArray(1, elementsMayContainReferences = false)

        assertEquals(3000L, heap.maximumPayloadWords())
        assertEquals(AllocationAvailability.GROWABLE, heap.fixedAllocationAvailability(struct))
        heap.allocateStruct(struct, LongArray(1))

        // One 2048-word page is committed, leaving 952 words for dedicated arrays.
        assertEquals(AllocationAvailability.EXHAUSTED, heap.arrayAllocationAvailability(array, 1024))
        assertFailsWith<GuestHeapOutOfMemoryException> {
            heap.allocateArrayFilled(array, 1024, 0L)
        }
    }
}
