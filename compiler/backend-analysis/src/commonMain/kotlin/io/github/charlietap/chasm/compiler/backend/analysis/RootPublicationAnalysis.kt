package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.Instruction

/**
 * Which writes of a reference slot a frame must publish to its roots: only those a collection may still follow before
 * the function returns.
 */
class RootPublication(
    private val afterPosition: BooleanArray,
    val afterEntry: Boolean,
) {

    /** Whether a write made by the instruction at [position] must be published; outside the body, always. */
    fun requiresPublicationAfter(
        position: Int,
    ): Boolean = position !in afterPosition.indices || afterPosition[position]

    companion object {
        val EVERY_WRITE = RootPublication(BooleanArray(0), afterEntry = true)
    }
}

/**
 * Finds which writes [function] must publish. A self tail call outside any try region lowers to a jump to the start,
 * and anything inside a try region may raise to that region's handlers or, through its parents, to theirs.
 */
fun analyzeRootPublication(
    function: CirFunction,
    mayCollect: BooleanArray,
): RootPublication {
    val instructions = function.instructions
    val positions = instructions.size
    val regions = function.exceptions?.regions.orEmpty()
    val innermost = innermostRegions(regions, positions)
    val nodes = positions + regions.size
    val graph = ReversedEdges(nodes)

    for (position in 0 until positions) {
        val instruction = instructions[position]
        if (instruction.fallsThrough() && position + 1 < positions) graph.add(position, position + 1)
        for (target in instruction.jumpTargets()) if (target < positions) graph.add(position, target)
        if (instruction.isSelfTailCall(function, innermost[position])) graph.add(position, 0)
        if (innermost[position] >= 0) graph.add(position, positions + innermost[position])
    }
    regions.forEachIndexed { index, region ->
        region.catches.forEach { handler -> if (handler.target.index < positions) graph.add(positions + index, handler.target.index) }
        region.parent?.let { parent -> graph.add(positions + index, positions + parent) }
    }

    val collects = BooleanArray(nodes) { node -> node < positions && instructions[node].mayCollectWhileFrameLive(mayCollect) }
    graph.propagation().spread(collects)

    val afterPosition = BooleanArray(positions)
    graph.forEachEdge { source, successor -> if (source < positions && collects[successor]) afterPosition[source] = true }
    return RootPublication(afterPosition, afterEntry = positions > 0 && collects[0])
}

/** Whether [this] is a tail call by [function] to itself outside any try region, [region], which lowers to a jump. */
fun Instruction.isSelfTailCall(
    function: CirFunction,
    region: Int,
): Boolean = this is ControlInstruction.ReturnCall && this.function == function.index && region < 0

private class ReversedEdges(
    private val nodes: Int,
) {
    private var sources = IntArray(INITIAL_EDGES)
    private var successors = IntArray(INITIAL_EDGES)
    private var edges = 0

    fun add(
        source: Int,
        successor: Int,
    ) {
        if (edges == sources.size) {
            sources = sources.copyOf(edges * 2)
            successors = successors.copyOf(edges * 2)
        }
        sources[edges] = source
        successors[edges++] = successor
    }

    fun propagation(): PropagationGraph = PropagationGraph(nodes, successors.copyOf(edges), sources.copyOf(edges))

    inline fun forEachEdge(
        action: (source: Int, successor: Int) -> Unit,
    ) {
        for (edge in 0 until edges) action(sources[edge], successors[edge])
    }
}

private const val INITIAL_EDGES = 64
