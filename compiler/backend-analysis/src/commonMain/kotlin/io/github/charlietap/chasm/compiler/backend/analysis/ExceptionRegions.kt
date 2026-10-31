package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.ExceptionRegion

/**
 * The innermost region covering each position, or -1. Regions nest and are ordered by start with each parent before its
 * children, as the frontend enters them, so one sweep with a stack of open regions finds them.
 */
fun innermostRegions(
    outermostFirst: List<ExceptionRegion>,
    positions: Int,
): IntArray {
    val innermost = IntArray(positions)
    val open = IntArray(outermostFirst.size)
    var depth = 0
    var next = 0
    for (position in 0 until positions) {
        while (depth > 0 && outermostFirst[open[depth - 1]].end.index <= position) depth--
        while (next < outermostFirst.size && outermostFirst[next].start.index <= position) {
            if (outermostFirst[next].end.index > position) open[depth++] = next
            next++
        }
        innermost[position] = if (depth > 0) open[depth - 1] else -1
    }
    return innermost
}
