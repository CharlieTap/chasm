package io.github.charlietap.chasm.gc

import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_ARRAY
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_MASK
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class StructAllocationTest {

    @Test
    fun `allocation copies every field before publishing the struct`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerStruct(7, 3, intArrayOf(1))
        val initialFields = longArrayOf(11, 22, 33)

        val reference = heap.allocateStruct(descriptorKey, initialFields)
        initialFields.fill(-1)

        assertEquals(11, heap.getStructField(reference, 0))
        assertEquals(22, heap.getStructField(reference, 1))
        assertEquals(33, heap.getStructField(reference, 2))
        assertEquals(7, heap.structSemanticId(reference))
        heap.checkInvariants()
    }

    @Test
    fun `zero-field structs retain distinct identities across a page boundary`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerStruct(0, 0, intArrayOf())
        var previousReference = 0L
        var index = 0
        while (index < 2049) {
            val reference = heap.allocateStruct(descriptorKey, longArrayOf())
            assertNotEquals(previousReference, reference)
            previousReference = reference
            index++
        }

        val statistics = heap.snapshotStatistics()
        assertEquals(2, statistics.activePageCount)
        assertEquals(2049L, statistics.allocatedSlotWords)
        assertEquals(4096L, statistics.committedWords)
        heap.checkInvariants()
    }

    @Test
    fun `non-dividing slots fill one page then continue on another`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerStruct(0, 31, intArrayOf())
        val fields = LongArray(31) { it.toLong() }
        var firstReference = 0L
        var lastReference = 0L
        var index = 0
        while (index < 67) {
            val reference = heap.allocateStruct(descriptorKey, fields)
            if (index == 0) firstReference = reference
            lastReference = reference
            index++
        }

        assertEquals(0, heap.getStructField(firstReference, 0))
        assertEquals(30, heap.getStructField(lastReference, 30))
        assertEquals(2, heap.snapshotStatistics().activePageCount)
        heap.checkInvariants()
    }

    @Test
    fun `page-exact structs preserve first middle and last field access`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerStruct(0, 2048, intArrayOf())
        val fields = LongArray(2048) { it.toLong() }

        val reference = heap.allocateStruct(descriptorKey, fields)
        heap.setStructField(reference, 0, -1)
        heap.setStructField(reference, 1024, -2)
        heap.setStructField(reference, 2047, -3)

        assertEquals(-1, heap.getStructField(reference, 0))
        assertEquals(-2, heap.getStructField(reference, 1024))
        assertEquals(-3, heap.getStructField(reference, 2047))
        heap.checkInvariants()
    }

    @Test
    fun `free-list reuse overwrites poisoned fields and relinks a full page`() {
        val heap = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 2,
                maximumPageCount = 1,
            ),
        )
        val descriptorKey = heap.registerStruct(0, 4, intArrayOf())
        val references = LongArray(512)
        var index = 0
        while (index < references.size) {
            references[index] = heap.allocateStruct(
                descriptorKey,
                longArrayOf(index.toLong(), 2, 3, 4),
            )
            index++
        }
        val firstReleasedReference = references[200]
        val secondReleasedReference = references[100]
        heap.releaseStructForTesting(firstReleasedReference)
        heap.releaseStructForTesting(secondReleasedReference)
        heap.checkInvariants()

        val typedNull = 0x0000_002A_0000_0001L
        val firstReusedReference = heap.allocateStruct(
            descriptorKey,
            longArrayOf(0, typedNull, -7, Long.MAX_VALUE),
        )
        val secondReusedReference = heap.allocateStruct(
            descriptorKey,
            longArrayOf(5, 6, 7, 8),
        )

        assertEquals(secondReleasedReference, firstReusedReference)
        assertEquals(firstReleasedReference, secondReusedReference)
        assertEquals(0, heap.getStructField(firstReusedReference, 0))
        assertEquals(typedNull, heap.getStructField(firstReusedReference, 1))
        assertEquals(-7, heap.getStructField(firstReusedReference, 2))
        assertEquals(Long.MAX_VALUE, heap.getStructField(firstReusedReference, 3))
        assertEquals(5, heap.getStructField(secondReusedReference, 0))
        assertEquals(8, heap.getStructField(secondReusedReference, 3))
        assertEquals(1, heap.snapshotStatistics().activePageCount)
        heap.checkInvariants()
    }

    @Test
    fun `invalid allocation input fails before publishing capacity or slots`() {
        val heap = GarbageCollectedHeap()
        val structDescriptor = heap.registerStruct(0, 2, intArrayOf())
        val arrayDescriptor = heap.registerArray(1, elementsMayContainReferences = false)

        assertFailsWith<IllegalArgumentException> {
            heap.allocateStruct(structDescriptor, longArrayOf(1))
        }
        assertFailsWith<IllegalArgumentException> {
            heap.allocateStruct(arrayDescriptor, longArrayOf())
        }
        assertFailsWith<IllegalArgumentException> {
            heap.allocateStruct(12345, longArrayOf())
        }

        val statistics = heap.snapshotStatistics()
        assertEquals(0, statistics.activePageCount)
        assertEquals(0L, statistics.committedWords)
        assertEquals(0L, statistics.allocatedSlotWords)
        heap.checkInvariants()
    }

    @Test
    fun `capacity failure preserves the allocated page and accounting`() {
        val heap = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 2,
                maximumPageCount = 1,
            ),
        )
        val descriptorKey = heap.registerStruct(0, 2048, intArrayOf())
        val reference = heap.allocateStruct(descriptorKey, LongArray(2048) { it.toLong() })
        val before = heap.snapshotStatistics()

        assertFailsWith<OutOfMemoryError> {
            heap.allocateStruct(descriptorKey, LongArray(2048))
        }

        val after = heap.snapshotStatistics()
        assertEquals(before.activePageCount, after.activePageCount)
        assertEquals(before.committedWords, after.committedWords)
        assertEquals(before.allocatedSlotWords, after.allocatedSlotWords)
        assertEquals(2047, heap.getStructField(reference, 2047))
        heap.checkInvariants()
    }

    @Test
    fun `payload growth preserves live fields and an intrusive free chain`() {
        val heap = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 2,
                maximumPageCount = 5,
            ),
        )
        val reusableDescriptor = heap.registerStruct(0, 1024, intArrayOf())
        val secondDescriptor = heap.registerStruct(1, 2048, intArrayOf())
        val thirdDescriptor = heap.registerStruct(2, 2048, intArrayOf())
        val firstReleasedReference = heap.allocateStruct(
            reusableDescriptor,
            LongArray(1024) { 1_000L + it },
        )
        val secondReleasedReference = heap.allocateStruct(
            reusableDescriptor,
            LongArray(1024) { 1_500L + it },
        )
        heap.releaseStructForTesting(firstReleasedReference)
        heap.releaseStructForTesting(secondReleasedReference)

        val secondReference = heap.allocateStruct(
            secondDescriptor,
            LongArray(2048) { 2_000L + it },
        )
        val thirdReference = heap.allocateStruct(
            thirdDescriptor,
            LongArray(2048) { 3_000L + it },
        )
        val firstReusedReference = heap.allocateStruct(
            reusableDescriptor,
            LongArray(1024) { 4_000L + it },
        )
        val secondReusedReference = heap.allocateStruct(
            reusableDescriptor,
            LongArray(1024) { 5_000L + it },
        )

        assertEquals(secondReleasedReference, firstReusedReference)
        assertEquals(firstReleasedReference, secondReusedReference)
        assertEquals(4_000L, heap.getStructField(firstReusedReference, 0))
        assertEquals(5_023L, heap.getStructField(firstReusedReference, 1023))
        assertEquals(5_000L, heap.getStructField(secondReusedReference, 0))
        assertEquals(6_023L, heap.getStructField(secondReusedReference, 1023))
        assertEquals(2_000L, heap.getStructField(secondReference, 0))
        assertEquals(4_047L, heap.getStructField(secondReference, 2047))
        assertEquals(3_000L, heap.getStructField(thirdReference, 0))
        assertEquals(5_047L, heap.getStructField(thirdReference, 2047))
        assertEquals(8_192L, heap.snapshotStatistics().committedWords)
        heap.checkInvariants()
    }

    @Test
    fun `recycled payload is fully overwritten by a different struct layout`() {
        val heap = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 2,
                maximumPageCount = 1,
            ),
        )
        val poisonDescriptor = heap.registerStruct(0, 4, intArrayOf(0, 2))
        val replacementDescriptor = heap.registerStruct(1, 2, intArrayOf(1))
        val poisonReference = heap.allocateStruct(
            poisonDescriptor,
            longArrayOf(Long.MAX_VALUE, -1, Long.MIN_VALUE, -2),
        )
        heap.releaseStructForTesting(poisonReference)
        heap.recycleEmptyPageForTesting(1)

        val typedNull = 0x0000_0007_0000_0001L
        val replacementReference = heap.allocateStruct(
            replacementDescriptor,
            longArrayOf(0, typedNull),
        )

        assertEquals(0L, heap.getStructField(replacementReference, 0))
        assertEquals(typedNull, heap.getStructField(replacementReference, 1))
        assertEquals(1, heap.structSemanticId(replacementReference))
        heap.checkInvariants()
    }

    @Test
    fun `semantic lookup rejects null wrong-tag unallocated and released references`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerStruct(17, 1, intArrayOf())
        val reference = heap.allocateStruct(descriptorKey, longArrayOf(1))
        val wrongTag =
            (reference and RV_TYPE_MASK.inv()) or RV_TYPE_ARRAY

        assertEquals(17, heap.structSemanticId(reference))
        assertFailsWith<IllegalArgumentException> { heap.structSemanticId(0L) }
        assertFailsWith<IllegalArgumentException> { heap.structSemanticId(wrongTag) }
        heap.releaseStructForTesting(reference)
        assertFailsWith<IllegalArgumentException> { heap.structSemanticId(reference) }
        heap.checkInvariants()
    }

    @Test
    fun `benchmark reset restores precommitted pages without exposing stale payload`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerStruct(0, 2, intArrayOf())
        val originalReference = heap.allocateStruct(descriptorKey, longArrayOf(91, 92))
        heap.allocateStruct(descriptorKey, longArrayOf(93, 94))

        heap.resetStructAllocationsForTesting(descriptorKey)

        assertEquals(0L, heap.snapshotStatistics().allocatedSlotWords)
        val reusedReference = heap.allocateStruct(descriptorKey, longArrayOf(1, 2))
        assertEquals(originalReference, reusedReference)
        assertEquals(1, heap.getStructField(reusedReference, 0))
        assertEquals(2, heap.getStructField(reusedReference, 1))
        heap.checkInvariants()
    }

    @Test
    fun `random allocation and field mutation agrees with a primitive model`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerStruct(23, 3, intArrayOf(1))
        val references = LongArray(1000)
        val model = LongArray(3000)
        var index = 0
        while (index < references.size) {
            val first = nextValue(index)
            val second = nextValue(first.toInt())
            val third = nextValue(second.toInt())
            references[index] = heap.allocateStruct(
                descriptorKey,
                longArrayOf(first, second, third),
            )
            val modelOffset = index * 3
            model[modelOffset] = first
            model[modelOffset + 1] = second
            model[modelOffset + 2] = third
            index++
        }

        var state = 42
        var operation = 0
        while (operation < 5000) {
            state = nextValue(state).toInt()
            val objectIndex = (state ushr 1) % references.size
            val fieldIndex = (state ushr 12) % 3
            val value = nextValue(state)
            heap.setStructField(references[objectIndex], fieldIndex, value)
            model[objectIndex * 3 + fieldIndex] = value
            assertEquals(
                model[objectIndex * 3 + fieldIndex],
                heap.getStructField(references[objectIndex], fieldIndex),
            )
            operation++
        }

        assertEquals(23, heap.structSemanticId(references.last()))
        heap.checkInvariants()
    }

    @Test
    fun `range allocation copies the registered width from its source offset`() {
        val heap = GarbageCollectedHeap()
        val descriptor = heap.registerStruct(31, 3, intArrayOf(1))
        val source = longArrayOf(-1, -2, 11, 22, 33, -3)

        val reference = heap.allocateStruct(descriptor, source, 2)
        source.fill(0)

        assertEquals(11, heap.getStructField(reference, 0))
        assertEquals(22, heap.getStructField(reference, 1))
        assertEquals(33, heap.getStructField(reference, 2))
        heap.checkInvariants()
    }

    @Test
    fun `invalid range and extra exact payload fail before reservation`() {
        val heap = GarbageCollectedHeap()
        val descriptor = heap.registerStruct(31, 2, intArrayOf())

        assertFailsWith<IllegalArgumentException> {
            heap.allocateStruct(descriptor, longArrayOf(1, 2, 3))
        }
        assertFailsWith<IllegalArgumentException> {
            heap.allocateStruct(descriptor, longArrayOf(1, 2), -1)
        }
        assertFailsWith<IllegalArgumentException> {
            heap.allocateStruct(descriptor, longArrayOf(1, 2), 1)
        }

        assertEquals(0L, heap.snapshotStatistics().allocatedSlotWords)
        assertEquals(0, heap.snapshotStatistics().activePageCount)
        heap.checkInvariants()
    }

    private fun nextValue(value: Int): Long =
        (value.toLong() * 1_664_525L + 1_013_904_223L) and Int.MAX_VALUE.toLong()
}
