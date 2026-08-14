package io.github.charlietap.chasm.decoder.reader

import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeException
import io.github.charlietap.chasm.fake.decoder.FakeSourceReader
import io.github.charlietap.chasm.stream.SourceReader
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BufferedWasmBinaryReaderTest {

    @Test
    fun `reads fixed-width floating point values without disturbing following bytes`() {
        val reader = reader(
            byteArrayOf(
                0x00,
                0x00,
                0x80.toByte(),
                0x3F,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0xF0.toByte(),
                0x3F,
                0x7F,
            ),
        )

        assertEquals(1.0f, reader.float())
        assertEquals(1.0, reader.double())
        assertEquals(0x7Fu.toUByte(), reader.peekUByte())
        assertEquals(12u, reader.position())
    }

    @Test
    fun `peeks integers without advancing the reader`() {
        val reader = reader(byteArrayOf(0xE5.toByte(), 0x8E.toByte(), 0x26))

        assertEquals(0xE5u.toUByte(), reader.peekUByte())
        assertEquals(624_485u, reader.peekUInt())
        assertEquals(0u, reader.position())
        assertEquals(624_485u, reader.uint())
        assertEquals(3u, reader.position())
    }

    @Test
    fun `retains byte array input without copying`() {
        val bytes = byteArrayOf(1)
        val reader = reader(bytes)

        bytes[0] = 2

        assertEquals(2, reader.byte())
    }

    @Test
    fun `reads a bounded byte array range without copying`() {
        val bytes = byteArrayOf(1, 2, 3, 4)
        val reader = BufferedWasmBinaryReader(bytes, 1, 3)

        bytes[1] = 5

        assertEquals(1u, reader.position())
        assertEquals(5, reader.byte())
        assertEquals(3, reader.byte())
        assertEquals(true, reader.exhausted())
        assertFailsWith<NoSuchElementException> { reader.byte() }
    }

    @Test
    fun `rejects invalid byte array ranges`() {
        val bytes = byteArrayOf(1, 2, 3)

        assertFailsWith<IllegalArgumentException> { BufferedWasmBinaryReader(bytes, -1, 2) }
        assertFailsWith<IllegalArgumentException> { BufferedWasmBinaryReader(bytes, 2, 1) }
        assertFailsWith<IllegalArgumentException> { BufferedWasmBinaryReader(bytes, 0, 4) }
    }

    @Test
    fun `reads bulk and fixed width values across short source refills`() {
        val bytes = byteArrayOf(
            1,
            2,
            3,
            4,
            0x00,
            0x00,
            0x80.toByte(),
            0x3F,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0xF0.toByte(),
            0x3F,
        )
        val reader = BinaryReader(shortReadSource(bytes, 2))

        assertContentEquals(byteArrayOf(1, 2, 3, 4), reader.bytes(4))
        assertEquals(1.0f, reader.float())
        assertEquals(1.0, reader.double())
        assertEquals(16u, reader.position())
        assertEquals(true, reader.exhausted())
    }

    @Test
    fun `peek refills without consuming source bytes`() {
        val reader = BinaryReader(shortReadSource(byteArrayOf(0xE5.toByte(), 0x8E.toByte(), 0x26), 1))

        assertEquals(624_485u, reader.peekUInt())
        assertEquals(0u, reader.position())
        assertEquals(624_485u, reader.uint())
        assertEquals(3u, reader.position())
    }

    @Test
    fun `logical limits retain source bytes read ahead for the parent`() {
        val reader = BinaryReader(shortReadSource(byteArrayOf(1, 2, 3), 3))
        val parentLimit = reader.pushLimit(2u)

        assertContentEquals(byteArrayOf(1, 2), reader.bytes(2))
        assertEquals(true, reader.exhausted())

        reader.restoreLimit(parentLimit)
        assertEquals(3.toByte(), reader.byte())
        assertEquals(true, reader.exhausted())
    }

    @Test
    fun `wraps source failures at the refill boundary`() {
        val expected = IllegalStateException("read failed")
        val reader = BinaryReader(IOErrorSourceReader(expected))

        val actual = assertFailsWith<WasmDecodeException> { reader.byte() }

        assertEquals(WasmDecodeError.IOError(expected), actual.error)
    }

    @Test
    fun `rejects a source that makes no progress`() {
        val reader = BinaryReader(
            FakeSourceReader(
                bytes = { byteArrayOf() },
                exhausted = { false },
            ),
        )

        val actual = assertFailsWith<WasmDecodeException> { reader.byte() }

        val error = actual.error as WasmDecodeError.IOError
        assertEquals("ByteSource made no progress", error.throwable.message)
    }

    @Test
    fun `rejects invalid source read counts`() {
        listOf(-2, 11).forEach { count ->
            val reader = BufferedWasmBinaryReader(ByteSource { _, _, _ -> count }, bufferSize = 10)

            val actual = assertFailsWith<WasmDecodeException> { reader.byte() }

            val error = actual.error as WasmDecodeError.IOError
            assertEquals("ByteSource returned $count for a 10 byte destination", error.throwable.message)
        }
    }

    private fun reader(bytes: ByteArray): WasmBinaryReader = BinaryReader(bytes)

    private fun shortReadSource(
        bytes: ByteArray,
        maximumRead: Int,
        startPosition: Int = 0,
    ): SourceReader = object : SourceReader {
        private var position = startPosition

        override fun byte(): Byte {
            if (exhausted()) throw NoSuchElementException("No more elements")
            return bytes[position++]
        }

        override fun bytes(amount: Int): ByteArray {
            val count = minOf(amount, maximumRead, bytes.size - position)
            return bytes.copyOfRange(position, position + count).also {
                position += count
            }
        }

        override fun exhausted(): Boolean = position >= bytes.size

        override fun peek(): SourceReader = shortReadSource(bytes, maximumRead, position)
    }
}
