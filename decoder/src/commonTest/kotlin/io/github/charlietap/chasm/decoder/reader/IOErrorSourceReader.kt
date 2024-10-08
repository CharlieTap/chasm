package io.github.charlietap.chasm.decoder.reader

import io.github.charlietap.chasm.stream.SourceReader

fun IOErrorSourceReader(err: Throwable): SourceReader = object : SourceReader {
    override fun byte(): Byte = throw err

    override fun bytes(amount: Int): ByteArray = throw err

    override fun exhausted(): Boolean = throw err

    override fun peek(): SourceReader = throw err
}
