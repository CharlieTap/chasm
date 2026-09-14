@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlin.experimental.ExperimentalNativeApi::class,
    kotlin.native.runtime.NativeRuntimeApi::class,
)

package io.github.charlietap.chasm.memory

import io.github.charlietap.chasm.config.LinearMemoryConfig
import io.github.charlietap.chasm.memory.copy.LinearMemoryCopier
import io.github.charlietap.chasm.memory.destruct.LinearMemoryDestructor
import io.github.charlietap.chasm.memory.fill.LinearMemoryFiller
import io.github.charlietap.chasm.memory.init.LinearMemoryInitialiser
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
import platform.posix.usleep
import kotlin.concurrent.AtomicInt
import kotlin.native.runtime.GC
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class NativeMappedLinearMemoryHostTest {

    private val memories = mutableListOf<NativeMappedLinearMemory>()

    @AfterTest
    fun releaseMemories() {
        memories.forEach(NativeMappedLinearMemory::release)
        memories.clear()
    }

    @Test
    fun `reserves a stable address and exposes zeroed pages through growth`() {
        val memory = memory(initialPages = 1u, maximumPages = 4u)
        val address = memory.baseAddress
        memory.writeI8(PAGE_SIZE - 1, 47)

        assertSame(memory, memory.grow(1))
        assertEquals(address, memory.baseAddress)
        assertEquals(PAGE_SIZE * 2, memory.byteSize)
        assertEquals(47.toByte(), memory.readI8(PAGE_SIZE - 1))
        assertContentEquals(
            ByteArray(PAGE_SIZE),
            memory.read(ByteArray(PAGE_SIZE), PAGE_SIZE, PAGE_SIZE),
        )

        assertSame(memory, memory.grow(2))
        assertEquals(address, memory.baseAddress)
        assertEquals(PAGE_SIZE * 4, memory.byteSize)
        assertFailsWith<IllegalArgumentException> { memory.grow(1) }
        assertEquals(PAGE_SIZE * 4, memory.byteSize)
    }

    @Test
    fun `supports zero initial and zero maximum memories`() {
        val growable = memory(initialPages = 0u, maximumPages = 1u)
        assertEquals(0, growable.byteSize)
        assertFailsWith<IndexOutOfBoundsException> { growable.readI8(0) }

        growable.grow(1)
        assertEquals(PAGE_SIZE, growable.byteSize)
        assertEquals(0.toByte(), growable.readI8(PAGE_SIZE - 1))

        val empty = memory(initialPages = 0u, maximumPages = 0u)
        assertEquals(0, empty.byteSize)
        assertSame(empty, empty.grow(0))
        assertFailsWith<IllegalArgumentException> { empty.grow(1) }
    }

    @Test
    fun `honors prefault during construction and growth`() {
        val memory = NativeMappedLinearMemory.create(
            pages = LinearMemory.Pages(1u),
            maximumPages = LinearMemory.Pages(2u),
            config = LinearMemoryConfig(prefault = true),
        ).also(memories::add)

        assertTrue(memory.read(ByteArray(PAGE_SIZE), 0, PAGE_SIZE).all { it == 0.toByte() })
        memory.grow(1)
        assertTrue(memory.read(ByteArray(PAGE_SIZE), PAGE_SIZE, PAGE_SIZE).all { it == 0.toByte() })
    }

    @Test
    fun `reads and writes unaligned scalars with WebAssembly byte order`() {
        val memory = memory()
        val float = Float.fromBits(0xFFC01234.toInt())
        val double = Double.fromBits(0x7FF8000012345678L)

        memory.writeI8(1, 0x7F)
        memory.writeI16(3, 0x1234)
        memory.writeI32(7, 0x89ABCDEF.toInt())
        memory.writeI64(13, 0x123456789ABCDEFL)
        memory.writeF32(23, float)
        memory.writeF64(29, double)

        assertEquals(0x7F.toByte(), memory.readI8(1))
        assertEquals(0x1234.toShort(), memory.readI16(3))
        assertEquals(0x89ABCDEF.toInt(), memory.readI32(7))
        assertEquals(0x123456789ABCDEFL, memory.readI64(13))
        assertEquals(float.toRawBits(), memory.readF32(23).toRawBits())
        assertEquals(double.toRawBits(), memory.readF64(29).toRawBits())
        assertContentEquals(
            byteArrayOf(0xEF.toByte(), 0xCD.toByte(), 0xAB.toByte(), 0x89.toByte()),
            memory.read(ByteArray(4), 7, 4),
        )
    }

    @Test
    fun `interpreter scalar helpers use the mapped backing`() {
        val memory = memory()
        I32Writer(memory, 3, 0x89ABCDEF.toInt())

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

        val float = Float.fromBits(0x7FC01234)
        val double = Double.fromBits(0x7FF8000012345678L)
        F32Writer(memory, 17, float)
        F64Writer(memory, 23, double)
        assertEquals(float.toRawBits(), F32Reader(memory, 17).toRawBits())
        assertEquals(double.toRawBits(), F64Reader(memory, 23).toRawBits())
    }

    @Test
    fun `host scalar access rejects every range outside the logical size`() {
        val memory = memory()
        val reads = listOf<Pair<Int, (Int) -> Any>>(
            1 to memory::readI8,
            2 to memory::readI16,
            4 to memory::readI32,
            8 to memory::readI64,
            4 to memory::readF32,
            8 to memory::readF64,
        )
        for ((width, read) in reads) {
            assertEquals(read(0), read(memory.byteSize - width))
            for (address in invalidAddresses(memory.byteSize, width)) {
                assertFailsWith<IndexOutOfBoundsException> { read(address) }
            }
        }

        val writes = listOf<Pair<Int, (Int) -> Unit>>(
            1 to { memory.writeI8(it, -1) },
            2 to { memory.writeI16(it, -1) },
            4 to { memory.writeI32(it, -1) },
            8 to { memory.writeI64(it, -1) },
            4 to { memory.writeF32(it, 1.25f) },
            8 to { memory.writeF64(it, 1.25) },
        )
        for ((width, write) in writes) {
            write(memory.byteSize - width)
            for (address in invalidAddresses(memory.byteSize, width)) {
                val before = memory.read(ByteArray(16), memory.byteSize - 16, 16)
                assertFailsWith<IndexOutOfBoundsException> { write(address) }
                assertContentEquals(before, memory.read(ByteArray(16), memory.byteSize - 16, 16))
            }
        }
    }

    @Test
    fun `checks buffers before mutation and permits zero length at the end`() {
        val memory = memory()
        val source = byteArrayOf(10, 11, 12, 13, 14, 15)
        val destination = ByteArray(8) { 99 }

        memory.write(30, source, bufferPointer = 1, bytesToWrite = 4)
        val returned = memory.read(destination, memoryPointer = 30, bytesToRead = 4, bufferPointer = 2)

        assertSame(destination, returned)
        assertContentEquals(byteArrayOf(99, 99, 11, 12, 13, 14, 99, 99), destination)
        memory.write(memory.byteSize, byteArrayOf(), bytesToWrite = 0)
        assertSame(
            destination,
            memory.read(destination, memory.byteSize, bytesToRead = 0, bufferPointer = destination.size),
        )

        val before = memory.read(ByteArray(8), 30, 8)
        assertFailsWith<IndexOutOfBoundsException> {
            memory.write(memory.byteSize - 1, source, bytesToWrite = 2)
        }
        assertContentEquals(before, memory.read(ByteArray(8), 30, 8))
        assertFailsWith<IndexOutOfBoundsException> {
            memory.read(destination, 0, bytesToRead = 2, bufferPointer = destination.size - 1)
        }
    }

    @Test
    fun `fills copies and moves mapped and byte array memory`() {
        val source = memory()
        val destination = memory()
        val byteArraySource = ByteArrayLinearMemory(ByteArray(16) { (it + 20).toByte() })
        source.write(0, ByteArray(256) { it.toByte() })

        source.move(sourcePointer = 1, destinationPointer = 3, bytesToMove = 192)
        val expectedRight = ByteArray(256) { it.toByte() }.apply {
            copyInto(this, destinationOffset = 3, startIndex = 1, endIndex = 193)
        }
        assertContentEquals(expectedRight, source.read(ByteArray(256), 0, 256))

        source.write(0, ByteArray(256) { it.toByte() })
        source.move(sourcePointer = 3, destinationPointer = 1, bytesToMove = 192)
        val expectedLeft = ByteArray(256) { it.toByte() }.apply {
            copyInto(this, destinationOffset = 1, startIndex = 3, endIndex = 195)
        }
        assertContentEquals(expectedLeft, source.read(ByteArray(256), 0, 256))

        destination.copy(sourcePointer = 7, destinationPointer = 29, bytesToCopy = 192, source = source)
        destination.fill(memoryPointer = 221, value = 0x5A, bytesToFill = 33)
        assertContentEquals(expectedLeft.copyOfRange(7, 199), destination.read(ByteArray(192), 29, 192))
        assertTrue(destination.read(ByteArray(33), 221, 33).all { it == 0x5A.toByte() })

        destination.copy(sourcePointer = 2, destinationPointer = 400, bytesToCopy = 6, source = byteArraySource)
        assertContentEquals(byteArrayOf(22, 23, 24, 25, 26, 27), destination.read(ByteArray(6), 400, 6))
    }

    @Test
    fun `bulk instructions trap before changing mapped memory`() {
        val memory = memory()
        val source = ubyteArrayOf(1u, 2u, 3u, 4u)
        val size = memory.byteSize
        LinearMemoryInitialiser(source, memory, 0, 0, 4, source.size, size)
        LinearMemoryCopier(memory, memory, 0, 1, 4, size, size)
        LinearMemoryFiller(memory, 7, 257, 42, size)

        assertContentEquals(byteArrayOf(1, 1, 2, 3, 4), memory.read(ByteArray(5), 0, 5))
        assertTrue(memory.read(ByteArray(257), 7, 257).all { it == 42.toByte() })
        val before = memory.read(ByteArray(264), 0, 264)

        for (operation in listOf<() -> Unit>(
            { LinearMemoryCopier(memory, memory, size - 3, 0, 4, size, size) },
            { LinearMemoryCopier(memory, memory, 0, size - 3, 4, size, size) },
            { LinearMemoryInitialiser(source, memory, 0, size - 3, 4, source.size, size) },
            { LinearMemoryInitialiser(source, memory, 1, 0, 4, source.size, size) },
            { LinearMemoryFiller(memory, size - 3, 4, 9, size) },
        )) {
            val error = assertFailsWith<InvocationException> { operation() }
            assertEquals(InvocationError.MemoryOperationOutOfBounds, error.error)
            assertContentEquals(before, memory.read(ByteArray(264), 0, 264))
        }
    }

    @Test
    fun `reads and writes utf8 strings without exposing reserved pages`() {
        val memory = memory()
        val value = "Hello, 世界"
        val byteCount = value.encodeToByteArray().size
        StringWriter(memory, 17, value)
        memory.writeI8(17 + byteCount, 0)

        assertEquals(value, StringReader(memory, 17, byteCount))
        assertEquals(value, NullTerminatedStringReader(memory, 17))
        assertEquals("", NullTerminatedStringReader(memory, memory.byteSize))
        assertFailsWith<IndexOutOfBoundsException> {
            StringWriter(memory, memory.byteSize - 1, "hi")
        }
        assertFailsWith<IndexOutOfBoundsException> {
            StringReader(memory, memory.byteSize - 1, 2)
        }
    }

    @Test
    fun `failed growth preserves the logical size and committed contents`() {
        var commitCount = 0
        val operations = observedOperations(AtomicInt(0)).let { system ->
            NativeVirtualMemoryOperations(
                reserve = system.reserve,
                commit = { base, offset, bytes ->
                    commitCount++
                    if (commitCount == 2) throw OutOfMemoryError("expected commit failure")
                    system.commit(base, offset, bytes)
                },
                release = system.release,
            )
        }
        val memory = NativeMappedLinearMemory.create(
            pages = LinearMemory.Pages(1u),
            maximumPages = LinearMemory.Pages(2u),
            operations = operations,
        ).also(memories::add)
        memory.writeI32(7, 42)

        assertFailsWith<OutOfMemoryError> { memory.grow(1) }
        assertEquals(PAGE_SIZE, memory.byteSize)
        assertEquals(42, memory.readI32(7))
    }

    @Test
    fun `initial commit failure releases the reservation`() {
        val releases = AtomicInt(0)
        val system = observedOperations(releases)
        val operations = NativeVirtualMemoryOperations(
            reserve = system.reserve,
            commit = { _, _, _ -> throw OutOfMemoryError("expected commit failure") },
            release = system.release,
        )

        assertFailsWith<OutOfMemoryError> {
            NativeMappedLinearMemory.create(
                pages = LinearMemory.Pages(1u),
                maximumPages = LinearMemory.Pages(1u),
                operations = operations,
            )
        }
        assertEquals(1, releases.value)
    }

    @Test
    fun `explicit release is immediate and idempotent`() {
        val releases = AtomicInt(0)
        val memory = NativeMappedLinearMemory.create(
            pages = LinearMemory.Pages(1u),
            maximumPages = LinearMemory.Pages(1u),
            operations = observedOperations(releases),
        )

        LinearMemoryDestructor(memory)
        LinearMemoryDestructor(memory)

        assertEquals(1, releases.value)
        assertEquals(0, memory.byteSize)
        assertFailsWith<IndexOutOfBoundsException> { memory.readI8(0) }
    }

    @Test
    fun `cleaner eventually releases an unreachable mapping once`() {
        val releases = AtomicInt(0)
        allocateAndForget(releases, releaseExplicitly = false)

        awaitRelease(releases)

        assertEquals(1, releases.value)
    }

    @Test
    fun `cleaner does not repeat an explicit release`() {
        val releases = AtomicInt(0)
        allocateAndForget(releases, releaseExplicitly = true)
        assertEquals(1, releases.value)

        repeat(10) {
            GC.collect()
            usleep(10_000u)
        }

        assertEquals(1, releases.value)
    }

    private fun memory(
        initialPages: UInt = 1u,
        maximumPages: UInt = 2u,
    ): NativeMappedLinearMemory = NativeMappedLinearMemory.create(
        pages = LinearMemory.Pages(initialPages),
        maximumPages = LinearMemory.Pages(maximumPages),
    ).also(memories::add)

    private fun invalidAddresses(size: Int, width: Int): List<Int> = listOf(
        -1,
        Int.MIN_VALUE,
        size - width + 1,
        size,
        Int.MAX_VALUE,
    )

    private fun observedOperations(releases: AtomicInt): NativeVirtualMemoryOperations =
        NativeVirtualMemoryOperations(
            reserve = ::reserveVirtualMemory,
            commit = ::commitVirtualMemory,
            release = { base, bytes ->
                check(releases.incrementAndGet() > 0)
                releaseVirtualMemory(base, bytes)
            },
        )

    private fun allocateAndForget(releases: AtomicInt, releaseExplicitly: Boolean) {
        val memory = NativeMappedLinearMemory.create(
            pages = LinearMemory.Pages(1u),
            maximumPages = LinearMemory.Pages(1u),
            operations = observedOperations(releases),
        )
        if (releaseExplicitly) {
            memory.release()
        }
    }

    private fun awaitRelease(releases: AtomicInt) {
        repeat(100) {
            if (releases.value == 1) return
            GC.collect()
            usleep(10_000u)
        }
    }
}
