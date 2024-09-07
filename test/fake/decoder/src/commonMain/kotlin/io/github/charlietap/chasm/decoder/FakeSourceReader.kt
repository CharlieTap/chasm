package io.github.charlietap.chasm.decoder

import io.github.charlietap.chasm.stream.SourceReader

fun FakeSourceReader(
    bytes: ByteArray,
    startPosition: Int = 0,
): SourceReader = object : SourceReader {
    private var position = startPosition

    override fun byte(): Byte {
        if (position >= bytes.size) throw NoSuchElementException("No more elements")
        return bytes[position++]
    }

    override fun bytes(amount: Int): ByteArray {
        val endPosition = (position + amount).coerceAtMost(bytes.size)
        val result = bytes.copyOfRange(position, endPosition)
        position = endPosition
        return result
    }

    override fun exhausted(): Boolean = position >= bytes.size

    override fun peek(): SourceReader = FakeSourceReader(bytes, position)
}

fun FakeSourceReader(
    byte: () -> Byte = { 0x00 },
    bytes: (Int) -> ByteArray = { byteArrayOf() },
    exhausted: () -> Boolean = { false },
    peek: () -> SourceReader = { FakeSourceReader() },
): SourceReader = object : SourceReader {
    override fun byte(): Byte = byte()

    override fun bytes(amount: Int): ByteArray = bytes(amount)

    override fun exhausted(): Boolean = exhausted()

    override fun peek(): SourceReader = peek()
}

fun FakeByteSourceReader(
    byte: () -> Byte,
): SourceReader = FakeSourceReader(byte = byte)

fun FakeByteArraySourceReader(
    bytes: (Int) -> ByteArray,
): SourceReader = FakeSourceReader(bytes = bytes)

fun FakeExhaustedSourceReader(
    exhausted: () -> Boolean,
): SourceReader = FakeSourceReader(exhausted = exhausted)
