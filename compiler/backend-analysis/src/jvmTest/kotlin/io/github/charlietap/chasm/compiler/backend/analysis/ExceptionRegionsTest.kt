package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.ExceptionRegion
import io.github.charlietap.chasm.cir.InstructionIndex
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals

class ExceptionRegionsTest {

    @Test
    fun `nested, adjacent and empty regions each cover only their own positions`() {
        val regions = listOf(
            region(1, 9, parent = null),
            region(1, 4, parent = 0),
            region(4, 4, parent = 0),
            region(4, 7, parent = 0),
            region(5, 6, parent = 3),
        )

        assertContentEquals(intArrayOf(-1, 1, 1, 1, 3, 4, 3, 0, 0, -1, -1), innermostRegions(regions, 11))
    }

    @Test
    fun `the sweep agrees with filling every region's positions in order`() {
        val random = Random(7)
        repeat(500) {
            val positions = random.nextInt(1, 60)
            val regions = ArrayList<ExceptionRegion>()
            nest(random, regions, start = 0, end = positions, parent = null, depth = 0)

            assertContentEquals(filled(regions, positions), innermostRegions(regions, positions))
        }
    }

    // Adds regions inside [start, end) as the frontend would: in order of entry, each parent before its children.
    private fun nest(
        random: Random,
        regions: MutableList<ExceptionRegion>,
        start: Int,
        end: Int,
        parent: Int?,
        depth: Int,
    ) {
        var cursor = start
        while (cursor < end && depth < 6 && random.nextInt(3) != 0) {
            val regionStart = random.nextInt(cursor, end + 1)
            val regionEnd = random.nextInt(regionStart, end + 1)
            val index = regions.size
            regions += region(regionStart, regionEnd, parent)
            nest(random, regions, regionStart, regionEnd, index, depth + 1)
            cursor = regionEnd
        }
    }

    private fun filled(
        regions: List<ExceptionRegion>,
        positions: Int,
    ): IntArray {
        val innermost = IntArray(positions) { -1 }
        regions.forEachIndexed { index, region ->
            for (position in region.start.index until region.end.index) innermost[position] = index
        }
        return innermost
    }

    private fun region(
        start: Int,
        end: Int,
        parent: Int?,
    ) = ExceptionRegion(InstructionIndex(start), InstructionIndex(end), parent, emptyList())
}
