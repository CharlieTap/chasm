package io.github.charlietap.chasm.compiled.internal

import io.github.charlietap.chasm.declarations.DataReader
import io.github.charlietap.chasm.declarations.DataSource
import java.io.ByteArrayOutputStream
import java.io.EOFException
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile
import java.net.URI
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.util.concurrent.ConcurrentHashMap

internal class ClassLoaderResources(
    private val loader: ClassLoader?,
) : ModuleResources {

    private val sources = ConcurrentHashMap<String, ResourceDataSource>()

    override fun contains(path: String): Boolean {

        if (loader == null) {
            return false
        }
        if (loader.getResource(path) != null) {
            return true
        }

        val stream = loader.getResourceAsStream(path) ?: return false
        stream.close()

        return true
    }

    override fun length(path: String): Long? {
        return source(path).length()
    }

    override fun open(path: String): InputStream? {
        return loader?.getResourceAsStream(path)
    }

    override fun data(path: String): DataSource {
        return source(path)
    }

    private fun source(path: String): ResourceDataSource {
        return sources.getOrPut(path) { ResourceDataSource(loader, path) }
    }
}

private class ResourceDataSource(
    private val loader: ClassLoader?,
    private val path: String,
) : DataSource {

    private val mapped: ByteBuffer? by lazy { mapResource(loader, path) }

    fun length(): Long? {

        val bytes = mapped
        if (bytes != null) {
            return bytes.capacity().toLong()
        }

        if (loader == null) {
            return null
        }

        val declared = loader.getResource(path)?.openConnection()?.contentLengthLong ?: UNKNOWN_LENGTH
        if (declared >= 0) {
            return declared
        }

        return loader.getResourceAsStream(path)?.use(::streamLength)
    }

    override fun open(): DataReader {

        val bytes = mapped
        if (bytes != null) {
            return MappedReader(path, bytes)
        }

        val stream = loader?.getResourceAsStream(path) ?: throw IllegalStateException("resource $path is missing")
        return StreamReader(path, stream)
    }
}

private fun streamLength(stream: InputStream): Long {

    val buffer = ByteArray(LENGTH_BUFFER)
    var length = 0L

    while (true) {
        val count = stream.read(buffer)
        if (count < 0) {
            return length
        }
        length += count
    }
}

@Suppress("TooGenericExceptionCaught", "SwallowedException")
private fun mapResource(
    loader: ClassLoader?,
    path: String,
): ByteBuffer? = try {
    val url = loader?.getResource(path)
    when (url?.protocol) {
        "file" -> RandomAccessFile(File(url.toURI()), "r").use { file -> map(file.channel, 0, file.length()) }
        "jar" -> {
            val spec = url.path
            val separator = spec.indexOf("!/")
            val archive = URI(spec.substring(0, separator))
            val entry = spec.substring(separator + 2)
            if (archive.scheme != "file" || "!/" in entry) {
                null
            } else {
                RandomAccessFile(File(archive), "r").use { file -> mapStoredEntry(file.channel, percentDecoded(entry)) }
            }
        }
        else -> null
    }
} catch (_: Exception) {
    null
}

private fun map(
    channel: FileChannel,
    position: Long,
    size: Long,
): ByteBuffer? = if (size > Int.MAX_VALUE) null else channel.map(FileChannel.MapMode.READ_ONLY, position, size)

