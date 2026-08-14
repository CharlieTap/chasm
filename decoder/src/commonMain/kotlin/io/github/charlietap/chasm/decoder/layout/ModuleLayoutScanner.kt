package io.github.charlietap.chasm.decoder.layout

internal fun ModuleLayoutScanner(bytes: ByteArray): CodeBodyRanges? {
    if (bytes.size < MODULE_HEADER_SIZE) return null

    val scanner = LayoutScanner(bytes)
    return scanner.scan()
}

private class LayoutScanner(
    private val bytes: ByteArray,
) {

    private var position = MODULE_HEADER_SIZE

    fun scan(): CodeBodyRanges? {
        var codeSectionFound = false
        var codeBodies = EMPTY_CODE_BODY_RANGES

        while (position < bytes.size) {
            val sectionId = readByte()
            val sectionSize = readSize() ?: return null
            val sectionStart = position
            val sectionEnd = endOfRange(sectionStart, sectionSize, bytes.size) ?: return null

            if (sectionId == CODE_SECTION_ID) {
                if (codeSectionFound) return null

                codeSectionFound = true
                codeBodies = scanCodeSection(sectionEnd) ?: return null
            }

            position = sectionEnd
        }

        return codeBodies
    }

    private fun scanCodeSection(sectionEnd: Int): CodeBodyRanges? {
        val count = readSize() ?: return null
        if (count > sectionEnd - position) return null

        val starts = IntArray(count)
        val ends = IntArray(count)
        val sizes = IntArray(count)

        for (index in 0 until count) {
            val start = position
            val bodySize = readSize() ?: return null
            val end = endOfRange(position, bodySize, sectionEnd) ?: return null

            starts[index] = start
            ends[index] = end
            sizes[index] = bodySize
            position = end
        }

        if (position != sectionEnd) return null
        return CodeBodyRanges(starts, ends, sizes)
    }

    private fun readByte(): Int = bytes[position++].toInt() and BYTE_MASK

    private fun readSize(): Int? {
        var value = 0L
        var shift = 0

        repeat(MAX_U32_BYTES) { index ->
            if (position >= bytes.size) return null

            val byte = readByte()
            if (index == MAX_U32_BYTES - 1 && byte > U32_FINAL_BYTE_MAX) return null

            value = value or ((byte and PAYLOAD_MASK).toLong() shl shift)
            if (byte < CONTINUATION_BIT) {
                return if (value <= Int.MAX_VALUE) value.toInt() else null
            }
            shift += PAYLOAD_BITS
        }

        return null
    }
}

private fun endOfRange(
    start: Int,
    size: Int,
    limit: Int,
): Int? {
    if (size > limit - start) return null
    return start + size
}

private val EMPTY_CODE_BODY_RANGES = CodeBodyRanges(IntArray(0), IntArray(0), IntArray(0))

private const val MODULE_HEADER_SIZE = 8
private const val CODE_SECTION_ID = 0x0A
private const val BYTE_MASK = 0xFF
private const val PAYLOAD_MASK = 0x7F
private const val CONTINUATION_BIT = 0x80
private const val PAYLOAD_BITS = 7
private const val MAX_U32_BYTES = 5
private const val U32_FINAL_BYTE_MAX = 0x0F
