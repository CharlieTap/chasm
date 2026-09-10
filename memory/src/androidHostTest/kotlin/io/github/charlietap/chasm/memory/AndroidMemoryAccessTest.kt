package io.github.charlietap.chasm.memory

import io.github.charlietap.chasm.host.ByteArrayHostMemory
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.memory.copy.LinearMemoryCopier
import io.github.charlietap.chasm.memory.fill.LinearMemoryFiller
import io.github.charlietap.chasm.memory.init.LinearMemoryInitialiser
import io.github.charlietap.chasm.memory.read.BytesReader
import io.github.charlietap.chasm.memory.read.F32Reader
import io.github.charlietap.chasm.memory.read.F64Reader
import io.github.charlietap.chasm.memory.read.I3216SReader
import io.github.charlietap.chasm.memory.read.I3216UReader
import io.github.charlietap.chasm.memory.read.I328SReader
import io.github.charlietap.chasm.memory.read.I328UReader
import io.github.charlietap.chasm.memory.read.I32Reader
import io.github.charlietap.chasm.memory.read.I6416SReader
import io.github.charlietap.chasm.memory.read.I6416UReader
import io.github.charlietap.chasm.memory.read.I6432SReader
import io.github.charlietap.chasm.memory.read.I6432UReader
import io.github.charlietap.chasm.memory.read.I648SReader
import io.github.charlietap.chasm.memory.read.I648UReader
import io.github.charlietap.chasm.memory.read.I64Reader
import io.github.charlietap.chasm.memory.read.NullTerminatedStringReader
import io.github.charlietap.chasm.memory.read.StringReader
import io.github.charlietap.chasm.memory.write.BytesWriter
import io.github.charlietap.chasm.memory.write.F32Writer
import io.github.charlietap.chasm.memory.write.F64Writer
import io.github.charlietap.chasm.memory.write.I32ToI16Writer
import io.github.charlietap.chasm.memory.write.I32ToI8Writer
import io.github.charlietap.chasm.memory.write.I32Writer
import io.github.charlietap.chasm.memory.write.I64ToI16Writer
import io.github.charlietap.chasm.memory.write.I64ToI32Writer
import io.github.charlietap.chasm.memory.write.I64ToI8Writer
import io.github.charlietap.chasm.memory.write.I64Writer
import io.github.charlietap.chasm.memory.write.StringWriter
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.PAGE_SIZE
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class AndroidMemoryAccessTest {

    @OptIn(UnsafeHostApi::class)
    @Test
    fun `borrowed host arrays share storage and follow growth and release`() {
        val memory = ByteArrayLinearMemory(LinearMemory.Pages(1u))
        val host: ByteArrayHostMemory = memory
        val original = host.unsafeBorrowByteArray()
        assertTrue(original.size > host.byteSize)
        original[0] = 42
        assertEquals(42.toByte(), host.readI8(0))
        host.writeI8(1, 43)
        assertEquals(43.toByte(), original[1])

        memory.grow(1)
        assertSame(original, host.unsafeBorrowByteArray())
        assertEquals(PAGE_SIZE * 2, host.byteSize)
        assertEquals(0.toByte(), host.readI8(PAGE_SIZE))

        memory.grow(original.size / PAGE_SIZE)
        val replacement = host.unsafeBorrowByteArray()
        assertNotSame(original, replacement)
        assertEquals(42.toByte(), replacement[0])
        original[0] = 99
        assertEquals(42.toByte(), host.readI8(0))

        memory.release()
        assertEquals(0, host.byteSize)
        assertContentEquals(byteArrayOf(), host.unsafeBorrowByteArray())
    }

    @Test
    fun `scalar readers reject logical overflow even with spare capacity`() {
        val memory = ByteArrayLinearMemory(LinearMemory.Pages(1u))
        assertTrue(memory.bytes.size > memory.byteSize)
        for (reader in readers) {
            assertEquals(reader.read(memory, 0), reader.read(memory, memory.byteSize - reader.width), reader.name)
            for (address in listOf(
                -1,
                Int.MIN_VALUE,
                memory.byteSize - reader.width + 1,
                memory.byteSize,
                Int.MAX_VALUE,
            )) {
                assertFailsWith<IndexOutOfBoundsException>(reader.name) {
                    reader.read(memory, address)
                }
            }
        }
    }

    @Test
    fun `scalar stores check the entire width before changing any bytes`() {
        for (maximumPages in listOf(1, 4)) {
            val memory = ByteArrayLinearMemory(LinearMemory.Pages(1u), maximumPages)
            for (writer in writers) {
                writer.write(memory, memory.byteSize - writer.width)
                for (address in listOf(
                    -1,
                    Int.MIN_VALUE,
                    memory.byteSize - writer.width + 1,
                    memory.byteSize,
                    Int.MAX_VALUE,
                )) {
                    val before = memory.bytes.copyOf()
                    assertFailsWith<IndexOutOfBoundsException>(writer.name) {
                        writer.write(memory, address)
                    }
                    assertContentEquals(before, memory.bytes, writer.name)
                }
            }
        }
    }

    @Test
    fun `unaligned scalar access preserves byte order, sign extension, and floating point bits`() {
        val memory = ByteArrayLinearMemory(LinearMemory.Pages(1u))
        I32Writer(memory, 3, 0x89ABCDEF.toInt())
        assertContentEquals(
            byteArrayOf(0xEF.toByte(), 0xCD.toByte(), 0xAB.toByte(), 0x89.toByte()),
            memory.bytes.copyOfRange(3, 7),
        )
        assertEquals(0x89ABCDEF.toInt(), I32Reader(memory, 3))
        assertEquals(0x89ABCDEF.toInt().toLong(), I6432SReader(memory, 3))
        assertEquals(0x89ABCDEFL, I6432UReader(memory, 3))
        assertEquals(-17, I328SReader(memory, 3))
        assertEquals(239, I328UReader(memory, 3))
        assertEquals(-17L, I648SReader(memory, 3))
        assertEquals(239L, I648UReader(memory, 3))
        assertEquals(0xCDEF.toShort().toInt(), I3216SReader(memory, 3))
        assertEquals(0xCDEF, I3216UReader(memory, 3))
        assertEquals(0xCDEF.toShort().toLong(), I6416SReader(memory, 3))
        assertEquals(0xCDEFL, I6416UReader(memory, 3))
        I64Writer(memory, 7, 0x123456789ABCDEF0L)
        assertEquals(0x123456789ABCDEF0L, I64Reader(memory, 7))
        I32ToI8Writer(memory, 1, 0x1234)
        assertEquals(0x34, I328UReader(memory, 1))
        I64ToI8Writer(memory, 1, 0x12345678L)
        assertEquals(0x78, I328UReader(memory, 1))
        I32ToI16Writer(memory, 1, 0x12345678)
        assertEquals(0x5678, I3216UReader(memory, 1))
        I64ToI16Writer(memory, 1, 0x123456789ABCDEF0L)
        assertEquals(0xDEF0, I3216UReader(memory, 1))
        I64ToI32Writer(memory, 1, 0x123456789ABCDEF0L)
        assertEquals(0x9ABCDEF0.toInt(), I32Reader(memory, 1))
        val float = Float.fromBits(0xFFC01234.toInt())
        val double = Double.fromBits(0x7FF8000012345678L)
        F32Writer(memory, 17, float)
        F64Writer(memory, 23, double)
        assertEquals(float.toRawBits(), F32Reader(memory, 17).toRawBits())
        assertEquals(double.toRawBits(), F64Reader(memory, 23).toRawBits())
    }

    @Test
    fun `growth exposes only new logical bytes and readers follow replacement storage`() {
        val memory = ByteArrayLinearMemory(LinearMemory.Pages(1u))
        val original = memory.bytes
        assertFailsWith<IndexOutOfBoundsException> { I32Reader(memory, PAGE_SIZE) }
        memory.grow(1)
        assertSame(original, memory.bytes)
        assertEquals(0, I32Reader(memory, PAGE_SIZE))
        I32Writer(memory, PAGE_SIZE, 42)
        memory.grow(1)
        assertNotSame(original, memory.bytes)
        assertEquals(42, I32Reader(memory, PAGE_SIZE))
        assertEquals(0, I32Reader(memory, 2 * PAGE_SIZE))
        assertFailsWith<IndexOutOfBoundsException> { I32Writer(memory, memory.byteSize, 99) }
    }

    @Test
    fun `explicit bounds failures still become Wasm traps`() {
        val memory = ByteArrayLinearMemory(LinearMemory.Pages(1u))
        val error = assertFailsWith<InvocationException> {
            OptimisticBoundsChecker(memory.byteSize - 3, 4, memory.byteSize) {
                I32Writer(memory, memory.byteSize - 3, 0x12345678)
            }
        }
        assertEquals(InvocationError.MemoryOperationOutOfBounds, error.error)
        assertTrue(memory.bytes.all { it == 0.toByte() })
    }

    @Test
    fun `buffer and string access cannot expose spare capacity`() {
        val memory = ByteArrayLinearMemory(LinearMemory.Pages(1u))
        val size = memory.byteSize
        val buffer = byteArrayOf(1, 2, 3, 4)
        BytesWriter(memory, size, buffer, size - 4, 4, 0)
        val destination = ByteArray(6)
        assertSame(destination, BytesReader(memory, destination, size - 4, 4, 1))
        assertContentEquals(byteArrayOf(0, 1, 2, 3, 4, 0), destination)
        val before = memory.bytes.copyOf()
        for ((pointer, length, bufferPointer) in listOf(
            Triple(size - 3, 4, 0),
            Triple(size, 1, 0),
            Triple(0, -1, 0),
            Triple(0, Int.MAX_VALUE, 0),
            Triple(0, 4, 1),
            Triple(0, 1, -1),
        )) {
            assertFailsWith<IndexOutOfBoundsException> {
                BytesWriter(memory, size, buffer, pointer, length, bufferPointer)
            }
            assertContentEquals(before, memory.bytes)
            val output = buffer.copyOf()
            assertFailsWith<IndexOutOfBoundsException> { BytesReader(memory, output, pointer, length, bufferPointer) }
            assertContentEquals(buffer, output)
        }
        StringWriter(memory, size - 2, "hi")
        assertEquals("hi", StringReader(memory, size - 2, 2))
        assertEquals("", NullTerminatedStringReader(memory, size - 2))
        assertFailsWith<IndexOutOfBoundsException> { StringReader(memory, size - 1, 2) }
        val beforeString = memory.bytes.copyOf()
        assertFailsWith<IndexOutOfBoundsException> { StringWriter(memory, size - 1, "hi") }
        assertContentEquals(beforeString, memory.bytes)
        assertFailsWith<IndexOutOfBoundsException> { NullTerminatedStringReader(memory, size + 1) }
        assertFailsWith<IndexOutOfBoundsException> { NullTerminatedStringReader(memory, -1) }
    }

    @Test
    fun `zero length access is valid at the logical end including empty memory`() {
        for (pages in listOf(0u, 1u)) {
            val memory = ByteArrayLinearMemory(LinearMemory.Pages(pages))
            val end = memory.byteSize
            BytesWriter(memory, end, byteArrayOf(), end, 0, 0)
            assertContentEquals(byteArrayOf(), BytesReader(memory, byteArrayOf(), end, 0, 0))
            StringWriter(memory, end, "")
            assertEquals("", StringReader(memory, end, 0))
            assertEquals("", NullTerminatedStringReader(memory, end))
            assertFailsWith<IndexOutOfBoundsException> { BytesWriter(memory, end, byteArrayOf(), end + 1, 0, 0) }
            assertFailsWith<IndexOutOfBoundsException> { I328SReader(memory, end) }
        }
    }

    @Test
    fun `bulk instructions preserve overlap and reject access to reserved capacity before mutation`() {
        val memory = ByteArrayLinearMemory(LinearMemory.Pages(1u))
        val size = memory.byteSize
        val source = ubyteArrayOf(1u, 2u, 3u, 4u)
        LinearMemoryInitialiser(source, memory, 0, 0, 4, source.size, size)
        LinearMemoryCopier(memory, memory, 0, 1, 4, size, size)
        assertContentEquals(byteArrayOf(1, 1, 2, 3, 4), memory.bytes.copyOfRange(0, 5))
        LinearMemoryFiller(memory, 7, 257, 42, size)
        assertTrue(memory.bytes.sliceArray(7 until 264).all { it == 42.toByte() })
        val before = memory.bytes.copyOf()
        val capacity = memory.bytes.size
        for (operation in listOf<() -> Unit>(
            { LinearMemoryCopier(memory, memory, size - 3, 0, 4, capacity, capacity) },
            { LinearMemoryCopier(memory, memory, 0, size - 3, 4, capacity, capacity) },
            { LinearMemoryInitialiser(source, memory, 0, size - 3, 4, source.size, capacity) },
            { LinearMemoryInitialiser(source, memory, 1, 0, 4, 5, size) },
            { LinearMemoryFiller(memory, size - 3, 4, 9, capacity) },
        )) {
            val error = assertFailsWith<InvocationException> { operation() }
            assertEquals(InvocationError.MemoryOperationOutOfBounds, error.error)
            assertContentEquals(before, memory.bytes)
        }
    }

    private data class Reader(val name: String, val width: Int, val read: (LinearMemory, Int) -> Any)

    private data class Writer(val name: String, val width: Int, val write: (LinearMemory, Int) -> Unit)

    private val readers = listOf(
        Reader("I328SReader", 1, ::I328SReader),
        Reader("I328UReader", 1, ::I328UReader),
        Reader("I648SReader", 1, ::I648SReader),
        Reader("I648UReader", 1, ::I648UReader),
        Reader("I3216SReader", 2, ::I3216SReader),
        Reader("I3216UReader", 2, ::I3216UReader),
        Reader("I6416SReader", 2, ::I6416SReader),
        Reader("I6416UReader", 2, ::I6416UReader),
        Reader("I32Reader", 4, ::I32Reader),
        Reader("I6432SReader", 4, ::I6432SReader),
        Reader("I6432UReader", 4, ::I6432UReader),
        Reader("I64Reader", 8, ::I64Reader),
        Reader("F32Reader", 4, ::F32Reader),
        Reader("F64Reader", 8, ::F64Reader),
    )

    private val writers = listOf(
        Writer("I32ToI8Writer", 1) { memory, address -> I32ToI8Writer(memory, address, -1) },
        Writer("I64ToI8Writer", 1) { memory, address -> I64ToI8Writer(memory, address, -1L) },
        Writer("I32ToI16Writer", 2) { memory, address -> I32ToI16Writer(memory, address, -1) },
        Writer("I64ToI16Writer", 2) { memory, address -> I64ToI16Writer(memory, address, -1L) },
        Writer("I32Writer", 4) { memory, address -> I32Writer(memory, address, -1) },
        Writer("I64ToI32Writer", 4) { memory, address -> I64ToI32Writer(memory, address, -1L) },
        Writer("I64Writer", 8) { memory, address -> I64Writer(memory, address, -1L) },
        Writer("F32Writer", 4) { memory, address -> F32Writer(memory, address, 1.25f) },
        Writer("F64Writer", 8) { memory, address -> F64Writer(memory, address, 1.25) },
    )
}
