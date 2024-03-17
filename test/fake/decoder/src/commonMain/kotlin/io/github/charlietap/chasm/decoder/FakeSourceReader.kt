package io.github.charlietap.chasm.decoder

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
