package io.github.charlietap.chasm.gc

import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_ARRAY
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_MASK
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class GarbageCollectedHeapTest {

    @Test
    fun `drop releases storage and is idempotent`() {
        val heap = GarbageCollectedHeap()
        val descriptor = heap.registerStruct(1, 2048, intArrayOf())
        heap.allocateStruct(descriptor, LongArray(2048))

        heap.drop()
        heap.drop()

        val statistics = heap.snapshotStatistics()
        assertEquals(0, statistics.activePageCount)
        assertEquals(0L, statistics.committedWords)
        assertEquals(0L, statistics.allocatedSlotWords)
    }

    @Test
    fun `an empty heap has no committed storage or descriptors`() {
        val heap = GarbageCollectedHeap()

        val statistics = heap.snapshotStatistics()

        assertEquals(0, statistics.activePageCount)
        assertEquals(0L, statistics.committedWords)
        assertEquals(0L, statistics.allocatedSlotWords)
        assertEquals(0L, statistics.bitmapWords)
        assertEquals(0, statistics.descriptorCount)
        assertEquals(0, statistics.recycledPageCount)
        heap.checkInvariants()
    }

    @Test
    fun `configuration rejects impossible directory and page limits`() {
        assertFailsWith<IllegalArgumentException> {
            GarbageCollectedHeap.Configuration(maximumPageCount = 0)
        }
        assertFailsWith<IllegalArgumentException> {
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 1,
            )
        }
        assertFailsWith<IllegalArgumentException> {
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 3,
                maximumPageCount = 1,
            )
        }
        assertFailsWith<IllegalArgumentException> {
            GarbageCollectedHeap.Configuration(
                initialDescriptorDirectoryCapacity = 0,
            )
        }
        assertFailsWith<IllegalArgumentException> {
            GarbageCollectedHeap.Configuration(
                initialDescriptorDirectoryCapacity = (1 shl 30) + 1,
            )
        }
        GarbageCollectedHeap.Configuration(maximumPageCount = 524_287)
        assertFailsWith<IllegalArgumentException> {
            GarbageCollectedHeap.Configuration(maximumPageCount = 524_288)
        }
    }

    @Test
    fun `struct registration copies sorts and retains one layout authority`() {
        val heap = GarbageCollectedHeap()
        val callerIndices = intArrayOf(2, 0)

        val descriptorKey = heap.registerStruct(
            semanticId = 7,
            payloadWords = 3,
            referenceFieldIndices = callerIndices,
        )
        callerIndices.fill(1)

        assertEquals(
            descriptorKey,
            heap.registerStruct(
                semanticId = 7,
                payloadWords = 3,
                referenceFieldIndices = intArrayOf(0, 2),
            ),
        )
        assertFailsWith<IllegalArgumentException> {
            heap.registerStruct(7, 3, intArrayOf(1))
        }
        assertFailsWith<IllegalArgumentException> {
            heap.registerArray(7, elementsMayContainReferences = false)
        }
        heap.checkInvariants()
    }

    @Test
    fun `registration rejects invalid payload and reference indices`() {
        val heap = GarbageCollectedHeap()

        heap.registerStruct(0, 2048, intArrayOf())
        assertFailsWith<IllegalArgumentException> {
            heap.registerStruct(1, 2049, intArrayOf())
        }
        assertFailsWith<IllegalArgumentException> {
            heap.registerStruct(1, -1, intArrayOf())
        }
        assertFailsWith<IllegalArgumentException> {
            heap.registerStruct(1, 1, intArrayOf(1))
        }
        assertFailsWith<IllegalArgumentException> {
            heap.registerStruct(1, 2, intArrayOf(1, 1))
        }
        assertFailsWith<IllegalArgumentException> {
            heap.registerException(-1, 0, intArrayOf())
        }
    }

    @Test
    fun `exception descriptors preserve tag identity despite identical layouts`() {
        val heap = GarbageCollectedHeap()

        val first = heap.registerException(3, 2, intArrayOf(1))
        val second = heap.registerException(4, 2, intArrayOf(1))

        assertEquals((3 shl 2) or 3, first)
        assertEquals((4 shl 2) or 3, second)
        assertNotEquals(first, second)
        assertEquals(2, heap.snapshotStatistics().descriptorCount)
    }

    @Test
    fun `array registration is idempotent and rejects a conflicting element layout`() {
        val heap = GarbageCollectedHeap()

        val descriptorKey = heap.registerArray(9, elementsMayContainReferences = true)

        assertEquals(
            descriptorKey,
            heap.registerArray(9, elementsMayContainReferences = true),
        )
        assertFailsWith<IllegalArgumentException> {
            heap.registerArray(9, elementsMayContainReferences = false)
        }
        heap.checkInvariants()
    }

    @Test
    fun `exception registration copies its layout and rejects a conflict`() {
        val heap = GarbageCollectedHeap()
        val callerIndices = intArrayOf(1, 0)

        val descriptorKey = heap.registerException(9, 2, callerIndices)
        callerIndices.fill(0)

        assertEquals(descriptorKey, heap.registerException(9, 2, intArrayOf(0, 1)))
        assertFailsWith<IllegalArgumentException> {
            heap.registerException(9, 2, intArrayOf(1))
        }
        heap.checkInvariants()
    }

    @Test
    fun `packed descriptor keys preserve every semantic bit`() {
        val heap = GarbageCollectedHeap()
        val maximumSemanticId = (1 shl 30) - 1

        assertEquals(1, heap.descriptorKeyForTesting(0, 1))
        assertEquals(-1, heap.descriptorKeyForTesting(maximumSemanticId, 3))
        assertFailsWith<IllegalArgumentException> {
            heap.descriptorKeyForTesting(-1, 1)
        }
    }

    @Test
    fun `candidate validation rejects forged and unallocated references`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerStruct(1, 3, intArrayOf())
        val pageId = heap.commitFixedPageForTesting(descriptorKey)
        val allocatedReference = heap.rawReferenceForTesting(pageId, 0)
        val unallocatedReference = heap.rawReferenceForTesting(pageId, 1)

        heap.setSlotAllocatedForTesting(pageId, 0)

        assertTrue(heap.isAllocatedReferenceForTesting(allocatedReference))
        assertFalse(heap.isAllocatedReferenceForTesting(unallocatedReference))
        assertFalse(heap.isAllocatedReferenceForTesting(0L))
        assertFalse(heap.isAllocatedReferenceForTesting(-1L))
        assertFalse(heap.isAllocatedReferenceForTesting(allocatedReference + (1L shl 8)))
        val wrongKind =
            (allocatedReference and RV_TYPE_MASK.inv()) or RV_TYPE_ARRAY
        assertFalse(heap.isAllocatedReferenceForTesting(wrongKind))
        heap.checkInvariants()
    }

    @Test
    fun `first and last page slots validate across bitmap word boundaries`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerStruct(0, 1, intArrayOf())
        val pageId = heap.commitFixedPageForTesting(descriptorKey)
        val lastSlot = heap.slotCountForTesting(pageId) - 1

        heap.setSlotAllocatedForTesting(pageId, 0)
        heap.setSlotAllocatedForTesting(pageId, 63)
        heap.setSlotAllocatedForTesting(pageId, 64)
        heap.setSlotAllocatedForTesting(pageId, lastSlot)

        assertTrue(heap.isAllocatedReferenceForTesting(heap.rawReferenceForTesting(pageId, 0)))
        assertTrue(heap.isAllocatedReferenceForTesting(heap.rawReferenceForTesting(pageId, 63)))
        assertTrue(heap.isAllocatedReferenceForTesting(heap.rawReferenceForTesting(pageId, 64)))
        assertTrue(heap.isAllocatedReferenceForTesting(heap.rawReferenceForTesting(pageId, lastSlot)))
        assertEquals(4L, heap.snapshotStatistics().allocatedSlotWords)
    }

    @Test
    fun `bitmap geometry covers slot counts around one bitmap word`() {
        val heap = GarbageCollectedHeap()
        val thirtyOneWordDescriptor = heap.registerStruct(1, 31, intArrayOf())
        val thirtyTwoWordDescriptor = heap.registerStruct(2, 32, intArrayOf())
        val thirtyThreeWordDescriptor = heap.registerStruct(3, 33, intArrayOf())

        val thirtyOneWordPage = heap.commitFixedPageForTesting(thirtyOneWordDescriptor)
        val thirtyTwoWordPage = heap.commitFixedPageForTesting(thirtyTwoWordDescriptor)
        val thirtyThreeWordPage = heap.commitFixedPageForTesting(thirtyThreeWordDescriptor)

        assertEquals(66, heap.slotCountForTesting(thirtyOneWordPage))
        assertEquals(64, heap.slotCountForTesting(thirtyTwoWordPage))
        assertEquals(62, heap.slotCountForTesting(thirtyThreeWordPage))
        heap.setSlotAllocatedForTesting(thirtyOneWordPage, 65)
        heap.setSlotAllocatedForTesting(thirtyTwoWordPage, 63)
        heap.setSlotAllocatedForTesting(thirtyThreeWordPage, 61)
        assertEquals(31L + 32L + 33L, heap.snapshotStatistics().allocatedSlotWords)
    }

    @Test
    fun `candidate validation agrees with an exhaustive page model`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerStruct(0, 3, intArrayOf())
        val pageId = heap.commitFixedPageForTesting(descriptorKey)
        val allocatedSlots = intArrayOf(0, 1, 63, 64, heap.slotCountForTesting(pageId) - 1)
        var index = 0
        while (index < allocatedSlots.size) {
            heap.setSlotAllocatedForTesting(pageId, allocatedSlots[index])
            index++
        }
        val pageStartReference = heap.rawReferenceForTesting(pageId, 0)
        val pageStartAddressBits = pageStartReference and RV_TYPE_MASK.inv()

        var wordOffset = 0
        while (wordOffset < 2048) {
            var tag = 0L
            while (tag <= 9L) {
                val candidate = pageStartAddressBits + (wordOffset.toLong() shl 8) or tag
                val slotIndex = wordOffset / 3
                val expected =
                    tag == 3L &&
                        wordOffset % 3 == 0 &&
                        allocatedSlots.any { it == slotIndex }
                assertEquals(expected, heap.isAllocatedReferenceForTesting(candidate))
                tag++
            }
            wordOffset++
        }

        val overflowedAddress = (Int.MAX_VALUE.toLong() + 1L) shl 8 or 3L
        assertFalse(heap.isAllocatedReferenceForTesting(overflowedAddress))
    }

    @Test
    fun `retasking a wide page to one-word slots grows bitmap capacity safely`() {
        val heap = GarbageCollectedHeap()
        val wideDescriptor = heap.registerStruct(1, 128, intArrayOf())
        val narrowDescriptor = heap.registerStruct(2, 1, intArrayOf())
        val pageId = heap.commitFixedPageForTesting(wideDescriptor)
        heap.setSlotAllocatedForTesting(pageId, 0)
        heap.clearSlotAllocatedForTesting(pageId, 0)
        heap.recycleEmptyPageForTesting(pageId)

        val reusedPageId = heap.commitFixedPageForTesting(narrowDescriptor)
        heap.setSlotAllocatedForTesting(reusedPageId, 0)

        assertEquals(pageId, reusedPageId)
        assertTrue(
            heap.isAllocatedReferenceForTesting(heap.rawReferenceForTesting(reusedPageId, 0)),
        )
        assertEquals(64L, heap.snapshotStatistics().bitmapWords)
        heap.checkInvariants()
    }

    @Test
    fun `retasking one-word slots to a wide page retains zero bitmap capacity`() {
        val heap = GarbageCollectedHeap()
        val narrowDescriptor = heap.registerStruct(1, 1, intArrayOf())
        val wideDescriptor = heap.registerStruct(2, 128, intArrayOf())
        val pageId = heap.commitFixedPageForTesting(narrowDescriptor)
        heap.setSlotAllocatedForTesting(pageId, 0)
        heap.clearSlotAllocatedForTesting(pageId, 0)
        heap.recycleEmptyPageForTesting(pageId)

        val reusedPageId = heap.commitFixedPageForTesting(wideDescriptor)
        heap.setSlotAllocatedForTesting(reusedPageId, 0)

        assertEquals(pageId, reusedPageId)
        assertEquals(64L, heap.snapshotStatistics().bitmapWords)
        heap.checkInvariants()
    }

    @Test
    fun `retasking between kinds rejects the old reference tag`() {
        val heap = GarbageCollectedHeap()
        val structDescriptor = heap.registerStruct(1, 1, intArrayOf())
        val exceptionDescriptor = heap.registerException(1, 1, intArrayOf())
        val pageId = heap.commitFixedPageForTesting(structDescriptor)
        heap.setSlotAllocatedForTesting(pageId, 0)
        val oldReference = heap.rawReferenceForTesting(pageId, 0)
        heap.clearSlotAllocatedForTesting(pageId, 0)
        heap.recycleEmptyPageForTesting(pageId)

        val reusedPageId = heap.commitFixedPageForTesting(exceptionDescriptor)
        heap.setSlotAllocatedForTesting(reusedPageId, 0)
        val newReference = heap.rawReferenceForTesting(reusedPageId, 0)

        assertEquals(pageId, reusedPageId)
        assertFalse(heap.isAllocatedReferenceForTesting(oldReference))
        assertTrue(heap.isAllocatedReferenceForTesting(newReference))
        heap.checkInvariants()
    }

    @Test
    fun `page directory grows and empty page IDs are recycled once`() {
        val heap = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 2,
                initialDescriptorDirectoryCapacity = 1,
                maximumPageCount = 3,
            ),
        )
        val descriptorKey = heap.registerStruct(0, 4, intArrayOf())
        val firstPageId = heap.commitFixedPageForTesting(descriptorKey)
        val secondPageId = heap.commitFixedPageForTesting(descriptorKey)

        assertEquals(1, firstPageId)
        assertEquals(2, secondPageId)
        assertEquals(2, heap.snapshotStatistics().activePageCount)
        heap.recycleEmptyPageForTesting(firstPageId)
        assertEquals(1, heap.snapshotStatistics().recycledPageCount)

        val reusedPageId = heap.commitFixedPageForTesting(descriptorKey)

        assertEquals(firstPageId, reusedPageId)
        assertEquals(0, heap.snapshotStatistics().recycledPageCount)
        assertEquals(2, heap.snapshotStatistics().activePageCount)
        heap.checkInvariants()
    }

    @Test
    fun `page limit failure leaves published state unchanged`() {
        val heap = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 2,
                maximumPageCount = 1,
            ),
        )
        val descriptorKey = heap.registerException(0, 0, intArrayOf())
        heap.commitFixedPageForTesting(descriptorKey)

        assertFailsWith<OutOfMemoryError> {
            heap.commitFixedPageForTesting(descriptorKey)
        }

        assertEquals(1, heap.snapshotStatistics().activePageCount)
        assertEquals(2048L, heap.snapshotStatistics().committedWords)
        heap.checkInvariants()
    }

    @Test
    fun `payload growth clamps to a non-power-of-two page limit`() {
        val heap = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 2,
                maximumPageCount = 3,
            ),
        )
        val descriptorKey = heap.registerStruct(0, 2048, intArrayOf())

        assertEquals(1, heap.commitFixedPageForTesting(descriptorKey))
        assertEquals(2, heap.commitFixedPageForTesting(descriptorKey))
        assertEquals(3, heap.commitFixedPageForTesting(descriptorKey))
        val before = heap.snapshotStatistics()
        assertEquals(6_144L, before.committedWords)

        assertFailsWith<OutOfMemoryError> {
            heap.commitFixedPageForTesting(descriptorKey)
        }

        val after = heap.snapshotStatistics()
        assertEquals(before.activePageCount, after.activePageCount)
        assertEquals(before.committedWords, after.committedWords)
        assertEquals(before.pageDirectoryCapacity, after.pageDirectoryCapacity)
        heap.checkInvariants()
    }
}
