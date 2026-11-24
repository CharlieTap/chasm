package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.compiler.backend.analysis.InstructionTable
import io.github.charlietap.chasm.compiler.backend.analysis.loopCrossings
import io.github.charlietap.chasm.compiler.backend.dex.analysis.DexSizeEstimate
import io.github.charlietap.chasm.compiler.backend.dex.analysis.FunctionFacts
import io.github.charlietap.chasm.compiler.backend.dex.analysis.livenessGraph

internal class SplitFacts(
    function: CirFunction,
    facts: FunctionFacts,
    val estimate: DexSizeEstimate?,
) {
    val crossings = loopCrossings(facts.table.jumps)

    private val handlerTargets = BooleanArray(function.instructions.size + 1).also { targets ->
        function.exceptions?.regions?.forEach { region -> region.catches.forEach { handler -> targets[handler.target.index] = true } }
    }

    private val sources = JumpSources(function, facts.table)

    val continuations = continuations(function, facts.table, handlerTargets)

    val ranked = continuations.count { id -> id >= 0 }

    val graph = livenessGraph(facts.table, function, facts.frame.frameSlots)

    val liveness = functionLiveness(function, graph)

    val live = liveness?.let { solved -> solved::liveCount }

    fun entry(
        position: Int,
        start: Int,
        end: Int,
    ): Boolean {
        return position == start || handlerTargets[position] || sources.enters(position, start, end)
    }
}

private class JumpSources(
    function: CirFunction,
    table: InstructionTable,
) {
    private val first = IntArray(function.instructions.size + 1) { Int.MAX_VALUE }
    private val last = IntArray(function.instructions.size + 1) { -1 }

    init {
        function.instructions.forEachIndexed { position, instruction ->

            val dispatched = instruction is AdminInstruction.JumpTableS

            for (edge in table.jumps.start[position] until table.jumps.end(position)) {
                val target = table.jumps.targets[edge]
                first[target] = if (dispatched) -1 else minOf(first[target], position)
                last[target] = maxOf(last[target], position)
            }
        }
    }

    fun enters(
        target: Int,
        start: Int,
        end: Int,
    ): Boolean {
        return first[target] < start || last[target] >= end
    }
}

private fun continuations(
    function: CirFunction,
    table: InstructionTable,
    handlerTargets: BooleanArray,
): IntArray {

    val size = function.instructions.size
    val targets = handlerTargets.copyOf()
    for (edge in 0 until table.jumps.count) targets[table.jumps.targets[edge]] = true

    var next = 0

    return IntArray(size + 1) { position -> if (position < size && (position == 0 || targets[position])) next++ else -1 }
}
