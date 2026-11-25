package io.github.charlietap.chasm.compiled.internal

import io.github.charlietap.chasm.declarations.DataReader
import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.nio.Buffer
import java.nio.ByteBuffer

internal class MappedReader(
    private val path: String,
    private val bytes: ByteBuffer,
) : DataReader {

    override val length: Long
        get() = bytes.capacity().toLong()

    override fun read(
        offset: Long,
        size: Int,
    ): ByteArray {
        return ByteArray(size).also { destination -> read(offset, size, destination, 0) }
    }

    override fun read(
        offset: Long,
        size: Int,
        destination: ByteArray,
        destinationOffset: Int,
    ) {

        val view = bytes.duplicate()
        val positioned: Buffer = view
        positioned.position(Math.toIntExact(offset))

        try {
            view.get(destination, destinationOffset, size)
        } catch (fault: InternalError) {
            throw IOException("resource $path could not be read", fault)
        }
    }

    override fun close() {
        return
    }
}

internal class StreamReader(
    private val path: String,
    private val stream: InputStream,
) : DataReader {

    private var position = 0L

    override fun read(
        offset: Long,
        size: Int,
    ): ByteArray {

        require(offset >= position) { "payload at $offset read after $position in $path" }
        var skip = offset - position
        while (skip > 0) {
            val skipped = stream.skip(skip)
            if (skipped <= 0) {
                if (stream.read() < 0) {
                    throw EOFException("resource $path ends before $offset")
                }
                skip--
            } else {
                skip -= skipped
            }
        }

        val bytes = ByteArray(size)
        var read = 0
        while (read < size) {
            val chunk = stream.read(bytes, read, size - read)
            if (chunk < 0) {
                throw EOFException("resource $path ends inside the payload at $offset")
            }
            read += chunk
        }

        position = offset + size
        return bytes
    }

    override fun close() {
        stream.close()
    }
}

internal class ByteArrayDataReader(
    private val bytes: ByteArray,
) : DataReader {

    override val length: Long
        get() = bytes.size.toLong()

    override fun read(
        offset: Long,
        size: Int,
    ): ByteArray {
        return ByteArray(size).also { destination -> read(offset, size, destination, 0) }
    }

    override fun read(
        offset: Long,
        size: Int,
        destination: ByteArray,
        destinationOffset: Int,
    ) {

        val start = Math.toIntExact(offset)
        bytes.copyInto(destination, destinationOffset, start, start + size)
    }

    override fun close() {
        return
    }
}

internal class MappedInputStream(
    bytes: ByteBuffer,
) : InputStream() {

    private val view = bytes.duplicate()

    override fun read(): Int {
        return if (view.hasRemaining()) {
            view.get().toInt() and BYTE_MASK
        } else {
            -1
        }
    }

    override fun read(
        destination: ByteArray,
        offset: Int,
        length: Int,
    ): Int {

        if (length == 0) {
            return 0
        }
        if (!view.hasRemaining()) {
            return -1
        }

        val count = minOf(length, view.remaining())
        view.get(destination, offset, count)

        return count
    }

    override fun available(): Int {
        return view.remaining()
    }

    private companion object {
        const val BYTE_MASK = 0xFF
    }
}
