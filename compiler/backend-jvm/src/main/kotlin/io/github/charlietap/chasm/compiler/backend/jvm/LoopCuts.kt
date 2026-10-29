package io.github.charlietap.chasm.compiler.backend.jvm

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.compiler.backend.analysis.jumpTargets

internal fun loopCrossings(
    function: CirFunction,
): IntArray {
    val size = function.instructions.size
    val changes = IntArray(size + 2)
    function.instructions.forEachIndexed { source, instruction ->
        instruction.jumpTargets().forEach { target ->
            if (target <= source) {
                changes[target + 1]++
                changes[source + 1]--
            }
        }
    }
    var running = 0
    return IntArray(size + 1) { position ->
        running += changes[position]
        if (position == size) 0 else running
    }
}

internal fun cheapestCut(
    crossings: IntArray,
    start: Int,
    limit: Int,
    live: ((Int) -> Int)? = null,
): Int {
    var fewest = Int.MAX_VALUE
    var furthest = limit
    for (candidate in limit downTo start + 1) {
        if (crossings[candidate] < fewest) {
            fewest = crossings[candidate]
            furthest = candidate
        }
        if (fewest == 0) break
    }
    if (live == null) return furthest
    val late = start + (limit - start) * 3 / 4
    var best = furthest
    var bestLive = live(furthest)
    for (candidate in furthest - 1 downTo maxOf(late, start + 1)) {
        if (crossings[candidate] != fewest) continue
        val slots = live(candidate)
        if (slots < bestLive) {
            best = candidate
            bestLive = slots
        }
    }
    return best
}
