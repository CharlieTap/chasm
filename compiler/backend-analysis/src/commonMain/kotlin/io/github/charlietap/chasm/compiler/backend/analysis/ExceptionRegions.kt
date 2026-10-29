package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.ExceptionRegion

fun innermostRegions(
    outermostFirst: List<ExceptionRegion>,
    positions: Int,
): IntArray {
    val innermost = IntArray(positions) { -1 }
    outermostFirst.forEachIndexed { index, region ->
        for (position in region.start.index until region.end.index) innermost[position] = index
    }
    return innermost
}
