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
    val entry = findCodeEntry(bytes, definedFunctionIndex) ?: return null
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

/** The range includes the entry's size prefix. */
private fun findCodeEntry(bytes: ByteArray, definedFunctionIndex: Int): IntRange? {
    val cursor = Cursor(bytes, MODULE_HEADER_SIZE)
    if (bytes.size < MODULE_HEADER_SIZE || definedFunctionIndex < 0) return null
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
        if (definedFunctionIndex >= count) return null
        repeat(definedFunctionIndex + 1) { index ->
            val entryStart = cursor.position
            val entrySize = cursor.unsigned() ?: return null
            val entryEnd = cursor.position + entrySize
            if (entryEnd > sectionEnd || entryEnd < cursor.position) return null
            if (index == definedFunctionIndex) return entryStart until entryEnd
            cursor.position = entryEnd
        }
        return null
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
