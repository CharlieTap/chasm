package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.type.FunctionType

class RootRefreshes private constructor(
    val afterEntry: Boolean,
    private val analysis: Analysis?,
) {

    fun refreshRequiredAfter(position: Int): Boolean {
        val afterCall = analysis?.afterCall ?: return true
        return position !in afterCall.indices || afterCall[position]
    }

    fun arrayCurrentAt(position: Int): Boolean {
        val current = analysis?.current ?: return true
        return position in current.indices && current[position]
    }

    private data class Analysis(
        val afterCall: BooleanArray,
        val current: BooleanArray,
    )

    companion object {
        val ALWAYS = RootRefreshes(afterEntry = true, analysis = null)

        internal fun analyzed(
            afterEntry: Boolean,
            afterCall: BooleanArray,
            current: BooleanArray,
        ): RootRefreshes {
            return RootRefreshes(afterEntry, Analysis(afterCall, current))
        }
    }
}

internal fun analyzeRootRefreshes(
    table: InstructionTable,
    graph: FlowGraph,
    function: CirFunction,
    type: FunctionType,
    rootIndex: IntArray,
    isReference: BooleanArray,
    publication: RootPublication,
): RootRefreshes {

    val instructions = function.instructions
    val positions = instructions.size
    val writes = table.writes
    val republishes = type.params.types.indices.any { parameter ->
        isReference[parameter] && publication.neededOnEntry(rootIndex[parameter])
    }
    val stops = BooleanArray(graph.nodes) { node ->
        node >= positions || (instructions[node].isCall() && !instructions[node].isTailCall())
    }

    val publishes = BooleanArray(positions)
    val current = BooleanArray(graph.nodes)
    for (position in 0 until positions) {

        var publishing = republishes && table.restartsFunction(position)
        if (!publishing && publication.requiresPublicationAfter(position)) {
            for (index in writes.start[position] until writes.end(position)) {
                val slot = writes.slots[index]
                if (slot in isReference.indices && isReference[slot] && publication.requiresPublication(position, rootIndex[slot])) {
                    publishing = true
                    break
                }
            }
        }

        publishes[position] = publishing
        current[position] = publishing && !stops[position]
    }

    graph.predecessors.spread(current, stops)
    val afterCall = BooleanArray(positions) { position ->
        stops[position] && (publishes[position] || graph.hasMarkedSuccessor(position, current))
    }
    val afterEntry = (publication.afterEntry && republishes) || (positions > 0 && current[0])

    return RootRefreshes.analyzed(afterEntry, afterCall, current)
}
