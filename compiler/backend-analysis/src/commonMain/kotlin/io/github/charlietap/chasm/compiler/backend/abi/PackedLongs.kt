package io.github.charlietap.chasm.compiler.backend.abi

private const val GROUP_BITS = 7
private const val GROUP = 0x7F
private const val SIGN = 0x40
private const val CONTINUES = 0x80
private const val MAX_GROUPS = 10

fun packLongs(
    values: LongArray,
    maxBytes: Int = 60_000,
): List<String> {
    val parts = ArrayList<String>()
    val part = StringBuilder()
    var partBytes = 0
    val groups = CharArray(MAX_GROUPS)
    for (value in values) {
        val count = encodeGroups(value, groups)
        val bytes = modifiedUtf8Bytes(groups, count)
        require(bytes <= maxBytes) { "a value takes $bytes bytes, more than the $maxBytes allowed" }
        if (partBytes + bytes > maxBytes) {
            parts += part.toString()
            part.clear()
            partBytes = 0
        }
        part.appendRange(groups, 0, count)
        partBytes += bytes
    }
    if (part.isNotEmpty() || parts.isEmpty()) parts += part.toString()
    return parts
}

private fun encodeGroups(
    value: Long,
    groups: CharArray,
): Int {
    var remaining = value
    var count = 0
    while (true) {
        val group = (remaining and GROUP.toLong()).toInt()
        remaining = remaining shr GROUP_BITS
        val last = (remaining == 0L && group and SIGN == 0) || (remaining == -1L && group and SIGN != 0)
        groups[count++] = (if (last) group else group or CONTINUES).toChar()
        if (last) return count
    }
}

private fun modifiedUtf8Bytes(
    chars: CharArray,
    count: Int,
): Int {
    var bytes = 0
    for (index in 0 until count) bytes += if (chars[index].code in 1..0x7F) 1 else 2
    return bytes
}
