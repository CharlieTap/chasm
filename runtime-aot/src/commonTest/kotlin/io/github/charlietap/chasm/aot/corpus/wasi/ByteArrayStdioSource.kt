package io.github.charlietap.chasm.aot.corpus.wasi

import at.released.weh.filesystem.stdio.StdioSource
import kotlinx.io.Buffer

internal class ByteArrayStdioSource(
    private val bytes: ByteArray,
) : StdioSource {

    private var offset = 0

    override fun readAtMostTo(
        sink: Buffer,
        byteCount: Long,
    ): Long {
        if (offset >= bytes.size) return -1

        val count = minOf(byteCount.toInt(), bytes.size - offset)
        sink.write(bytes, offset, offset + count)
        offset += count
        return count.toLong()
    }

    override fun close() = Unit
}
