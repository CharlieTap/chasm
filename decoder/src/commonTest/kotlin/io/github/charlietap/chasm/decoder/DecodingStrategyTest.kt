package io.github.charlietap.chasm.decoder

import io.github.charlietap.chasm.decoder.layout.CodeBodyRanges
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs

class selectDecodingStrategyTest {

    @Test
    fun `keeps small modules serial`() {
        val strategy = selectDecodingStrategy(
            moduleSize = 1024,
            bodies = bodyRanges(512, 512),
            mode = DecodingMode.AUTO,
            availableProcessors = 8,
        )

        assertEquals(DecodingStrategy.Serial, strategy)
    }

    @Test
    fun `balances large bodies before small bodies`() {
        val strategy = selectDecodingStrategy(
            moduleSize = 256 * 1024,
            bodies = bodyRanges(1000, 900, 100, 100),
            mode = DecodingMode.PARALLEL,
            availableProcessors = 3,
        )

        val assignments = assertIs<DecodingStrategy.Parallel>(strategy).assignments
        assertContentEquals(intArrayOf(0, 3), assignments[0])
        assertContentEquals(intArrayOf(1, 2), assignments[1])
    }

    @Test
    fun `requires two body workers`() {
        val strategy = selectDecodingStrategy(
            moduleSize = 256 * 1024,
            bodies = bodyRanges(1024, 1024),
            mode = DecodingMode.PARALLEL,
            availableProcessors = 2,
        )

        assertEquals(DecodingStrategy.Serial, strategy)
    }

    private fun bodyRanges(vararg sizes: Int): CodeBodyRanges = CodeBodyRanges(
        starts = IntArray(sizes.size),
        ends = IntArray(sizes.size),
        sizes = sizes,
    )
}
