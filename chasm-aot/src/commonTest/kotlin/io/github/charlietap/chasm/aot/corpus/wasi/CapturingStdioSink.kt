package io.github.charlietap.chasm.aot.corpus.wasi

import at.released.weh.filesystem.stdio.StdioSink
import kotlinx.io.Buffer

internal class CapturingStdioSink(
    private val bytes: MutableList<Byte>,
) : StdioSink {

    override fun write(
        source: Buffer,
        byteCount: Long,
    ) {
        repeat(byteCount.toInt()) {
            bytes += source.readByte()
        }
    }

    override fun flush() = Unit

    override fun close() = Unit
}
