package io.github.charlietap.chasm.memory

import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.host.readU16
import io.github.charlietap.chasm.host.readU32
import io.github.charlietap.chasm.host.readU64
import io.github.charlietap.chasm.host.readU8
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

@OptIn(UnsafeHostApi::class)
class ByteBufferLinearMemoryHostTest {

    @Test
    fun `reads and writes scalar values directly in little endian order`() {
        val memory = memory(64)
        val float = Float.fromBits(0x7FC01234)
        val double = Double.fromBits(0x7FF8000012345678)

        memory.writeI8(0, 0x7F)
        memory.writeI16(2, 0x1234)
        memory.writeI32(4, 0x12345678)
        memory.writeI64(8, 0x123456789ABCDEFL)
        memory.writeF32(16, float)
        memory.writeF64(24, double)

        assertEquals(0x7F.toByte(), memory.readI8(0))
        assertEquals(0x1234.toShort(), memory.readI16(2))
        assertEquals(0x12345678, memory.readI32(4))
        assertEquals(0x123456789ABCDEFL, memory.readI64(8))
        assertEquals(float.toRawBits(), memory.readF32(16).toRawBits())
        assertEquals(double.toRawBits(), memory.readF64(24).toRawBits())
    }

    @Test
    fun `reads unsigned values with inline conversions`() {
        val memory = memory(16)
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
        val memory = memory(32)
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
    fun `fills copies and moves same and cross memory ranges without temporary buffers`() {
        val memory = memory(16)
        val destination = memory(16)
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
        val source = memory(8)
        val destination = memory(8)
        source.write(0, ByteArray(8) { it.toByte() })
        destination.fill(0, 9, 8)
        val before = destination.read(ByteArray(8), 0, 8)

        assertFailsWith<IndexOutOfBoundsException> {
            destination.copy(sourcePointer = 0, destinationPointer = 7, bytesToCopy = 2, source = source)
        }

        assertContentEquals(before, destination.read(ByteArray(8), 0, 8))
        assertSame(destination.memory, destination.unsafeBorrowByteBuffer())
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
        val memory = memory(original.size)

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
        val source = memory(sourceBytes.size)
        val destination = memory(sourceBytes.size)

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

    private fun memory(size: Int): ByteBufferLinearMemory = ByteBufferLinearMemory(
        ByteBuffer.allocateDirect(size).order(ByteOrder.LITTLE_ENDIAN),
    )
}
