package io.github.charlietap.chasm.decoder.reader

interface SourceReader {

    fun byte(): Byte

    fun bytes(amount: Int): ByteArray

    fun exhausted(): Boolean

    fun peek(): SourceReader
}
