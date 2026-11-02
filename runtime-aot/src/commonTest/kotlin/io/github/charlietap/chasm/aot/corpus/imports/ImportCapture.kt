package io.github.charlietap.chasm.aot.corpus.imports

import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.corpus.lib.fixture.FixtureImportCapture

internal class ImportCapture(
    var maxCalls: Int,
) {
    val text = StringBuilder()

    private var calls = 0

    fun append(
        capture: FixtureImportCapture,
        params: List<ExecutionValue>,
    ) {
        if (calls < maxCalls) {
            val value = params.getOrNull(capture.param)
                ?: error("Import capture ${capture.name} param ${capture.param} is missing")
            val bits = value.toLongFromBoxed()
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
