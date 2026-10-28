package io.github.charlietap.chasm.compiler.backend.chasm

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.runtime.store.Store

class FunctionLayout(
    private val instructionCount: Int,
    private val unlinkedCheckpoints: IntArray,
) {

    val size: Int
        get() = instructionCount - unlinkedCheckpoints.size

    fun offset(
        position: Int,
    ): Int = position - unlinkedCheckpointsBefore(unlinkedCheckpoints, position)

    fun position(
        offset: Int,
    ): Int {
        if (offset !in 0 until size) return -1
        var low = 0
        var high = unlinkedCheckpoints.size
        while (low < high) {
            val middle = (low + high) ushr 1
            if (unlinkedCheckpoints[middle] - middle <= offset) low = middle + 1 else high = middle
        }
        return offset + low
    }
}

fun FunctionLayout(
    function: CirFunction,
    store: Store,
): FunctionLayout = FunctionLayout(function, linksCheckpoints(store))

internal fun FunctionLayout(
    function: CirFunction,
    linksCheckpoints: Boolean,
): FunctionLayout = FunctionLayout(
    instructionCount = function.instructions.size,
    unlinkedCheckpoints = if (linksCheckpoints) NO_POSITIONS else function.checkpoints,
)

internal fun linksCheckpoints(
    store: Store,
): Boolean = store.fuel.metered || store.interrupt.enabled

internal fun unlinkedCheckpointsBefore(
    unlinkedCheckpoints: IntArray,
    position: Int,
    from: Int = 0,
): Int {
    var low = from
    var high = unlinkedCheckpoints.size
    while (low < high) {
        val middle = (low + high) ushr 1
        if (unlinkedCheckpoints[middle] < position) low = middle + 1 else high = middle
    }
    return low
}

internal val NO_POSITIONS = IntArray(0)
