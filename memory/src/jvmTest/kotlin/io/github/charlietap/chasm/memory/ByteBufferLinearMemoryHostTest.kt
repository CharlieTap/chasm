package io.github.charlietap.chasm.memory

import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.host.readU16
import io.github.charlietap.chasm.host.readU32
import io.github.charlietap.chasm.host.readU64
import io.github.charlietap.chasm.host.readU8
import io.github.charlietap.chasm.memory.destruct.LinearMemoryDestructor
import io.github.charlietap.chasm.memory.read.NullTerminatedStringReader
import io.github.charlietap.chasm.memory.read.StringReader
import io.github.charlietap.chasm.memory.write.StringWriter
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.PAGE_SIZE
import java.nio.MappedByteBuffer
import java.util.concurrent.Executors
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

@OptIn(UnsafeHostApi::class)
class ByteBufferLinearMemoryHostTest {

    private val memories = mutableListOf<ByteBufferLinearMemory>()

    @AfterTest
    fun releaseMemories() {
        memories.forEach(ByteBufferLinearMemory::release)
        memories.clear()
    }

    @Test
    fun `reserves the declared maximum and grows within the fixed mapping`() {
        val memory = ByteBufferLinearMemory(
            pages = LinearMemory.Pages(1u),
            maximumPages = LinearMemory.Pages(4u),
        ).also(memories::add)
        val buffer = memory.unsafeBorrowByteBuffer()
        memory.writeI8(PAGE_SIZE - 1, 47)

        assertTrue(buffer.isDirect)
        assertTrue(buffer is MappedByteBuffer)
        assertTrue(memory.mapping is MappedByteBuffer)
        assertEquals(PAGE_SIZE * 4, memory.mapping.capacity())
        assertEquals(PAGE_SIZE, buffer.capacity())
        assertEquals(PAGE_SIZE, memory.byteSize)
        assertEquals(PAGE_SIZE, memory.memory.capacity())
        assertEquals(0.toByte(), memory.readI8(0))
        assertEquals(47.toByte(), buffer.get(PAGE_SIZE - 1))

        assertSame(memory, memory.grow(1))

        assertEquals(PAGE_SIZE * 2, memory.byteSize)
        assertEquals(PAGE_SIZE * 2, memory.memory.capacity())
        assertEquals(47.toByte(), memory.readI8(PAGE_SIZE - 1))
        assertEquals(0.toByte(), memory.readI8(PAGE_SIZE))
        assertEquals(0.toByte(), memory.readI8(PAGE_SIZE * 2 - 1))
        assertEquals(PAGE_SIZE * 2, memory.unsafeBorrowByteBuffer().capacity())
        assertEquals(PAGE_SIZE, buffer.capacity())

        assertSame(memory, memory.grow(2))
        assertEquals(PAGE_SIZE * 4, memory.byteSize)
        assertEquals(47.toByte(), memory.readI8(PAGE_SIZE - 1))
        assertEquals(0.toByte(), memory.readI8(PAGE_SIZE * 4 - 1))
        assertFailsWith<IllegalArgumentException> { memory.grow(1) }

        memory.unsafeBorrowByteBuffer().put(0, 91)
        assertEquals(91.toByte(), memory.readI8(0))
    }

    @Test
    fun `reserves the full jvm int address space when no maximum is declared`() {
        val memory = ByteBufferLinearMemory(LinearMemory.Pages(1u)).also(memories::add)

        assertEquals(MAX_JVM_MEMORY_BYTES, memory.mapping.capacity())
        assertSame(memory, memory.grow(1))
        assertEquals(MAX_JVM_MEMORY_BYTES, memory.mapping.capacity())
    }

    @Test
    fun `zero sized memory has a fixed reservation available for growth`() {
        val memory = ByteBufferLinearMemory(
            pages = LinearMemory.Pages(0u),
            maximumPages = LinearMemory.Pages(1u),
        ).also(memories::add)
        assertEquals(0, memory.byteSize)
        assertEquals(0, memory.memory.capacity())
        assertEquals(PAGE_SIZE, memory.mapping.capacity())

        memory.grow(1)

        assertEquals(PAGE_SIZE, memory.byteSize)
        assertEquals(PAGE_SIZE, memory.mapping.capacity())
        assertEquals(0.toByte(), memory.readI8(PAGE_SIZE - 1))
    }

