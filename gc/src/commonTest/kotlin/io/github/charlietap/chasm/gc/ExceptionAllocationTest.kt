package io.github.charlietap.chasm.gc

import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_STRUCT
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class ExceptionAllocationTest {

    @Test
    fun `exception payload is copied in semantic parameter order`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerException(7, 4, intArrayOf(1, 3))
        val fields = longArrayOf(11, 22, 33, 44)

        val reference = heap.allocateException(descriptorKey, fields)
        fields.fill(-1)

        assertEquals(11, heap.getExceptionField(reference, 0))
        assertEquals(22, heap.getExceptionField(reference, 1))
        assertEquals(33, heap.getExceptionField(reference, 2))
        assertEquals(44, heap.getExceptionField(reference, 3))
        assertEquals(7, heap.exceptionTagAddress(reference))
        heap.checkInvariants()
    }

    @Test
    fun `exception payload can be copied from a caller owned range`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerException(7, 3, intArrayOf(1))
        val source = longArrayOf(-1, 11, 22, 33, -2)

        val reference = heap.allocateException(descriptorKey, source, 1)
        source.fill(-3)

        assertEquals(11, heap.getExceptionField(reference, 0))
        assertEquals(22, heap.getExceptionField(reference, 1))
        assertEquals(33, heap.getExceptionField(reference, 2))
        assertFailsWith<IllegalArgumentException> {
            heap.allocateException(descriptorKey, source, 3)
        }
        heap.checkInvariants()
    }

    @Test
    fun `equal exception layouts retain distinct tag identities`() {
        val heap = GarbageCollectedHeap()
        val firstDescriptor = heap.registerException(3, 2, intArrayOf(0))
        val secondDescriptor = heap.registerException(9, 2, intArrayOf(0))

        val first = heap.allocateException(firstDescriptor, longArrayOf(1, 2))
        val second = heap.allocateException(secondDescriptor, longArrayOf(1, 2))

        assertNotEquals(firstDescriptor, secondDescriptor)
        assertEquals(3, heap.exceptionTagAddress(first))
        assertEquals(9, heap.exceptionTagAddress(second))
        heap.checkInvariants()
    }

    @Test
    fun `zero-field exceptions retain identity and occupy physical slots`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerException(0, 0, intArrayOf())

        val first = heap.allocateException(descriptorKey, longArrayOf())
        val second = heap.allocateException(descriptorKey, longArrayOf())

        assertNotEquals(first, second)
        assertEquals(0, heap.exceptionTagAddress(first))
        assertEquals(2L, heap.snapshotStatistics().allocatedSlotWords)
        heap.checkInvariants()
    }

    @Test
    fun `invalid exception allocation and hostile references are rejected`() {
        val heap = GarbageCollectedHeap()
        val exceptionDescriptor = heap.registerException(2, 1, intArrayOf())
        val structDescriptor = heap.registerStruct(2, 1, intArrayOf())
        val exceptionReference = heap.allocateException(exceptionDescriptor, longArrayOf(8))

        assertFailsWith<IllegalArgumentException> {
            heap.allocateException(exceptionDescriptor, longArrayOf())
        }
        assertFailsWith<IllegalArgumentException> {
            heap.allocateException(structDescriptor, longArrayOf(1))
        }
        assertFailsWith<IllegalArgumentException> {
            heap.exceptionTagAddress(0L)
        }
        assertFailsWith<IllegalArgumentException> {
            heap.exceptionTagAddress(
                (exceptionReference and 0xFFL.inv()) or RV_TYPE_STRUCT,
            )
        }
        assertFailsWith<IllegalArgumentException> {
            heap.exceptionTagAddress(exceptionReference + (1L shl 8))
        }
        assertEquals(2, heap.exceptionTagAddressOrNegative(exceptionReference))
        assertEquals(-1, heap.exceptionTagAddressOrNegative(0L))
        assertEquals(
            -1,
            heap.exceptionTagAddressOrNegative(
                (exceptionReference and 0xFFL.inv()) or RV_TYPE_STRUCT,
            ),
        )
        assertEquals(-1, heap.exceptionTagAddressOrNegative(exceptionReference + (1L shl 8)))
        heap.checkInvariants()
    }

    @Test
    fun `exception page boundary preserves first and last field access`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerException(1, 31, intArrayOf(0, 30))
        val fields = LongArray(31) { it.toLong() }
        var last = 0L
        repeat(67) {
            last = heap.allocateException(descriptorKey, fields)
        }

        assertEquals(0, heap.getExceptionField(last, 0))
        assertEquals(30, heap.getExceptionField(last, 30))
        assertEquals(2, heap.snapshotStatistics().activePageCount)
        heap.checkInvariants()
    }
}
