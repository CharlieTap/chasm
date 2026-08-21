package io.github.charlietap.chasm.gc

import io.github.charlietap.chasm.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_ARRAY
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_STRUCT
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MarkingTest {

    @Test
    fun `randomized precise graphs match a slow reachability model`() {
        repeat(5) { seed ->
            val heap = GarbageCollectedHeap()
            val descriptor = heap.registerStruct(0, 3, intArrayOf(0, 2))
            val nodeCount = 512
            val references = LongArray(nodeCount) {
                heap.allocateStruct(descriptor, longArrayOf(0, 0, 0))
            }
            val firstEdges = IntArray(nodeCount)
            val secondEdges = IntArray(nodeCount)
            val random = Random(seed)
            var nodeIndex = 0
            while (nodeIndex < nodeCount) {
                val firstEdge = random.nextInt(nodeCount)
                val secondEdge = random.nextInt(nodeCount)
                firstEdges[nodeIndex] = firstEdge
                secondEdges[nodeIndex] = secondEdge
                heap.setStructField(references[nodeIndex], 0, references[firstEdge])
                heap.setStructField(references[nodeIndex], 1, references[(firstEdge + 1) % nodeCount])
                heap.setStructField(references[nodeIndex], 2, references[secondEdge])
                nodeIndex++
            }

            val reachable = BooleanArray(nodeCount)
            val pending = IntArray(nodeCount)
            var pendingSize = 0
            heap.beginMarkingForTesting()
            repeat(16) {
                val rootIndex = random.nextInt(nodeCount)
                heap.markRootForTesting(references[rootIndex])
                if (!reachable[rootIndex]) {
                    reachable[rootIndex] = true
                    pending[pendingSize++] = rootIndex
                }
            }
            while (pendingSize != 0) {
                val current = pending[--pendingSize]
                val firstEdge = firstEdges[current]
                if (!reachable[firstEdge]) {
                    reachable[firstEdge] = true
                    pending[pendingSize++] = firstEdge
                }
                val secondEdge = secondEdges[current]
                if (!reachable[secondEdge]) {
                    reachable[secondEdge] = true
                    pending[pendingSize++] = secondEdge
                }
            }

            heap.drainMarkWorklistForTesting()
            nodeIndex = 0
            var expectedMarkedCount = 0
            while (nodeIndex < nodeCount) {
                assertEquals(
                    reachable[nodeIndex],
                    heap.isMarkedForTesting(references[nodeIndex]),
                    "seed=$seed node=$nodeIndex",
                )
                if (reachable[nodeIndex]) expectedMarkedCount++
                nodeIndex++
            }
            assertEquals(expectedMarkedCount, heap.markedObjectCountForTesting())
            heap.abortMarkingForTesting()
            heap.checkInvariants()
        }
    }

    @Test
    fun `marking follows only descriptor declared references through cycles`() {
        val heap = GarbageCollectedHeap()
        val leafDescriptor = heap.registerStruct(1, 0, intArrayOf())
        val nodeDescriptor = heap.registerStruct(2, 2, intArrayOf(1))
        val referenceArrayDescriptor = heap.registerArray(3, elementsMayContainReferences = true)
        val numericArrayDescriptor = heap.registerArray(4, elementsMayContainReferences = false)
        val exceptionDescriptor = heap.registerException(5, 2, intArrayOf(0))
        val deadLeaf = heap.allocateStruct(leafDescriptor, longArrayOf())
        val liveLeaf = heap.allocateStruct(leafDescriptor, longArrayOf())
        val node = heap.allocateStruct(nodeDescriptor, longArrayOf(deadLeaf, 0))
        val numericArray = heap.allocateArrayFilled(numericArrayDescriptor, 1, deadLeaf)
        val referenceArray = heap.allocateArrayFromElements(
            referenceArrayDescriptor,
            longArrayOf(node, liveLeaf, numericArray),
            0,
            3,
        )
        heap.setStructField(node, 1, referenceArray)
        val exception = heap.allocateException(exceptionDescriptor, longArrayOf(node, deadLeaf))

        heap.beginMarkingForTesting()
        heap.markRootForTesting(exception)
        heap.markRootForTesting(exception)
        heap.drainMarkWorklistForTesting()

        assertTrue(heap.isMarkedForTesting(exception))
        assertTrue(heap.isMarkedForTesting(node))
        assertTrue(heap.isMarkedForTesting(referenceArray))
        assertTrue(heap.isMarkedForTesting(numericArray))
        assertTrue(heap.isMarkedForTesting(liveLeaf))
        assertFalse(heap.isMarkedForTesting(deadLeaf))
        assertEquals(5, heap.markedObjectCountForTesting())
        heap.checkInvariants()

        heap.abortMarkingForTesting()
        assertEquals(0, heap.markedObjectCountForTesting())
        heap.checkInvariants()
    }

    @Test
    fun `ordinary array marking ignores poisoned size class slack`() {
        val heap = GarbageCollectedHeap()
        val leafDescriptor = heap.registerStruct(0, 0, intArrayOf())
        val arrayDescriptor = heap.registerArray(1, elementsMayContainReferences = true)
        val staleTarget = heap.allocateStruct(leafDescriptor, longArrayOf())
        val arrays = LongArray(15) {
            heap.allocateArrayFilled(arrayDescriptor, 128, staleTarget)
        }
        val poisoned = arrays[7]
        heap.releaseArrayForTesting(poisoned)
        val shorter = heap.allocateArrayFilled(arrayDescriptor, 97, 0)
        assertEquals(poisoned, shorter)

        heap.beginMarkingForTesting()
        heap.markRootForTesting(shorter)
        heap.drainMarkWorklistForTesting()

        assertTrue(heap.isMarkedForTesting(shorter))
        assertFalse(heap.isMarkedForTesting(staleTarget))
        assertEquals(1, heap.markedObjectCountForTesting())
        heap.abortMarkingForTesting()
        heap.checkInvariants()
    }

    @Test
    fun `dedicated numeric arrays skip payload while reference arrays trace it`() {
        val heap = GarbageCollectedHeap(GarbageCollectedHeap.Configuration(maximumPageCount = 16))
        val leafDescriptor = heap.registerStruct(0, 0, intArrayOf())
        val numericArrayDescriptor = heap.registerArray(1, elementsMayContainReferences = false)
        val referenceArrayDescriptor = heap.registerArray(2, elementsMayContainReferences = true)
        val numericOnly = heap.allocateStruct(leafDescriptor, longArrayOf())
        val referenced = heap.allocateStruct(leafDescriptor, longArrayOf())
        val numericArray = heap.allocateArrayFilled(numericArrayDescriptor, 1024, numericOnly)
        val referenceArray = heap.allocateArrayFilled(referenceArrayDescriptor, 1024, referenced)

        heap.beginMarkingForTesting()
        heap.markRootForTesting(numericArray)
        heap.markRootForTesting(referenceArray)
        heap.drainMarkWorklistForTesting()

        assertTrue(heap.isMarkedForTesting(numericArray))
        assertTrue(heap.isMarkedForTesting(referenceArray))
        assertFalse(heap.isMarkedForTesting(numericOnly))
        assertTrue(heap.isMarkedForTesting(referenced))
        assertEquals(3, heap.markedObjectCountForTesting())
        heap.abortMarkingForTesting()
        heap.checkInvariants()
    }

    @Test
    fun `root submission failure is cleared by abort`() {
        val heap = GarbageCollectedHeap()
        val descriptor = heap.registerStruct(0, 0, intArrayOf())
        val reference = heap.allocateStruct(descriptor, longArrayOf())

        heap.beginMarkingForTesting()
        assertFailsWith<GuestHeapOutOfMemoryError> {
            heap.markRootForTesting(reference, maximumWorklistCapacity = 0)
        }
        heap.abortMarkingForTesting()

        heap.beginMarkingForTesting()
        heap.markRootForTesting(reference)
        heap.drainMarkWorklistForTesting()
        assertTrue(heap.isMarkedForTesting(reference))
        heap.abortMarkingForTesting()
        heap.checkInvariants()
    }

    @Test
    fun `wide frontier grows then releases an oversized retained worklist after a low cycle`() {
        val heap = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 2,
                maximumPageCount = 64,
            ),
        )
        val leafDescriptor = heap.registerStruct(0, 0, intArrayOf())
        val arrayDescriptor = heap.registerArray(1, elementsMayContainReferences = true)
        val references = LongArray(20_000) {
            heap.allocateStruct(leafDescriptor, longArrayOf())
        }
        val root = heap.allocateArrayFromElements(arrayDescriptor, references, 0, references.size)

        heap.beginMarkingForTesting()
        heap.markRootForTesting(root)
        heap.drainMarkWorklistForTesting()
        assertEquals(20_000, heap.markWorklistPeakSizeForTesting())
        assertTrue(heap.markWorklistCapacityForTesting() > 16 * 1024)
        assertEquals(20_001, heap.markedObjectCountForTesting())
        val statistics = heap.snapshotStatistics()
        assertEquals(heap.markWorklistCapacityForTesting(), statistics.markWorklistCapacity)
        assertEquals(20_000, statistics.markWorklistPeakSize)
        heap.abortMarkingForTesting()
        val retainedCapacity = heap.markWorklistCapacityForTesting()
        assertTrue(retainedCapacity > 16 * 1024)

        heap.beginMarkingForTesting()
        heap.markRootForTesting(references[0])
        heap.drainMarkWorklistForTesting()
        assertEquals(1, heap.markWorklistPeakSizeForTesting())
        heap.abortMarkingForTesting()
        assertEquals(0, heap.markWorklistCapacityForTesting())
        heap.checkInvariants()
    }

    @Test
    fun `failed worklist growth leaves the candidate unmarked and abort clears partial marks`() {
        val heap = GarbageCollectedHeap()
        val leafDescriptor = heap.registerStruct(0, 0, intArrayOf())
        val arrayDescriptor = heap.registerArray(1, elementsMayContainReferences = true)
        val references = LongArray(8) {
            heap.allocateStruct(leafDescriptor, longArrayOf())
        }
        val root = heap.allocateArrayFromElements(arrayDescriptor, references, 0, references.size)

        heap.beginMarkingForTesting()
        heap.markRootForTesting(root, maximumWorklistCapacity = 4)
        assertFailsWith<GuestHeapOutOfMemoryError> {
            heap.drainMarkWorklistForTesting(maximumWorklistCapacity = 4)
        }

        assertEquals(5, heap.markedObjectCountForTesting())
        assertFalse(heap.isMarkedForTesting(references[4]))
        heap.checkInvariants()
        heap.abortMarkingForTesting()
        assertEquals(0, heap.markedObjectCountForTesting())
        heap.checkInvariants()
    }

    @Test
    fun `forged numeric and wrong kind roots do not mark objects`() {
        val heap = GarbageCollectedHeap()
        val structDescriptor = heap.registerStruct(0, 2, intArrayOf())
        val arrayDescriptor = heap.registerArray(1, elementsMayContainReferences = false)
        val struct = heap.allocateStruct(structDescriptor, longArrayOf(0, 0))
        val releasedStruct = heap.allocateStruct(structDescriptor, longArrayOf(0, 0))
        heap.releaseStructForTesting(releasedStruct)
        val array = heap.allocateArrayFilled(arrayDescriptor, 1024, 0)
        val releasedDedicated = heap.allocateArrayFilled(arrayDescriptor, 1024, 0)
        heap.releaseArrayForTesting(releasedDedicated)
        val wrongStructKind = (struct and 0xFFL.inv()) or RV_TYPE_ARRAY
        val wrongArrayKind = (array and 0xFFL.inv()) or RV_TYPE_STRUCT
        val dedicatedZeroId = (1L shl 30 shl RV_SHIFT_BITS) or RV_TYPE_ARRAY
        val unissuedDedicatedId =
            (((1L shl 30) or 3L) shl RV_SHIFT_BITS) or RV_TYPE_ARRAY
        val pageZeroOrdinary = (1L shl RV_SHIFT_BITS) or RV_TYPE_STRUCT
        val outOfRangeOrdinary =
            ((0x3FFF_FFFFL) shl RV_SHIFT_BITS) or RV_TYPE_STRUCT

        heap.beginMarkingForTesting()
        heap.markRootForTesting(0)
        heap.markRootForTesting(-1)
        heap.markRootForTesting(wrongStructKind)
        heap.markRootForTesting(wrongArrayKind)
        heap.markRootForTesting(releasedStruct)
        heap.markRootForTesting(pageZeroOrdinary)
        heap.markRootForTesting(outOfRangeOrdinary)
        heap.markRootForTesting(dedicatedZeroId)
        heap.markRootForTesting(releasedDedicated)
        heap.markRootForTesting(unissuedDedicatedId)
        heap.markRootForTesting(struct + (1L shl RV_SHIFT_BITS))
        heap.drainMarkWorklistForTesting()

        assertEquals(0, heap.markedObjectCountForTesting())
        heap.abortMarkingForTesting()
        heap.checkInvariants()
    }
}
