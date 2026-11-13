package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.Instruction

/**
 * Which writes of a reference slot a frame must publish to its roots: only those a collection may still follow before
 * the function returns.
 */
class RootPublication internal constructor(
    private val afterPosition: BooleanArray,
    val afterEntry: Boolean,
    private val needs: RootNeeds?,
) {

    /** Whether a write made by the instruction at [position] must be published; outside the body, always. */
    fun requiresPublicationAfter(
        position: Int,
    ): Boolean = position !in afterPosition.indices || afterPosition[position]

    fun requiresPublication(
        position: Int,
        root: Int,
    ): Boolean {
        return when {
            position == ENTRY -> afterEntry && neededOnEntry(root)
            !requiresPublicationAfter(position) -> false
            needs == null || position !in 0 until needs.positions -> true
            else -> needs.neededAfter(position, root)
        }
    }

    internal fun neededOnEntry(root: Int): Boolean {
        return needs == null || needs.neededOnEntry(root)
    }

    companion object {
        val EVERY_WRITE = RootPublication(BooleanArray(0), afterEntry = true, needs = null)

        const val ENTRY = -1

        const val COLD_CODE = Int.MAX_VALUE
    }
}

/**
 * Finds which writes [function] must publish. A self tail call outside any try region lowers to a jump to the start,
 * and anything inside a try region may raise to that region's handlers or, through its parents, to theirs.
 */
internal fun analyzeRootPublication(
    graph: FlowGraph,
    function: CirFunction,
    mayCollect: BooleanArray,
    needs: RootNeeds?,
): RootPublication {

    val instructions = function.instructions
    val positions = graph.positions
    val collects = BooleanArray(graph.nodes) { node -> node < positions && instructions[node].mayCollectWhileFrameLive(mayCollect) }
    graph.predecessors.spread(collects)

    val afterPosition = BooleanArray(positions) { position -> graph.hasMarkedSuccessor(position, collects) }
    return RootPublication(afterPosition, afterEntry = positions > 0 && collects[0], needs)
}

/** Whether [this] is a tail call by [function] to itself outside any try region, [region], which lowers to a jump. */
fun Instruction.isSelfTailCall(
    function: CirFunction,
    region: Int,
): Boolean = this is ControlInstruction.ReturnCall && this.function == function.index && region < 0
