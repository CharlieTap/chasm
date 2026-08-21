package io.github.charlietap.chasm.gc

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeepMarkingTest {

    @Test
    fun `one million node chain marks iteratively without recursion`() {
        val heap = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 2,
                maximumPageCount = 1_024,
            ),
        )
        val descriptor = heap.registerStruct(0, 1, intArrayOf(0))
        val field = LongArray(1)
        var root = 0L
        repeat(1_000_000) {
            field[0] = root
            root = heap.allocateStruct(descriptor, field)
        }

        heap.beginMarkingForTesting()
        heap.markRootForTesting(root)
        heap.drainMarkWorklistForTesting()

        assertEquals(1_000_000, heap.markedObjectCountForTesting())
        assertEquals(1, heap.markWorklistPeakSizeForTesting())
        assertTrue(heap.isMarkedForTesting(root))
        heap.abortMarkingForTesting()
        heap.checkInvariants()
    }
}
