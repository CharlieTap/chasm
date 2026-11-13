package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.compiler.backend.dex.analysis.BlockLiveness
import io.github.charlietap.chasm.compiler.backend.dex.analysis.LivenessGraph

internal fun functionLiveness(
    function: CirFunction,
    graph: LivenessGraph,
): BlockLiveness? {

    if (function.exceptions != null) {
        return null
    }

    val words = graph.words
    if (function.instructions.size.toLong() * words > MAX_LIVENESS_WORDS) {
        return null
    }

    return graph.solve(0, function.instructions.size, LongArray(words))
}

private const val MAX_LIVENESS_WORDS = 16_000_000L