    @Test
    fun `fresh and newly exposed file backed pages read as zero`() {
        val memory = ByteBufferLinearMemory(LinearMemory.Pages(2u)).also(memories::add)

        assertContentEquals(ByteArray(PAGE_SIZE * 2), memory.read(ByteArray(PAGE_SIZE * 2), 0, PAGE_SIZE * 2))

        memory.fill(0, 0x5A, PAGE_SIZE * 2)
        memory.grow(2)

        assertContentEquals(
            ByteArray(PAGE_SIZE * 2),
            memory.read(ByteArray(PAGE_SIZE * 2), PAGE_SIZE * 2, PAGE_SIZE * 2),
        )
    }

    @Test
    fun `maps buffer bounds failures to the wasm out of bounds trap`() {
        val memory = memory()

        val error = assertFailsWith<InvocationException> {
            OptimisticBoundsChecker(memory.byteSize - 3, 4, memory.byteSize) {
                memory.readI32(memory.byteSize - 3)
            }
        }

        assertEquals(InvocationError.MemoryOperationOutOfBounds, error.error)
    }

    @Test
    fun `reads and writes unaligned scalar values in little endian order`() {
        val memory = memory()
        val float = Float.fromBits(0x7FC01234)
        val double = Double.fromBits(0x7FF8000012345678)

        memory.writeI8(1, 0x7F)
        memory.writeI16(3, 0x1234)
        memory.writeI32(7, 0x12345678)
        memory.writeI64(13, 0x123456789ABCDEFL)
        memory.writeF32(23, float)
        memory.writeF64(29, double)

        assertEquals(0x7F.toByte(), memory.readI8(1))
        assertEquals(0x1234.toShort(), memory.readI16(3))
        assertEquals(0x12345678, memory.readI32(7))
        assertEquals(0x123456789ABCDEFL, memory.readI64(13))
        assertEquals(float.toRawBits(), memory.readF32(23).toRawBits())
        assertEquals(double.toRawBits(), memory.readF64(29).toRawBits())
        assertContentEquals(
            byteArrayOf(0x78, 0x56, 0x34, 0x12),
            memory.read(ByteArray(4), 7, 4),
        )
    }

    @Test
    fun `reads unsigned values with inline conversions`() {
        val memory = memory()
        memory.writeI8(0, -1)
        memory.writeI16(2, -1)
        memory.writeI32(4, -1)
        memory.writeI64(8, -1)

        assertEquals(UByte.MAX_VALUE, memory.readU8(0))
        assertEquals(UShort.MAX_VALUE, memory.readU16(2))
        assertEquals(UInt.MAX_VALUE, memory.readU32(4))
        assertEquals(ULong.MAX_VALUE, memory.readU64(8))
    }

    @Test
    fun `reuses caller buffers with independent memory and buffer pointers`() {
        val memory = memory()
        val source = byteArrayOf(10, 11, 12, 13, 14, 15, 16)
        val destination = ByteArray(10) { 99 }

        memory.write(
            memoryPointer = 8,
            buffer = source,
            bufferPointer = 2,
            bytesToWrite = 4,
        )
        val returned = memory.read(
            buffer = destination,
            memoryPointer = 8,
            bytesToRead = 4,
            bufferPointer = 3,
        )

        assertSame(destination, returned)
        assertContentEquals(byteArrayOf(99, 99, 99, 12, 13, 14, 15, 99, 99, 99), destination)
    }

    @Test
    fun `fills copies and moves same and cross memory ranges`() {
        val memory = memory()
        val destination = memory()
        memory.write(0, ByteArray(10) { it.toByte() })

        memory.move(sourcePointer = 0, destinationPointer = 2, bytesToMove = 6)
        assertContentEquals(
            byteArrayOf(0, 1, 0, 1, 2, 3, 4, 5, 8, 9),
            memory.read(ByteArray(10), 0, 10),
        )

        destination.fill(memoryPointer = 0, value = 7, bytesToFill = 10)
        destination.copy(sourcePointer = 2, destinationPointer = 3, bytesToCopy = 6, source = memory)
        assertContentEquals(
            byteArrayOf(7, 7, 7, 0, 1, 2, 3, 4, 5, 7),
            destination.read(ByteArray(10), 0, 10),
        )
    }

