package io.github.charlietap.chasm.corpus.imports

import io.github.charlietap.corpus.lib.fixture.FixtureImportCapture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ImportCaptureTest {

    @Test
    fun `code points below the supplementary planes append one character`() {

        val capture = ImportCapture(maxCalls = 2)

        capture.append(CODE_POINT, listOf(0x41L))
        capture.append(CODE_POINT, listOf(0xe9L))

        assertEquals("Aé", capture.text.toString())
    }

    @Test
    fun `supplementary code points append a surrogate pair`() {

        val capture = ImportCapture(maxCalls = 2)

        capture.append(CODE_POINT, listOf(0x1f600L))
        capture.append(CODE_POINT, listOf(0x10ffffL))

        assertEquals("😀􏿿", capture.text.toString())
    }

    @Test
    fun `a value beyond the last code point is rejected`() {

        val capture = ImportCapture(maxCalls = 1)

        assertFailsWith<IllegalArgumentException> {
            capture.append(CODE_POINT, listOf(0x110000L))
        }
    }

    private companion object {
        val CODE_POINT = FixtureImportCapture(name = "putchar", format = "unicode_code_point")
    }
}
