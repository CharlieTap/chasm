package io.github.charlietap.chasm.reader

import io.github.charlietap.chasm.SourceReader

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

fun FakeSourceReader(
    byteStream: Sequence<Byte>,
): SourceReader = object : SourceReader {
    private val iter = byteStream.iterator()

    override fun byte(): Byte = iter.next()

    override fun bytes(amount: Int): ByteArray = (1..amount).map {
        iter.next()
    }.toByteArray()

    override fun exhausted(): Boolean = iter.hasNext().not()

    override fun peek(): SourceReader = FakeSourceReader()
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
