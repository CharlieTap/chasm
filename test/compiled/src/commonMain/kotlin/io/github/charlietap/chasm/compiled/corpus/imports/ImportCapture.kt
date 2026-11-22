package io.github.charlietap.chasm.compiled.corpus.imports

import io.github.charlietap.corpus.lib.fixture.FixtureImportCapture

internal class ImportCapture(
    var maxCalls: Int,
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
                "utf16_code_unit" -> text.append((bits.toInt() and 0xffff).toChar())
                "unicode_code_point" -> text.appendCodePoint(bits.toInt())
                "decimal_i32" -> text.append(bits.toInt())
                else -> error("Unsupported import capture format: ${capture.format ?: capture.encoding}")
            }
        }
        calls++
    }
}
