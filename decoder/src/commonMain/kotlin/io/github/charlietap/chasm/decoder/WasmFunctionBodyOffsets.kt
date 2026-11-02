package io.github.charlietap.chasm.decoder

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.context.CodeBodyDecoderContext
import io.github.charlietap.chasm.decoder.decoder.instruction.ExpressionDecoder
import io.github.charlietap.chasm.decoder.decoder.instruction.InstructionDecoder
import io.github.charlietap.chasm.decoder.decoder.section.code.CodeEntryDecoder
import io.github.charlietap.chasm.decoder.decoder.section.code.LocalEntryDecoder
import io.github.charlietap.chasm.decoder.decoder.vector.CodeBodyVectorDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeException
import io.github.charlietap.chasm.decoder.reader.BinaryReader
import io.github.charlietap.chasm.decoder.section.SectionType

fun WasmFunctionBodyOffsets(
    config: ModuleConfig,
    bytes: ByteArray,
    definedFunctionIndex: Int,
): IntArray? {
    if (definedFunctionIndex < 0) return null
    val entry = codeEntries(bytes, definedFunctionIndex, definedFunctionIndex + 1)?.singleOrNull() ?: return null
    return bodyOffsets(config, bytes, entry)
}

/**
 * The body offsets of every defined function in one pass, in definition
 * order. An entry is null when that body cannot be decoded; the list is empty
 * when the code section cannot be found.
 */
fun WasmFunctionBodyOffsets(
    config: ModuleConfig,
    bytes: ByteArray,
): List<IntArray?> = codeEntries(bytes, 0, Int.MAX_VALUE)?.map { entry -> bodyOffsets(config, bytes, entry) }.orEmpty()

private fun bodyOffsets(
    config: ModuleConfig,
    bytes: ByteArray,
    entry: IntRange,
): IntArray? {
    val slice = bytes.copyOfRange(entry.first, entry.last + 1)
    val context = CodeBodyDecoderContext(config, BinaryReader(slice))
    val offsets = ArrayList<Int>()
    val result: Result<*, WasmDecodeError> = try {
        CodeEntryDecoder(
            context = context,
            localEntryDecoder = ::LocalEntryDecoder,
            expressionDecoder = { scoped ->
                ExpressionDecoder(scoped, ::InstructionDecoder) {
                    offsets.add(entry.first + scoped.reader.position().toInt())
                }
            },
            vectorDecoder = ::CodeBodyVectorDecoder,
        )
    } catch (error: WasmDecodeException) {
        Err(error.error)
    } catch (_: Throwable) {
        return null
    }
    return if (result.isOk) offsets.toIntArray() else null
}

/** The ranges of entries [from] until [until], each including its entry's size prefix. */
private fun codeEntries(
    bytes: ByteArray,
    from: Int,
    until: Int,
): List<IntRange>? {
    val cursor = Cursor(bytes, MODULE_HEADER_SIZE)
    if (bytes.size < MODULE_HEADER_SIZE) return null
    while (cursor.position < bytes.size) {
        val id = bytes[cursor.position++].toUByte()
        val size = cursor.unsigned() ?: return null
        val sectionEnd = cursor.position + size
        if (sectionEnd > bytes.size || sectionEnd < cursor.position) return null
        if (id != SectionType.Code.id) {
            cursor.position = sectionEnd
            continue
        }
        val count = cursor.unsigned() ?: return null
        val entries = ArrayList<IntRange>()
        repeat(minOf(count, until)) { index ->
            val entryStart = cursor.position
            val entrySize = cursor.unsigned() ?: return null
            val entryEnd = cursor.position + entrySize
            if (entryEnd > sectionEnd || entryEnd < cursor.position) return null
            if (index >= from) entries += entryStart until entryEnd
            cursor.position = entryEnd
        }
        return entries
    }
    return null
}

private class Cursor(val bytes: ByteArray, var position: Int) {

    fun unsigned(): Int? {
        var result = 0L
        var shift = 0
        while (position < bytes.size && shift < MAX_LEB_SHIFT) {
            val byte = bytes[position++].toInt()
            result = result or ((byte and 0x7F).toLong() shl shift)
            if (byte and 0x80 == 0) return if (result <= Int.MAX_VALUE) result.toInt() else null
            shift += 7
        }
        return null
    }
}

private const val MODULE_HEADER_SIZE = 8
private const val MAX_LEB_SHIFT = 35
