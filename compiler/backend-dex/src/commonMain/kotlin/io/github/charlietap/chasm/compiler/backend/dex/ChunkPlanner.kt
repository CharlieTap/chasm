package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.compiler.backend.analysis.cheapestCut

internal data class PlannedChunk(
    val start: Int,
    val end: Int,
    val entries: IntArray,
)

internal class ChunkPlanner {
    fun plan(
        function: CirFunction,
        splitFacts: SplitFacts,
        start: Int,
        limit: Int,
    ): PlannedChunk {

        val size = function.instructions.size
        val end = if (limit == size) size else cheapestCut(splitFacts.crossings, start, limit, splitFacts.live)

        var count = 0
        for (position in start until end) {
            if (splitFacts.entry(position, start, end)) {
                count++
            }
        }
        val entries = IntArray(count)
        var next = 0
        for (position in start until end) {
            if (splitFacts.entry(position, start, end)) {
                entries[next++] = position
            }
        }

        return PlannedChunk(start, end, entries)
    }
}
