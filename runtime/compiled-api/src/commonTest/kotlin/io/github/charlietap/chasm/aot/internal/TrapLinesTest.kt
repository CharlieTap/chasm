package io.github.charlietap.chasm.aot.internal

import io.github.charlietap.chasm.InternalChasmApi
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(InternalChasmApi::class)
class TrapLinesTest {

    @Test
    fun `ranges listed out of order are found`() {
        val lines = lines(
            LineRange(first = 20, last = 29, original = 50, linear = true),
            LineRange(first = 1, last = 9, original = 3, linear = false),
            LineRange(first = 10, last = 19, original = 40, linear = true),
        )

        assertEquals(3, lines.original("Code0", "f1", 5))
        assertEquals(42, lines.original("Code0", "f1", 12))
        assertEquals(59, lines.original("Code0", "f1", 29))
        assertEquals(-1, lines.original("Code0", "f1", 30))
        assertEquals(-1, lines.original("Code0", "f1", 0))
    }

    @Test
    fun `where ranges overlap the first listed to cover a line wins`() {
        val lines = lines(
            LineRange(first = 5, last = 15, original = 70, linear = false),
            LineRange(first = 1, last = 10, original = 30, linear = false),
        )

        assertEquals(30, lines.original("Code0", "f1", 3))
        assertEquals(70, lines.original("Code0", "f1", 7))
        assertEquals(70, lines.original("Code0", "f1", 12))
        assertEquals(-1, lines.original("Code0", "f1", 16))
    }

    private fun lines(
        vararg ranges: LineRange,
    ) = TrapLines(mapOf(trapLinesKey("Code0", "f1") to ranges.toList()))
}