    @Test
    fun `checks complete copy ranges before mutation`() {
        val source = memory()
        val destination = memory()
        source.write(0, ByteArray(8) { it.toByte() })
        destination.fill(0, 9, 8)
        val before = destination.read(ByteArray(8), 0, 8)

        assertFailsWith<IndexOutOfBoundsException> {
            destination.copy(sourcePointer = 0, destinationPointer = PAGE_SIZE - 1, bytesToCopy = 2, source = source)
        }

        assertContentEquals(before, destination.read(ByteArray(8), 0, 8))
        assertEquals(destination.byteSize, destination.unsafeBorrowByteBuffer().capacity())
    }

    @Test
    fun `moves large overlapping ranges in either direction`() {
        val original = ByteArray(256) { it.toByte() }
        val expectedRight = original.copyOf().apply {
            copyInto(this, destinationOffset = 3, startIndex = 1, endIndex = 193)
        }
        val expectedLeft = original.copyOf().apply {
            copyInto(this, destinationOffset = 1, startIndex = 3, endIndex = 195)
        }
        val memory = memory()

        memory.write(0, original)
        memory.move(sourcePointer = 1, destinationPointer = 3, bytesToMove = 192)
        assertContentEquals(expectedRight, memory.read(ByteArray(original.size), 0, original.size))

        memory.write(0, original)
        memory.move(sourcePointer = 3, destinationPointer = 1, bytesToMove = 192)
        assertContentEquals(expectedLeft, memory.read(ByteArray(original.size), 0, original.size))
    }

    @Test
    fun `fills and copies large unaligned ranges`() {
        val sourceBytes = ByteArray(256) { it.toByte() }
        val expectedSource = sourceBytes.copyOf().apply {
            copyInto(this, destinationOffset = 131, startIndex = 3, endIndex = 99)
        }
        val expectedDestination = ByteArray(256).apply {
            expectedSource.copyInto(this, destinationOffset = 29, startIndex = 7, endIndex = 199)
            fill(0x5A, fromIndex = 221, toIndex = 254)
        }
        val source = memory()
        val destination = memory()

        source.write(0, sourceBytes)
        source.copy(sourcePointer = 3, destinationPointer = 131, bytesToCopy = 96)
        destination.copy(sourcePointer = 7, destinationPointer = 29, bytesToCopy = 192, source = source)
        destination.fill(memoryPointer = 221, value = 0x5A, bytesToFill = 33)

        assertContentEquals(expectedSource, source.read(ByteArray(sourceBytes.size), 0, sourceBytes.size))
        assertContentEquals(
            expectedDestination,
            destination.read(ByteArray(sourceBytes.size), 0, sourceBytes.size),
        )
    }

    @Test
    fun `reads and writes utf8 strings through the buffer backing`() {
        val memory = memory()
        val value = "Hello, 世界"
        val byteSize = value.encodeToByteArray().size

        StringWriter(memory, 17, value)
        memory.writeI8(17 + byteSize, 0)

        assertEquals(value, StringReader(memory, 17, byteSize))
        assertEquals(value, NullTerminatedStringReader(memory, 17))
    }

    @Test
    fun `rejects an out of bounds string before writing a prefix`() {
        val memory = memory()
        val pointer = memory.byteSize - 1
        memory.writeI8(pointer, 0x5A)

        assertFailsWith<IndexOutOfBoundsException> {
            StringWriter(memory, pointer, "hi")
        }

        assertEquals(0x5A.toByte(), memory.readI8(pointer))
    }

    @Test
    fun `allows borrowed buffers to move between jvm threads`() {
        val memory = memory()
        val buffer = memory.unsafeBorrowByteBuffer()
        val executor = Executors.newSingleThreadExecutor()

        try {
            val result = executor.submit<Byte> {
                buffer.put(0, 91)
                memory.readI8(0)
            }.get()

            assertEquals(91.toByte(), result)
        } finally {
            executor.shutdownNow()
        }
    }

    @Test
    fun `destruction clears owned references and leaves outstanding borrows alive`() {
        val memory = memory()
        val buffer = memory.unsafeBorrowByteBuffer()
        buffer.put(0, 47)

        LinearMemoryDestructor(memory)
        LinearMemoryDestructor(memory)

        assertEquals(0, memory.byteSize)
        assertEquals(0, memory.unsafeBorrowByteBuffer().capacity())
        assertEquals(47.toByte(), buffer.get(0))
    }

    private fun memory(): ByteBufferLinearMemory =
        ByteBufferLinearMemory(
            pages = LinearMemory.Pages(1u),
            maximumPages = LinearMemory.Pages(2u),
        ).also(memories::add)
}
