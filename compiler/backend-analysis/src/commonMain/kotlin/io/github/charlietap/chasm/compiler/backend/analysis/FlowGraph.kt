package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.CirFunction

internal class FlowGraph(
    val positions: Int,
    val nodes: Int,
    val successors: PropagationGraph,
    val predecessors: PropagationGraph,
) {
    val sizeInWords: Long get() = successors.sizeInWords + predecessors.sizeInWords

    fun hasMarkedSuccessor(
        node: Int,
        marked: BooleanArray,
    ): Boolean {
        successors.forEachReached(node) { successor ->
            if (marked[successor]) {
                return true
            }
        }
        return false
    }
}

internal fun flowGraph(
    table: InstructionTable,
    function: CirFunction,
): FlowGraph {

    val positions = function.instructions.size
    val nodes = positions + (function.exceptions?.regions?.size ?: 0)

    var edges = 0
    forEachFlowEdge(table, function) { _, _ -> edges++ }

    val sources = IntArray(edges)
    val destinations = IntArray(edges)
    edges = 0
    forEachFlowEdge(table, function) { source, destination ->
        sources[edges] = source
        destinations[edges++] = destination
    }

    return FlowGraph(
        positions = positions,
        nodes = nodes,
        successors = PropagationGraph(nodes, sources, destinations),
        predecessors = PropagationGraph(nodes, destinations, sources),
    )
}

private inline fun forEachFlowEdge(
    table: InstructionTable,
    function: CirFunction,
    action: (source: Int, destination: Int) -> Unit,
) {

    val instructions = function.instructions
    val positions = instructions.size
    val jumps = table.jumps

    for (position in 0 until positions) {

        if (instructions[position].fallsThrough() && position + 1 < positions) {
            action(position, position + 1)
        }
        for (edge in jumps.start[position] until jumps.end(position)) {
            if (jumps.targets[edge] < positions) {
                action(position, jumps.targets[edge])
            }
        }
        if (table.restartsFunction(position)) {
            action(position, 0)
        }

        val region = table.innermostRegion(position)
        if (region >= 0) {
            action(position, positions + region)
        }
    }

    function.exceptions?.regions?.forEachIndexed { index, region ->
        region.catches.forEach { handler ->
            if (handler.target.index < positions) {
                action(positions + index, handler.target.index)
            }
        }
        region.parent?.let { parent -> action(positions + index, positions + parent) }
    }
}
