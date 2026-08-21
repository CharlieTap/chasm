package io.github.charlietap.chasm.gc

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CollectionTest {

    @Test
    fun `zero-root collection reclaims ordinary and dedicated objects`() {
        val heap = GarbageCollectedHeap()
        val structDescriptor = heap.registerStruct(1, 2, intArrayOf())
        val arrayDescriptor = heap.registerArray(2, elementsMayContainReferences = false)
        val struct = heap.allocateStruct(structDescriptor, longArrayOf(11, 22))
        val array = heap.allocateArrayFilled(arrayDescriptor, 1024, 33)

        collect(heap)

        val statistics = heap.snapshotStatistics()
        assertEquals(0, statistics.activePageCount)
        assertEquals(1, statistics.recycledPageCount)
        assertEquals(0, statistics.dedicatedArrayCount)
        assertEquals(0L, statistics.allocatedSlotWords)
        assertEquals(0L, statistics.dedicatedPayloadWords)
        assertEquals(0L, statistics.freeSlotWords)
        assertEquals(0L, statistics.virginSlotWords)
        assertEquals(0L, statistics.pageTailWords)
        assertFalse(heap.isAllocatedReferenceForTesting(struct))
        assertFalse(heap.isAllocatedReferenceForTesting(array))
        heap.checkInvariants()
    }

    @Test
    fun `collection retains an exact transitive graph and clears every mark`() {
        val heap = GarbageCollectedHeap()
        val descriptor = heap.registerStruct(1, 2, intArrayOf(0))
        val deadLeaf = heap.allocateStruct(descriptor, longArrayOf(0, 1))
        val liveLeaf = heap.allocateStruct(descriptor, longArrayOf(0, 2))
        val root = heap.allocateStruct(descriptor, longArrayOf(liveLeaf, deadLeaf))

        collect(heap, root)

        assertTrue(heap.isAllocatedReferenceForTesting(root))
        assertTrue(heap.isAllocatedReferenceForTesting(liveLeaf))
        assertFalse(heap.isAllocatedReferenceForTesting(deadLeaf))
        assertFalse(heap.isMarkedForTesting(root))
        assertFalse(heap.isMarkedForTesting(liveLeaf))
        assertEquals(4L, heap.snapshotStatistics().allocatedSlotWords)
        heap.checkInvariants()
    }

    @Test
    fun `bitmap boundary sweep extends an existing free list exactly once`() {
        val heap = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 2,
                maximumPageCount = 1,
            ),
        )
        val descriptor = heap.registerStruct(1, 31, intArrayOf())
        val references = LongArray(66) { index ->
            heap.allocateStruct(descriptor, LongArray(31) { index.toLong() })
        }
        heap.releaseStructForTesting(references[1])
        val liveIndices = intArrayOf(0, 63, 64, 65)

        collect(
            heap,
            references[liveIndices[0]],
            references[liveIndices[1]],
            references[liveIndices[2]],
            references[liveIndices[3]],
        )

        val sweptStatistics = heap.snapshotStatistics()
        assertEquals(62L * 31, sweptStatistics.freeSlotWords)
        assertEquals(0L, sweptStatistics.virginSlotWords)
        assertEquals(2L, sweptStatistics.pageTailWords)

        val expectedReusable = references.toMutableSet()
        for (liveIndex in liveIndices) expectedReusable.remove(references[liveIndex])
        val actualReusable = mutableSetOf<Long>()
        repeat(expectedReusable.size) { value ->
            val reference = heap.allocateStruct(descriptor, LongArray(31) { value.toLong() })
            assertTrue(actualReusable.add(reference), "slot was reused more than once")
        }
        assertEquals(expectedReusable, actualReusable)
        assertEquals(66L * 31, heap.snapshotStatistics().allocatedSlotWords)
        heap.checkInvariants()
    }

    @Test
    fun `empty pages compact the active prefix and retask across descriptors`() {
        val heap = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 2,
                maximumPageCount = 5,
            ),
        )
        val wideDescriptor = heap.registerStruct(1, 2048, intArrayOf())
        val references = LongArray(5) { index ->
            heap.allocateStruct(wideDescriptor, LongArray(2048) { index.toLong() })
        }

        collect(heap, references[1], references[3])

        var statistics = heap.snapshotStatistics()
        assertEquals(2, statistics.activePageCount)
        assertEquals(3, statistics.recycledPageCount)
        assertEquals(4096L, statistics.allocatedSlotWords)
        assertTrue(heap.isAllocatedReferenceForTesting(references[1]))
        assertTrue(heap.isAllocatedReferenceForTesting(references[3]))
        assertFalse(heap.isAllocatedReferenceForTesting(references[0]))
        assertFalse(heap.isAllocatedReferenceForTesting(references[2]))
        assertFalse(heap.isAllocatedReferenceForTesting(references[4]))

        val narrowDescriptor = heap.registerStruct(2, 1, intArrayOf())
        repeat(3) { index ->
            heap.allocateStruct(narrowDescriptor, longArrayOf(index.toLong()))
        }
        statistics = heap.snapshotStatistics()
        assertEquals(3, statistics.activePageCount)
        assertEquals(2, statistics.recycledPageCount)
        heap.checkInvariants()
    }

    @Test
    fun `last ordinary array class page leaves no stale availability head`() {
        val heap = GarbageCollectedHeap()
        val descriptor = heap.registerArray(1, elementsMayContainReferences = false)
        val references = LongArray(15) { index ->
            heap.allocateArrayFilled(descriptor, 128, index.toLong())
        }

        collect(heap)

        for (reference in references) assertFalse(heap.isAllocatedReferenceForTesting(reference))
        val replacement = heap.allocateArrayFilled(descriptor, 128, 99)
        assertEquals(99, heap.getArrayElement(replacement, 127))
        assertEquals(1, heap.snapshotStatistics().activePageCount)
        heap.checkInvariants()
    }

    @Test
    fun `dedicated sweep preserves the active prefix and reuses every dead ID`() {
        val heap = GarbageCollectedHeap()
        val descriptor = heap.registerArray(1, elementsMayContainReferences = false)
        val references = LongArray(5) { index ->
            heap.allocateArrayFilled(descriptor, 1024, index.toLong())
        }
        heap.releaseArrayForTesting(references[1])

        collect(heap, references[0], references[3])

        assertTrue(heap.isAllocatedReferenceForTesting(references[0]))
        assertTrue(heap.isAllocatedReferenceForTesting(references[3]))
        assertFalse(heap.isAllocatedReferenceForTesting(references[1]))
        assertFalse(heap.isAllocatedReferenceForTesting(references[2]))
        assertFalse(heap.isAllocatedReferenceForTesting(references[4]))
        assertEquals(2, heap.snapshotStatistics().dedicatedArrayCount)
        assertEquals(2L * 1025, heap.snapshotStatistics().dedicatedPayloadWords)

        val expectedReusable = setOf(references[1], references[2], references[4])
        val actualReusable = mutableSetOf<Long>()
        repeat(3) { index ->
            actualReusable += heap.allocateArrayFilled(descriptor, 1024, index.toLong())
        }
        assertEquals(expectedReusable, actualReusable)
        heap.checkInvariants()
    }

    @Test
    fun `recycle capacity failure remains abortable and does not reclaim`() {
        val heap = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 2,
                maximumPageCount = 3,
            ),
        )
        val descriptor = heap.registerStruct(1, 2048, intArrayOf())
        val first = heap.allocateStruct(descriptor, LongArray(2048) { 1 })
        val second = heap.allocateStruct(descriptor, LongArray(2048) { 2 })
        val before = heap.snapshotStatistics()

        heap.beginCollection()
        heap.markRoot(first)
        assertFailsWith<OutOfMemoryError> {
            heap.finishCollectionForTesting(maximumRecycledPageCapacity = 1)
        }
        assertTrue(heap.isAllocatedReferenceForTesting(first))
        assertTrue(heap.isAllocatedReferenceForTesting(second))
        assertEquals(before.activePageCount, heap.snapshotStatistics().activePageCount)
        assertEquals(before.allocatedSlotWords, heap.snapshotStatistics().allocatedSlotWords)

        heap.abortCollection()
        assertFalse(heap.isMarkedForTesting(first))
        heap.checkInvariants()
    }

    @Test
    fun `public lifecycle supports zero roots and idempotent abort`() {
        val heap = GarbageCollectedHeap()
        val descriptor = heap.registerStruct(1, 0, intArrayOf())
        val reference = heap.allocateStruct(descriptor, longArrayOf())

        heap.abortCollection()
        heap.abortCollection()

        heap.beginCollection()
        heap.finishCollection()
        heap.abortCollection()

        assertFalse(heap.isAllocatedReferenceForTesting(reference))
        heap.checkInvariants()
    }

    @Test
    fun `exception and reference arrays retain only their declared graph`() {
        val heap = GarbageCollectedHeap()
        val leafDescriptor = heap.registerStruct(1, 0, intArrayOf())
        val referenceArrayDescriptor = heap.registerArray(2, elementsMayContainReferences = true)
        val numericArrayDescriptor = heap.registerArray(3, elementsMayContainReferences = false)
        val exceptionDescriptor = heap.registerException(4, 2, intArrayOf(1))
        val liveLeaf = heap.allocateStruct(leafDescriptor, longArrayOf())
        val falseNumericLeaf = heap.allocateStruct(leafDescriptor, longArrayOf())
        val deadLeaf = heap.allocateStruct(leafDescriptor, longArrayOf())
        val numeric = heap.allocateArrayFilled(numericArrayDescriptor, 1024, falseNumericLeaf)
        val references = heap.allocateArrayFromElements(
            referenceArrayDescriptor,
            longArrayOf(liveLeaf, numeric),
            sourceOffset = 0,
            length = 2,
        )
        val exception = heap.allocateException(exceptionDescriptor, longArrayOf(deadLeaf, references))

        collect(heap, exception)

        assertTrue(heap.isAllocatedReferenceForTesting(exception))
        assertTrue(heap.isAllocatedReferenceForTesting(references))
        assertTrue(heap.isAllocatedReferenceForTesting(numeric))
        assertTrue(heap.isAllocatedReferenceForTesting(liveLeaf))
        assertFalse(heap.isAllocatedReferenceForTesting(falseNumericLeaf))
        assertFalse(heap.isAllocatedReferenceForTesting(deadLeaf))
        heap.checkInvariants()
    }

    @Test
    fun `random graph collections match an independent reachability model`() {
        repeat(4) { seed ->
            val heap = GarbageCollectedHeap()
            val descriptor = heap.registerStruct(1, 2, intArrayOf(0, 1))
            val nodeCount = 384
            val references = LongArray(nodeCount) {
                heap.allocateStruct(descriptor, longArrayOf(0, 0))
            }
            val firstEdges = IntArray(nodeCount)
            val secondEdges = IntArray(nodeCount)
            val random = Random(seed)
            var index = 0
            while (index < nodeCount) {
                firstEdges[index] = random.nextInt(nodeCount)
                secondEdges[index] = random.nextInt(nodeCount)
                heap.setStructField(references[index], 0, references[firstEdges[index]])
                heap.setStructField(references[index], 1, references[secondEdges[index]])
                index++
            }

            val reachable = BooleanArray(nodeCount)
            val pending = IntArray(nodeCount)
            var pendingSize = 0
            val roots = IntArray(12) { random.nextInt(nodeCount) }
            for (root in roots) {
                if (!reachable[root]) {
                    reachable[root] = true
                    pending[pendingSize++] = root
                }
            }
            while (pendingSize != 0) {
                val current = pending[--pendingSize]
                val first = firstEdges[current]
                if (!reachable[first]) {
                    reachable[first] = true
                    pending[pendingSize++] = first
                }
                val second = secondEdges[current]
                if (!reachable[second]) {
                    reachable[second] = true
                    pending[pendingSize++] = second
                }
            }

            heap.beginCollection()
            try {
                for (root in roots) heap.markRoot(references[root])
                heap.finishCollection()
            } finally {
                heap.abortCollection()
            }

            var liveCount = 0
            index = 0
            while (index < nodeCount) {
                assertEquals(
                    reachable[index],
                    heap.isAllocatedReferenceForTesting(references[index]),
                    "seed=$seed node=$index",
                )
                if (reachable[index]) liveCount++
                index++
            }
            assertEquals(liveCount.toLong() * 2, heap.snapshotStatistics().allocatedSlotWords)
            heap.checkInvariants()
        }
    }

    @Test
    fun `successful collection preserves the worklist peak until the next cycle`() {
        val heap = GarbageCollectedHeap()
        val descriptor = heap.registerStruct(1, 1, intArrayOf(0))
        val tail = heap.allocateStruct(descriptor, longArrayOf(0))
        val root = heap.allocateStruct(descriptor, longArrayOf(tail))

        collect(heap, root)

        assertTrue(heap.snapshotStatistics().markWorklistPeakSize > 0)
        heap.beginCollection()
        assertEquals(0, heap.snapshotStatistics().markWorklistPeakSize)
        heap.abortCollection()
        assertEquals(0, heap.snapshotStatistics().markWorklistPeakSize)
        heap.checkInvariants()
    }

    @Test
    fun `successive collections repair ordinary and dedicated reuse state`() {
        val heap = GarbageCollectedHeap()
        val structDescriptor = heap.registerStruct(1, 1, intArrayOf())
        val arrayDescriptor = heap.registerArray(2, elementsMayContainReferences = false)
        val retainedStruct = heap.allocateStruct(structDescriptor, longArrayOf(1))
        val firstDeadStruct = heap.allocateStruct(structDescriptor, longArrayOf(2))
        val retainedArray = heap.allocateArrayFilled(arrayDescriptor, 1024, 3)
        val firstDeadArray = heap.allocateArrayFilled(arrayDescriptor, 1024, 4)

        collect(heap, retainedStruct, retainedArray)
        assertTrue(heap.isAllocatedReferenceForTesting(retainedStruct))
        assertTrue(heap.isAllocatedReferenceForTesting(retainedArray))
        assertFalse(heap.isAllocatedReferenceForTesting(firstDeadStruct))
        assertFalse(heap.isAllocatedReferenceForTesting(firstDeadArray))
        heap.checkInvariants()

        collect(heap)
        assertFalse(heap.isAllocatedReferenceForTesting(retainedStruct))
        assertFalse(heap.isAllocatedReferenceForTesting(retainedArray))
        assertEquals(0L, heap.snapshotStatistics().allocatedSlotWords)
        heap.checkInvariants()

        val reusedStruct = heap.allocateStruct(structDescriptor, longArrayOf(5))
        val reusedArray = heap.allocateArrayFilled(arrayDescriptor, 1024, 6)
        assertEquals(5, heap.getStructField(reusedStruct, 0))
        assertEquals(6, heap.getArrayElement(reusedArray, 1023))
        heap.checkInvariants()
    }

    @Test
    fun `randomized multi-cycle allocation and collection matches an object model`() {
        val heap = GarbageCollectedHeap()
        val descriptor = heap.registerStruct(1, 2, intArrayOf(0, 1))
        val model = mutableMapOf<Long, LongArray>()
        val random = Random(42)

        repeat(6) { cycle ->
            repeat(96) {
                val reference = heap.allocateStruct(descriptor, longArrayOf(0, 0))
                model[reference] = longArrayOf(0, 0)
            }
            val candidates = model.keys.toLongArray()
            for ((reference, edges) in model) {
                edges[0] = candidates[random.nextInt(candidates.size)]
                edges[1] = candidates[random.nextInt(candidates.size)]
                heap.setStructField(reference, 0, edges[0])
                heap.setStructField(reference, 1, edges[1])
            }
            val roots = LongArray(8) { candidates[random.nextInt(candidates.size)] }
            val reachable = mutableSetOf<Long>()
            val pending = ArrayDeque<Long>()
            for (root in roots) {
                if (reachable.add(root)) pending.addLast(root)
            }
            while (pending.isNotEmpty()) {
                val current = pending.removeLast()
                val edges = checkNotNull(model[current])
                if (reachable.add(edges[0])) pending.addLast(edges[0])
                if (reachable.add(edges[1])) pending.addLast(edges[1])
            }

            heap.beginCollection()
            try {
                for (root in roots) heap.markRoot(root)
                heap.finishCollection()
            } finally {
                heap.abortCollection()
            }

            val iterator = model.iterator()
            while (iterator.hasNext()) {
                val reference = iterator.next().key
                val expectedLive = reference in reachable
                assertEquals(
                    expectedLive,
                    heap.isAllocatedReferenceForTesting(reference),
                    "cycle=$cycle reference=$reference",
                )
                if (!expectedLive) iterator.remove()
            }
            assertEquals(model.size.toLong() * 2, heap.snapshotStatistics().allocatedSlotWords)
            heap.checkInvariants()
        }
    }

    @Test
    fun `completely live full page remains stable across repeated collections`() {
        val heap = GarbageCollectedHeap()
        val descriptor = heap.registerStruct(1, 4, intArrayOf())
        val references = LongArray(512) { index ->
            heap.allocateStruct(descriptor, longArrayOf(index.toLong(), 2, 3, 4))
        }

        collect(heap, *references)
        collect(heap, *references)

        val statistics = heap.snapshotStatistics()
        assertEquals(1, statistics.activePageCount)
        assertEquals(0, statistics.recycledPageCount)
        assertEquals(2048L, statistics.allocatedSlotWords)
        assertEquals(0L, statistics.freeSlotWords)
        assertEquals(0L, statistics.virginSlotWords)
        assertEquals(0L, statistics.pageTailWords)
        for (reference in references) assertTrue(heap.isAllocatedReferenceForTesting(reference))
        heap.checkInvariants()
    }

    @Test
    fun `ordinary array page recycles across size classes`() {
        val heap = GarbageCollectedHeap()
        val descriptor = heap.registerArray(1, elementsMayContainReferences = false)
        val narrow = heap.allocateArrayFilled(descriptor, 96, 1)
        val narrowPageId = ((narrow ushr 8).toInt()) ushr 11

        collect(heap)

        val wide = heap.allocateArrayFilled(descriptor, 512, 2)
        val widePageId = ((wide ushr 8).toInt()) ushr 11
        assertEquals(narrowPageId, widePageId)
        assertEquals(narrow, wide, "page retasking preserves the documented stale-reference ABA")
        assertEquals(2, heap.getArrayElement(wide, 511))
        heap.checkInvariants()
    }

    private fun collect(
        heap: GarbageCollectedHeap,
        vararg roots: Long,
    ) {
        heap.beginCollection()
        try {
            for (root in roots) heap.markRoot(root)
            heap.finishCollection()
        } catch (failure: Throwable) {
            heap.abortCollection()
            throw failure
        }
    }
}