private fun mapStoredEntry(
    channel: FileChannel,
    name: ByteArray,
): ByteBuffer? {
    val size = channel.size()
    val tailSize = minOf(size, MAX_END_RECORD).toInt()
    val tailStart = size - tailSize
    val tail = read(channel, tailStart, tailSize)
    var end = tailSize - END_RECORD
    while (end >= 0 && !(tail.getInt(end) == END_SIGNATURE && isEndRecord(tail, end, tailStart + end))) end--
    if (end < 0) return null
    val directorySize = tail.u32(end + 12)
    val directoryOffset = tail.u32(end + 16)
    val directory = read(channel, directoryOffset, Math.toIntExact(directorySize))
    var entry = 0
    while (entry + DIRECTORY_HEADER <= directory.limit() && directory.getInt(entry) == DIRECTORY_SIGNATURE) {
        val nameLength = directory.u16(entry + 28)
        val next = entry + DIRECTORY_HEADER + nameLength + directory.u16(entry + 30) + directory.u16(entry + 32)
        if (nameLength == name.size && matches(directory, entry + DIRECTORY_HEADER, name)) {
            val flags = directory.u16(entry + 8)
            val method = directory.u16(entry + 10)
            val compressedSize = directory.u32(entry + 20)
            val uncompressedSize = directory.u32(entry + 24)
            val headerOffset = directory.u32(entry + 42)
            if (flags and ENCRYPTED != 0 || method != STORED || compressedSize != uncompressedSize) return null
            if (compressedSize == ZIP64_MARKER || headerOffset == ZIP64_MARKER) return null
            return mapLocalEntry(channel, name, headerOffset, compressedSize, directoryOffset)
        }
        entry = next
    }
    return null
}

private fun isEndRecord(
    tail: ByteBuffer,
    end: Int,
    position: Long,
): Boolean =
    end + END_RECORD + tail.u16(end + 20) == tail.limit() &&
        tail.u16(end + 4) == 0 &&
        tail.u16(end + 6) == 0 &&
        tail.u16(end + 8) == tail.u16(end + 10) &&
        tail.u32(end + 16) + tail.u32(end + 12) == position

private fun mapLocalEntry(
    channel: FileChannel,
    name: ByteArray,
    headerOffset: Long,
    size: Long,
    directoryOffset: Long,
): ByteBuffer? {
    if (headerOffset + LOCAL_HEADER + name.size > directoryOffset) return null
    val header = read(channel, headerOffset, LOCAL_HEADER + name.size)
    val flags = header.u16(6)
    val consistent = header.getInt(0) == LOCAL_SIGNATURE &&
        flags and ENCRYPTED == 0 &&
        header.u16(8) == STORED &&
        header.u16(26) == name.size &&
        matches(header, LOCAL_HEADER, name) &&
        (flags and DATA_DESCRIPTOR != 0 || header.u32(18) == size && header.u32(22) == size)
    if (!consistent) return null
    val start = headerOffset + LOCAL_HEADER + name.size + header.u16(28)
    return if (start + size > directoryOffset) null else map(channel, start, size)
}

private fun matches(
    buffer: ByteBuffer,
    at: Int,
    bytes: ByteArray,
): Boolean = bytes.indices.all { index -> buffer.get(at + index) == bytes[index] }

private fun ByteBuffer.u16(
    at: Int,
): Int = getShort(at).toInt() and 0xFFFF

private fun ByteBuffer.u32(
    at: Int,
): Long = getInt(at).toLong() and 0xFFFFFFFFL

private fun percentDecoded(
    text: String,
): ByteArray {
    val bytes = ByteArrayOutputStream()
    var index = 0
    while (index < text.length) {
        val char = text[index]
        if (char == '%' && index + 2 < text.length) {
            bytes.write(text.substring(index + 1, index + 3).toInt(16))
            index += 3
        } else {
            bytes.write(char.toString().toByteArray(Charsets.UTF_8))
            index++
        }
    }
    return bytes.toByteArray()
}

private fun read(
    channel: FileChannel,
    position: Long,
    size: Int,
): ByteBuffer {
    val buffer = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN)
    while (buffer.hasRemaining()) {
        if (channel.read(buffer, position + buffer.position()) < 0) throw EOFException("zip ends early")
    }
    val flipped: Buffer = buffer
    flipped.flip()
    return buffer
}

private const val STORED = 0
private const val ENCRYPTED = 0x1
private const val DATA_DESCRIPTOR = 0x8
private const val END_SIGNATURE = 0x06054b50
private const val DIRECTORY_SIGNATURE = 0x02014b50
private const val LOCAL_SIGNATURE = 0x04034b50
private const val END_RECORD = 22
private const val DIRECTORY_HEADER = 46
private const val LOCAL_HEADER = 30
private const val ZIP64_MARKER = 0xFFFFFFFFL

private const val MAX_END_RECORD = 22L + 0xFFFF

private const val LENGTH_BUFFER = 64 * 1024

private const val UNKNOWN_LENGTH = -1L
