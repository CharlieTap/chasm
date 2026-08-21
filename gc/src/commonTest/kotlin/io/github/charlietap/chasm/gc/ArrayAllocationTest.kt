package io.github.charlietap.chasm.gc

import io.github.charlietap.chasm.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_STRUCT
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ArrayAllocationTest {

    @Test
    fun `filled allocation preserves logical length values and typed nulls`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerArray(7, elementsMayContainReferences = true)
        val typedNull = 0x0000_002A_0000_0001L

        val empty = heap.allocateArrayFilled(descriptorKey, 0, typedNull)
        val array = heap.allocateArrayFilled(descriptorKey, 17, typedNull)

        assertEquals(0, heap.arrayLength(empty))
        assertEquals(17, heap.arrayLength(array))
        assertEquals(typedNull, heap.getArrayElement(array, 0))
        assertEquals(typedNull, heap.getArrayElement(array, 16))
        assertEquals(7, heap.arraySemanticId(array))
        assertEquals(18, heap.arraySlotWordsForTesting(array))
        heap.checkInvariants()
    }

    @Test
    fun `every size class boundary selects the least fitting capacity`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerArray(0, elementsMayContainReferences = false)
        val capacities = (0..96).toList() + listOf(128, 192, 256, 384, 512, 768, 1023)

        capacities.forEachIndexed { index, capacity ->
            val lowerCapacity = if (index == 0) -1 else capacities[index - 1]
            val lengths = intArrayOf(lowerCapacity + 1, capacity)
            lengths.forEach { length ->
                val reference = heap.allocateArrayFilled(descriptorKey, length, length.toLong())
                assertEquals(capacity + 1, heap.arraySlotWordsForTesting(reference), "length=$length")
                assertFalse(heap.isDedicatedArrayForTesting(reference), "length=$length")
                assertEquals(length, heap.arrayLength(reference))
            }
        }

        val dedicated = heap.allocateArrayFilled(descriptorKey, 1024, 9)
        assertTrue(heap.isDedicatedArrayForTesting(dedicated))
        assertEquals(1025, heap.arraySlotWordsForTesting(dedicated))
        heap.checkInvariants()
    }

    @Test
    fun `ordinary arrays fill a homogeneous page then continue on another`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerArray(0, elementsMayContainReferences = false)
        val references = LongArray(22)
        references.indices.forEach { index ->
            references[index] = heap.allocateArrayFilled(descriptorKey, 96, index.toLong())
        }

        assertEquals(2, heap.snapshotStatistics().activePageCount)
        assertEquals(0, heap.getArrayElement(references.first(), 95))
        assertEquals(21, heap.getArrayElement(references.last(), 95))
        heap.checkInvariants()
    }

    @Test
    fun `ordinary free reuse overwrites logical content without exposing class slack`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerArray(0, elementsMayContainReferences = false)
        val references = LongArray(15) { index ->
            heap.allocateArrayFilled(descriptorKey, 128, 1_000L + index)
        }
        val released = references[7]
        heap.releaseArrayForTesting(released)

        val reused = heap.allocateArrayFilled(descriptorKey, 97, -7)

        assertEquals(released, reused)
        assertEquals(97, heap.arrayLength(reused))
        assertEquals(-7, heap.getArrayElement(reused, 0))
        assertEquals(-7, heap.getArrayElement(reused, 96))
        assertFailsWith<IllegalArgumentException> { heap.getArrayElement(reused, 97) }
        val statistics = heap.snapshotStatistics()
        assertEquals(1, statistics.activePageCount)
        assertEquals(15L * 129L, statistics.allocatedSlotWords)
        assertEquals(31L, statistics.arrayClassSlackWords)
        heap.checkInvariants()
    }

    @Test
    fun `recycled array page retasks across size classes with documented address reuse`() {
        val heap = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 2,
                maximumPageCount = 1,
            ),
        )
        val descriptorKey = heap.registerArray(0, elementsMayContainReferences = false)
        val wide = heap.allocateArrayFilled(descriptorKey, 768, Long.MAX_VALUE)
        val pageId = ((wide ushr RV_SHIFT_BITS).toInt()) ushr 11
        heap.releaseArrayForTesting(wide)
        assertFailsWith<IllegalArgumentException> { heap.arraySemanticId(wide) }
        heap.recycleEmptyPageForTesting(pageId)

        val narrow = heap.allocateArrayFilled(descriptorKey, 3, 12)

        assertEquals(pageId, ((narrow ushr RV_SHIFT_BITS).toInt()) ushr 11)
        assertEquals(wide, narrow)
        assertEquals(3, heap.arrayLength(narrow))
        assertEquals(12, heap.getArrayElement(narrow, 2))
        assertEquals(0, heap.arraySemanticId(wide))
        heap.checkInvariants()
    }

    @Test
    fun `element source is copied for ordinary and dedicated arrays`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerArray(2, elementsMayContainReferences = false)
        val ordinarySource = LongArray(40) { it.toLong() }
        val dedicatedSource = LongArray(1030) { 10_000L + it }

        val ordinary = heap.allocateArrayFromElements(descriptorKey, ordinarySource, 3, 32)
        val dedicated = heap.allocateArrayFromElements(descriptorKey, dedicatedSource, 2, 1024)
        ordinarySource.fill(-1)
        dedicatedSource.fill(-1)

        assertEquals(3, heap.getArrayElement(ordinary, 0))
        assertEquals(34, heap.getArrayElement(ordinary, 31))
        assertEquals(10_002, heap.getArrayElement(dedicated, 0))
        assertEquals(11_025, heap.getArrayElement(dedicated, 1023))
        assertEquals(2, heap.arraySemanticId(dedicated))
        heap.checkInvariants()
    }

    @Test
    fun `scalar access checks dynamic indices without exposing class slack`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerArray(0, elementsMayContainReferences = false)
        val reference = heap.allocateArrayFilled(descriptorKey, 17, 1)

        heap.setArrayElement(reference, 0, 10)
        heap.setArrayElement(reference, 16, 20)

        assertEquals(10, heap.getArrayElement(reference, 0))
        assertEquals(20, heap.getArrayElement(reference, 16))
        assertFailsWith<IllegalArgumentException> { heap.getArrayElement(reference, -1) }
        assertFailsWith<IllegalArgumentException> { heap.getArrayElement(reference, 17) }
        assertFailsWith<IllegalArgumentException> { heap.setArrayElement(reference, 24, 5) }
        heap.checkInvariants()
    }

    @Test
    fun `bulk copy is overlap correct in both directions`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerArray(0, elementsMayContainReferences = false)
        val forward = heap.allocateArrayFromElements(descriptorKey, LongArray(16) { it.toLong() }, 0, 16)
        val backward = heap.allocateArrayFromElements(descriptorKey, LongArray(16) { it.toLong() }, 0, 16)

        heap.copyArray(forward, 0, forward, 4, 12)
        heap.copyArray(backward, 4, backward, 0, 12)

        assertContentEquals(
            longArrayOf(0, 1, 2, 3, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
            materialize(heap, forward),
        )
        assertContentEquals(
            longArrayOf(4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 12, 13, 14, 15),
            materialize(heap, backward),
        )
        heap.checkInvariants()
    }

    @Test
    fun `bulk copy supports every ordinary and dedicated backing combination`() {
        val heap = GarbageCollectedHeap(GarbageCollectedHeap.Configuration(maximumPageCount = 16))
        val descriptorKey = heap.registerArray(0, elementsMayContainReferences = false)
        val ordinarySource = heap.allocateArrayFromElements(descriptorKey, LongArray(32) { it.toLong() }, 0, 32)
        val ordinaryDestination = heap.allocateArrayFilled(descriptorKey, 32, -1)
        val dedicatedSource = heap.allocateArrayFromElements(
            descriptorKey,
            LongArray(1024) { 1_000L + it },
            0,
            1024,
        )
        val dedicatedDestination = heap.allocateArrayFilled(descriptorKey, 1024, -1)

        heap.copyArray(ordinarySource, 4, ordinaryDestination, 8, 16)
        heap.copyArray(ordinarySource, 0, dedicatedDestination, 10, 32)
        heap.copyArray(dedicatedSource, 20, ordinaryDestination, 0, 8)
        heap.copyArray(dedicatedSource, 100, dedicatedDestination, 100, 64)

        assertEquals(4, heap.getArrayElement(ordinaryDestination, 8))
        assertEquals(19, heap.getArrayElement(ordinaryDestination, 23))
        assertEquals(1_020, heap.getArrayElement(ordinaryDestination, 0))
        assertEquals(0, heap.getArrayElement(dedicatedDestination, 10))
        assertEquals(31, heap.getArrayElement(dedicatedDestination, 41))
        assertEquals(1_100, heap.getArrayElement(dedicatedDestination, 100))
        assertEquals(1_163, heap.getArrayElement(dedicatedDestination, 163))
        heap.checkInvariants()
    }

    @Test
    fun `dedicated bulk copy handles overlap ends and invalid ranges atomically`() {
        val heap = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 2,
                maximumPageCount = 8,
            ),
        )
        val descriptorKey = heap.registerArray(0, elementsMayContainReferences = false)
        val forward = heap.allocateArrayFromElements(
            descriptorKey,
            LongArray(1024) { it.toLong() },
            0,
            1024,
        )
        val backward = heap.allocateArrayFromElements(
            descriptorKey,
            LongArray(1024) { it.toLong() },
            0,
            1024,
        )

        heap.copyArray(forward, 0, forward, 4, 1020)
        heap.copyArray(backward, 4, backward, 0, 1020)
        heap.copyArray(forward, 1024, backward, 1024, 0)
        assertEquals(0, heap.getArrayElement(forward, 4))
        assertEquals(1019, heap.getArrayElement(forward, 1023))
        assertEquals(4, heap.getArrayElement(backward, 0))
        assertEquals(1023, heap.getArrayElement(backward, 1019))
        val beforeFailure = materialize(heap, backward)

        assertFailsWith<IllegalArgumentException> {
            heap.copyArray(forward, Int.MIN_VALUE, backward, 0, 0)
        }
        assertFailsWith<IllegalArgumentException> {
            heap.copyArray(forward, 0, backward, Int.MAX_VALUE, 0)
        }
        assertFailsWith<IllegalArgumentException> {
            heap.copyArray(forward, 1024, backward, 1024, 1)
        }
        assertContentEquals(beforeFailure, materialize(heap, backward))
        heap.checkInvariants()
    }

    @Test
    fun `fill and source initialization validate before mutation`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerArray(0, elementsMayContainReferences = false)
        val reference = heap.allocateArrayFilled(descriptorKey, 8, 1)

        heap.fillArray(reference, 2, 4, 9)
        heap.fillArray(reference, 8, 0, 7)
        heap.initializeArrayFromElements(reference, 1, longArrayOf(20, 21, 22), 0, 3)
        val beforeFailure = materialize(heap, reference)

        assertFailsWith<IllegalArgumentException> {
            heap.fillArray(reference, Int.MAX_VALUE, 1, 0)
        }
        assertFailsWith<IllegalArgumentException> {
            heap.initializeArrayFromElements(reference, 7, longArrayOf(30, 31), 0, 2)
        }
        assertContentEquals(beforeFailure, materialize(heap, reference))
        heap.checkInvariants()
    }

    @Test
    fun `range arithmetic rejects overflow shaped inputs before mutation`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerArray(0, elementsMayContainReferences = false)
        val reference = heap.allocateArrayFilled(descriptorKey, 4, 7)
        val beforeFailure = materialize(heap, reference)

        assertFailsWith<IllegalArgumentException> {
            heap.fillArray(reference, Int.MIN_VALUE, 0, 1)
        }
        assertFailsWith<IllegalArgumentException> {
            heap.fillArray(reference, 1, Int.MAX_VALUE, 1)
        }
        assertFailsWith<IllegalArgumentException> {
            heap.copyArray(reference, Int.MAX_VALUE, reference, 0, 0)
        }
        assertFailsWith<IllegalArgumentException> {
            heap.initializeArrayFromData(
                reference,
                0,
                ubyteArrayOf(1u),
                0,
                Int.MAX_VALUE,
                8,
            )
        }
        assertFailsWith<IllegalArgumentException> {
            heap.allocateArrayFilled(descriptorKey, Int.MAX_VALUE, 0)
        }

        assertContentEquals(beforeFailure, materialize(heap, reference))
        heap.checkInvariants()
    }

    @Test
    fun `data initialization preserves raw little endian number bits`() {
        val heap = GarbageCollectedHeap()
        val descriptorKey = heap.registerArray(0, elementsMayContainReferences = false)
        val bytes = ubyteArrayOf(
            0x80u,
            0xFFu,
            0x00u,
            0x80u,
            0x01u,
            0x00u,
            0xC0u,
            0x7Fu,
            0x00u,
            0x00u,
            0x00u,
            0x00u,
            0x00u,
            0x00u,
            0xF8u,
            0xFFu,
        )

        val i8 = heap.allocateArrayFromData(descriptorKey, bytes, 0, 1, 1)
        val i16 = heap.allocateArrayFromData(descriptorKey, bytes, 2, 1, 2)
        val f32 = heap.allocateArrayFromData(descriptorKey, bytes, 4, 1, 4)
        val f64 = heap.allocateArrayFromData(descriptorKey, bytes, 8, 1, 8)
        val negativeF32 = heap.allocateArrayFromData(
            descriptorKey,
            ubyteArrayOf(0u, 0u, 0u, 0x80u),
            0,
            1,
            4,
        )
        val dedicatedBytes = UByteArray(1024 * 4)
        dedicatedBytes[0] = 1u
        dedicatedBytes[dedicatedBytes.lastIndex] = 0x80u
        val dedicated = heap.allocateArrayFromData(descriptorKey, dedicatedBytes, 0, 1024, 4)
        val ordinaryInitialized = heap.allocateArrayFilled(descriptorKey, 2, 0)
        val dedicatedInitialized = heap.allocateArrayFilled(descriptorKey, 1024, 0)
        val initializationBytes = ubyteArrayOf(0x34u, 0x12u, 0x00u, 0x80u)
        heap.initializeArrayFromData(ordinaryInitialized, 0, initializationBytes, 0, 2, 2)
        heap.initializeArrayFromData(dedicatedInitialized, 1022, initializationBytes, 0, 2, 2)

        assertEquals(0x80, heap.getArrayElement(i8, 0))
        assertEquals(Short.MIN_VALUE.toLong(), heap.getArrayElement(i16, 0))
        assertEquals(0x7FC00001, heap.getArrayElement(f32, 0))
        assertEquals(0xFFF8000000000000uL.toLong(), heap.getArrayElement(f64, 0))
        assertEquals(Int.MIN_VALUE.toLong(), heap.getArrayElement(negativeF32, 0))
        assertEquals(1, heap.getArrayElement(dedicated, 0))
        assertEquals(Int.MIN_VALUE.toLong(), heap.getArrayElement(dedicated, 1023))
        assertEquals(0x1234, heap.getArrayElement(ordinaryInitialized, 0))
        assertEquals(Short.MIN_VALUE.toLong(), heap.getArrayElement(ordinaryInitialized, 1))
        assertEquals(0x1234, heap.getArrayElement(dedicatedInitialized, 1022))
        assertEquals(Short.MIN_VALUE.toLong(), heap.getArrayElement(dedicatedInitialized, 1023))
        assertFailsWith<IllegalArgumentException> {
            heap.allocateArrayFromData(descriptorKey, bytes, -1, 1, 1)
        }
        assertFailsWith<IllegalArgumentException> {
            heap.initializeArrayFromData(dedicatedInitialized, 0, bytes, bytes.size, 1, 1)
        }
        heap.checkInvariants()
    }

    @Test
    fun `dedicated IDs are recycled without retaining backing payload`() {
        val heap = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 2,
                maximumPageCount = 4,
            ),
        )
        val firstDescriptor = heap.registerArray(3, elementsMayContainReferences = true)
        val secondDescriptor = heap.registerArray(4, elementsMayContainReferences = false)
        val first = heap.allocateArrayFilled(firstDescriptor, 1024, 1)
        val second = heap.allocateArrayFilled(firstDescriptor, 1024, 2)
        val firstAddress = (first ushr RV_SHIFT_BITS).toInt()
        assertTrue(firstAddress and (1 shl 30) != 0)
        assertNotEquals(0, firstAddress and ((1 shl 30) - 1))
        heap.releaseArrayForTesting(first)

        val afterRelease = heap.snapshotStatistics()
        assertEquals(1, afterRelease.dedicatedArrayCount)
        assertEquals(1025L, afterRelease.dedicatedPayloadWords)
        assertFalse(heap.isAllocatedReferenceForTesting(first))

        val reused = heap.allocateArrayFilled(secondDescriptor, 1024, 3)
        assertEquals(first, reused)
        assertNotEquals(second, reused)
        assertEquals(3, heap.getArrayElement(reused, 1023))
        assertEquals(4, heap.arraySemanticId(reused))
        assertEquals(2, heap.snapshotStatistics().dedicatedArrayCount)
        heap.checkInvariants()
    }

    @Test
    fun `ordinary array pages recycle across classes and fixed layouts`() {
        val heap = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 2,
                maximumPageCount = 1,
            ),
        )
        val arrayDescriptor = heap.registerArray(0, elementsMayContainReferences = false)
        val structDescriptor = heap.registerStruct(1, 4, intArrayOf())
        val reference = heap.allocateArrayFilled(arrayDescriptor, 17, Long.MAX_VALUE)
        val pageId = ((reference ushr RV_SHIFT_BITS).toInt()) ushr 11
        heap.releaseArrayForTesting(reference)
        heap.recycleEmptyPageForTesting(pageId)

        val struct = heap.allocateStruct(structDescriptor, longArrayOf(1, 2, 3, 4))

        assertEquals(1, heap.getStructField(struct, 0))
        assertEquals(4, heap.getStructField(struct, 3))
        assertFailsWith<IllegalArgumentException> { heap.arraySemanticId(reference) }
        heap.checkInvariants()
    }

    @Test
    fun `invalid constructors and retained budget failures publish no state`() {
        val heap = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 2,
                maximumPageCount = 1,
            ),
        )
        val arrayDescriptor = heap.registerArray(0, elementsMayContainReferences = false)
        val structDescriptor = heap.registerStruct(1, 1, intArrayOf())

        assertFailsWith<IllegalArgumentException> {
            heap.allocateArrayFilled(arrayDescriptor, -1, 0)
        }
        assertFailsWith<IllegalArgumentException> {
            heap.allocateArrayFromElements(arrayDescriptor, longArrayOf(1), 1, 1)
        }
        assertFailsWith<IllegalArgumentException> {
            heap.allocateArrayFromData(arrayDescriptor, ubyteArrayOf(), 0, 1, 16)
        }
        assertFailsWith<IllegalArgumentException> {
            heap.allocateArrayFilled(structDescriptor, 1, 0)
        }
        assertFailsWith<GuestHeapOutOfMemoryError> {
            heap.allocateArrayFilled(arrayDescriptor, 2048, 0)
        }

        val statistics = heap.snapshotStatistics()
        assertEquals(0, statistics.activePageCount)
        assertEquals(0, statistics.dedicatedArrayCount)
        assertEquals(0L, statistics.retainedPayloadWords)
        heap.checkInvariants()
    }

    @Test
    fun `shared retained budget failures are atomic in ordinary and dedicated order`() {
        val dedicatedFirst = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 2,
                maximumPageCount = 1,
            ),
        )
        val dedicatedFirstDescriptor =
            dedicatedFirst.registerArray(0, elementsMayContainReferences = false)
        val dedicated = dedicatedFirst.allocateArrayFilled(dedicatedFirstDescriptor, 1024, 11)
        val beforeOrdinaryFailure = dedicatedFirst.snapshotStatistics()

        assertFailsWith<GuestHeapOutOfMemoryError> {
            dedicatedFirst.allocateArrayFilled(dedicatedFirstDescriptor, 1, 22)
        }
        val afterOrdinaryFailure = dedicatedFirst.snapshotStatistics()
        assertEquals(11, dedicatedFirst.getArrayElement(dedicated, 1023))
        assertEquals(beforeOrdinaryFailure.committedWords, afterOrdinaryFailure.committedWords)
        assertEquals(
            beforeOrdinaryFailure.dedicatedPayloadWords,
            afterOrdinaryFailure.dedicatedPayloadWords,
        )
        assertEquals(beforeOrdinaryFailure.allocatedSlotWords, afterOrdinaryFailure.allocatedSlotWords)
        dedicatedFirst.checkInvariants()

        val ordinaryFirst = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 2,
                maximumPageCount = 1,
            ),
        )
        val ordinaryFirstDescriptor =
            ordinaryFirst.registerArray(0, elementsMayContainReferences = false)
        val ordinary = ordinaryFirst.allocateArrayFilled(ordinaryFirstDescriptor, 1, 33)
        val beforeDedicatedFailure = ordinaryFirst.snapshotStatistics()

        assertFailsWith<GuestHeapOutOfMemoryError> {
            ordinaryFirst.allocateArrayFilled(ordinaryFirstDescriptor, 1024, 44)
        }
        val afterDedicatedFailure = ordinaryFirst.snapshotStatistics()
        assertEquals(33, ordinaryFirst.getArrayElement(ordinary, 0))
        assertEquals(beforeDedicatedFailure.committedWords, afterDedicatedFailure.committedWords)
        assertEquals(
            beforeDedicatedFailure.dedicatedPayloadWords,
            afterDedicatedFailure.dedicatedPayloadWords,
        )
        assertEquals(beforeDedicatedFailure.allocatedSlotWords, afterDedicatedFailure.allocatedSlotWords)
        ordinaryFirst.checkInvariants()
    }

    @Test
    fun `statistics separate ordinary slack dedicated backing and retained capacity`() {
        val heap = GarbageCollectedHeap(
            GarbageCollectedHeap.Configuration(
                initialPageDirectoryCapacity = 2,
                maximumPageCount = 4,
            ),
        )
        val descriptorKey = heap.registerArray(0, elementsMayContainReferences = false)
        val ordinary = heap.allocateArrayFilled(descriptorKey, 97, 1)
        val dedicated = heap.allocateArrayFilled(descriptorKey, 1024, 2)

        val allocated = heap.snapshotStatistics()
        assertEquals(2048L, allocated.committedWords)
        assertEquals(1025L, allocated.dedicatedPayloadWords)
        assertEquals(3073L, allocated.retainedPayloadWords)
        assertEquals(129L + 1025L, allocated.allocatedSlotWords)
        assertEquals(31L, allocated.arrayClassSlackWords)
        assertEquals(1, allocated.activePageCount)
        assertEquals(1, allocated.dedicatedArrayCount)

        heap.releaseArrayForTesting(ordinary)
        heap.releaseArrayForTesting(dedicated)

        val released = heap.snapshotStatistics()
        assertEquals(2048L, released.committedWords)
        assertEquals(0L, released.dedicatedPayloadWords)
        assertEquals(2048L, released.retainedPayloadWords)
        assertEquals(0L, released.allocatedSlotWords)
        assertEquals(0L, released.arrayClassSlackWords)
        assertEquals(0, released.dedicatedArrayCount)
        heap.checkInvariants()
    }

    @Test
    fun `checked array identity rejects null wrong kind interior and released references`() {
        val heap = GarbageCollectedHeap()
        val arrayDescriptor = heap.registerArray(4, elementsMayContainReferences = false)
        val ordinary = heap.allocateArrayFilled(arrayDescriptor, 4, 0)
        val dedicated = heap.allocateArrayFilled(arrayDescriptor, 1024, 0)

        assertFailsWith<IllegalArgumentException> { heap.arraySemanticId(0) }
        assertFailsWith<IllegalArgumentException> {
            heap.arraySemanticId((ordinary and 0xFFL.inv()) or RV_TYPE_STRUCT)
        }
        assertFailsWith<IllegalArgumentException> {
            heap.arraySemanticId(ordinary + (1L shl RV_SHIFT_BITS))
        }
        val dedicatedZeroId = (1L shl 30 shl RV_SHIFT_BITS) or 4L
        assertFailsWith<IllegalArgumentException> { heap.arraySemanticId(dedicatedZeroId) }
        val unissuedDedicatedId =
            (((1L shl 30) or 2L) shl RV_SHIFT_BITS) or 4L
        assertFailsWith<IllegalArgumentException> { heap.arraySemanticId(unissuedDedicatedId) }
        assertFailsWith<IllegalArgumentException> {
            heap.arraySemanticId((dedicated and 0xFFL.inv()) or RV_TYPE_STRUCT)
        }
        heap.releaseArrayForTesting(dedicated)
        assertFailsWith<IllegalArgumentException> { heap.arraySemanticId(dedicated) }
        heap.checkInvariants()
    }

    @Test
    fun `random scalar fill and copy operations match a slow model`() {
        val random = Random(42)
        val heap = GarbageCollectedHeap(GarbageCollectedHeap.Configuration(maximumPageCount = 64))
        val descriptorKey = heap.registerArray(0, elementsMayContainReferences = false)
        val lengths = intArrayOf(0, 1, 17, 95, 96, 97, 511, 1023, 1024, 1100)
        val references = LongArray(lengths.size)
        val models = Array(lengths.size) { index -> LongArray(lengths[index]) }
        references.indices.forEach { index ->
            references[index] = heap.allocateArrayFilled(descriptorKey, lengths[index], 0)
        }

        repeat(2_000) {
            when (random.nextInt(3)) {
                0 -> {
                    val target = random.nextInt(references.size)
                    if (models[target].isNotEmpty()) {
                        val index = random.nextInt(models[target].size)
                        val value = random.nextLong()
                        heap.setArrayElement(references[target], index, value)
                        models[target][index] = value
                    }
                }
                1 -> {
                    val target = random.nextInt(references.size)
                    val offset = random.nextInt(models[target].size + 1)
                    val length = random.nextInt(models[target].size - offset + 1)
                    val value = random.nextLong()
                    heap.fillArray(references[target], offset, length, value)
                    models[target].fill(value, offset, offset + length)
                }
                else -> {
                    val source = random.nextInt(references.size)
                    val destination = random.nextInt(references.size)
                    val length = random.nextInt(minOf(models[source].size, models[destination].size) + 1)
                    val sourceOffset = random.nextInt(models[source].size - length + 1)
                    val destinationOffset = random.nextInt(models[destination].size - length + 1)
                    heap.copyArray(
                        references[source],
                        sourceOffset,
                        references[destination],
                        destinationOffset,
                        length,
                    )
                    models[source].copyInto(
                        models[destination],
                        destinationOffset,
                        sourceOffset,
                        sourceOffset + length,
                    )
                }
            }
            heap.checkInvariants()
        }

        references.indices.forEach { index ->
            assertContentEquals(models[index], materialize(heap, references[index]))
        }
    }

    private fun materialize(
        heap: GarbageCollectedHeap,
        reference: Long,
    ): LongArray = LongArray(heap.arrayLength(reference)) { index ->
        heap.getArrayElement(reference, index)
    }
}
