package io.github.charlietap.chasm.executor.invoker.component.canonical

import io.github.charlietap.chasm.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.memory.read.BytesReader
import io.github.charlietap.chasm.memory.write.BytesWriter
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalStringEncoding
import io.github.charlietap.chasm.runtime.instance.MemoryInstance

internal fun CanonicalStringLifter(
    context: CanonicalCallContext,
    pointer: Int,
    taggedCodeUnits: Int,
): String {
    val memory = context.memory()
    val encoded = when (context.encoding) {
        CanonicalStringEncoding.Utf8 -> EncodedString(taggedCodeUnits, 1, StringEncoding.Utf8)
        CanonicalStringEncoding.Utf16 -> EncodedString(
            checkedByteLength(taggedCodeUnits, 2),
            2,
            StringEncoding.Utf16,
        )
        CanonicalStringEncoding.Latin1Utf16 -> if (taggedCodeUnits and UTF16_TAG != 0) {
            EncodedString(
                checkedByteLength(taggedCodeUnits xor UTF16_TAG, 2),
                2,
                StringEncoding.Utf16,
            )
        } else {
            EncodedString(taggedCodeUnits, 2, StringEncoding.Latin1)
        }
    }
    if (encoded.bytes < 0 || encoded.bytes > MAX_STRING_BYTE_LENGTH) invalidValue("canonical string is too large")
    if (pointer and (encoded.alignment - 1) != 0) invalidValue("canonical string pointer is misaligned")

    val bytes = readBytes(memory, pointer, encoded.bytes)
    return when (encoded.encoding) {
        StringEncoding.Utf8 -> decodeUtf8(bytes)
        StringEncoding.Utf16 -> decodeUtf16(bytes)
        StringEncoding.Latin1 -> decodeLatin1(bytes)
    }
}

internal fun CanonicalStringLowerer(
    context: CanonicalCallContext,
    value: String,
): Long {
    validateUnicode(value)
    val encoded = when (context.encoding) {
        CanonicalStringEncoding.Utf8 -> {
            val bytes = value.encodeToByteArray()
            LoweredString(bytes, bytes.size)
        }
        CanonicalStringEncoding.Utf16 -> {
            val bytes = encodeUtf16(value)
            LoweredString(bytes, value.length)
        }
        CanonicalStringEncoding.Latin1Utf16 -> if (value.all { character -> character.code <= UBYTE_MAX }) {
            val bytes = ByteArray(value.length) { index -> value[index].code.toByte() }
            LoweredString(bytes, value.length)
        } else {
            val bytes = encodeUtf16(value)
            LoweredString(bytes, value.length or UTF16_TAG)
        }
    }
    if (encoded.bytes.size > MAX_STRING_BYTE_LENGTH) invalidValue("canonical string is too large")
    val alignment = if (
        context.encoding == CanonicalStringEncoding.Utf16 ||
        context.encoding == CanonicalStringEncoding.Latin1Utf16
    ) {
        2
    } else {
        1
    }
    val pointer = context.realloc(alignment, encoded.bytes.size)
    writeBytes(context.memory(), pointer, encoded.bytes)
    return pointer.toUInt().toLong() or (encoded.taggedCodeUnits.toUInt().toLong() shl Int.SIZE_BITS)
}

private fun readBytes(
    memory: MemoryInstance,
    pointer: Int,
    size: Int,
): ByteArray = PessimisticBoundsChecker(pointer, size, memory.size) {
    BytesReader(memory.data, ByteArray(size), pointer, size, 0)
}

private fun writeBytes(
    memory: MemoryInstance,
    pointer: Int,
    bytes: ByteArray,
) = PessimisticBoundsChecker(pointer, bytes.size, memory.size) {
    BytesWriter(memory.data, memory.size, bytes, pointer, bytes.size, 0)
}

private fun decodeUtf8(bytes: ByteArray): String = try {
    bytes.decodeToString(throwOnInvalidSequence = true)
} catch (_: Exception) {
    invalidValue("canonical string contains invalid UTF-8")
}

private fun decodeUtf16(bytes: ByteArray): String {
    if (bytes.size and 1 != 0) invalidValue("canonical UTF-16 string has an odd byte length")
    val characters = CharArray(bytes.size / 2) { index ->
        val offset = index * 2
        ((bytes[offset].toInt() and 0xff) or ((bytes[offset + 1].toInt() and 0xff) shl 8)).toChar()
    }
    val value = characters.concatToString()
    validateUnicode(value)
    return value
}

private fun decodeLatin1(bytes: ByteArray): String = CharArray(bytes.size) { index ->
    (bytes[index].toInt() and 0xff).toChar()
}.concatToString()

private fun encodeUtf16(value: String): ByteArray = ByteArray(value.length * 2) { index ->
    val character = value[index / 2].code
    if (index and 1 == 0) character.toByte() else (character ushr 8).toByte()
}

private fun validateUnicode(value: String) {
    var index = 0
    while (index < value.length) {
        val character = value[index]
        when {
            character.isHighSurrogate() -> {
                if (index + 1 >= value.length || !value[index + 1].isLowSurrogate()) {
                    invalidValue("component string contains an unpaired surrogate")
                }
                index += 2
            }
            character.isLowSurrogate() -> invalidValue("component string contains an unpaired surrogate")
            else -> index += 1
        }
    }
}

private fun checkedByteLength(
    codeUnits: Int,
    codeUnitSize: Int,
): Int {
    if (codeUnits < 0 || codeUnits > MAX_STRING_BYTE_LENGTH / codeUnitSize) {
        invalidValue("canonical string is too large")
    }
    return codeUnits * codeUnitSize
}

private data class EncodedString(
    val bytes: Int,
    val alignment: Int,
    val encoding: StringEncoding,
)

private data class LoweredString(
    val bytes: ByteArray,
    val taggedCodeUnits: Int,
)

private enum class StringEncoding {
    Utf8,
    Utf16,
    Latin1,
}

private const val UTF16_TAG = Int.MIN_VALUE
private const val UBYTE_MAX = 0xff
private const val MAX_STRING_BYTE_LENGTH = (1 shl 28) - 1
