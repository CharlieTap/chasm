package io.github.charlietap.chasm.corpus.imports

import io.github.charlietap.corpus.lib.fixture.FixtureImportCapture

internal class ImportCapture(
    internal var maxCalls: Int,
) {
    val text = StringBuilder()

    private var calls = 0

    fun append(
        capture: FixtureImportCapture,
        params: List<Long>,
    ) {

        if (calls < maxCalls) {
            val bits = params.getOrNull(capture.param)
                ?: error("Import capture ${capture.name} param ${capture.param} is missing")
            when (capture.format ?: capture.encoding ?: "utf16_code_unit") {
                "utf16_code_unit" -> {
                    text.append((bits.toInt() and 0xffff).toChar())
                }
                "unicode_code_point" -> {
                    appendCodePoint(bits.toInt())
                }
                "decimal_i32" -> {
                    text.append(bits.toInt())
                }
                else -> error("Unsupported import capture format: ${capture.format ?: capture.encoding}")
            }
        }

        calls++
    }

    private fun appendCodePoint(codePoint: Int) {

        require(codePoint in 0..MAX_CODE_POINT) { "Invalid unicode code point: $codePoint" }

        if (codePoint < SUPPLEMENTARY_START) {
            text.append(codePoint.toChar())
        } else {
            val offset = codePoint - SUPPLEMENTARY_START
            text.append((HIGH_SURROGATE_START + (offset shr SURROGATE_BITS)).toChar())
            text.append((LOW_SURROGATE_START + (offset and SURROGATE_MASK)).toChar())
        }
    }

    private companion object {
        const val MAX_CODE_POINT = 0x10ffff
        const val SUPPLEMENTARY_START = 0x10000
        const val HIGH_SURROGATE_START = 0xd800
        const val LOW_SURROGATE_START = 0xdc00
        const val SURROGATE_BITS = 10
        const val SURROGATE_MASK = 0x3ff
    }
}
