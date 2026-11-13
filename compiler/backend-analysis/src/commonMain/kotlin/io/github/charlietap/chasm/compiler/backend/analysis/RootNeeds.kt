package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.CirFunction

class RootNeeds internal constructor(
    private val neededOnEntryBits: LongArray,
    private val neededAfterBits: LongArray,
    private val wordsPerNode: Int,
    internal val positions: Int,
) {
    internal fun neededOnEntry(root: Int): Boolean {
        return neededOnEntryBits.hasBit(root)
    }

    internal fun neededAfter(
        position: Int,
        root: Int,
    ): Boolean {
        return neededAfterBits.hasBit(position * wordsPerNode * Long.SIZE_BITS + root)
    }

    internal fun isEmpty(): Boolean {
        return neededOnEntryBits.all { word -> word == 0L } && neededAfterBits.all { word -> word == 0L }
    }
}

internal fun analyzeRootNeeds(
    table: InstructionTable,
    graph: FlowGraph,
    function: CirFunction,
    mayCollect: BooleanArray,
    rootIndex: IntArray,
    isReference: BooleanArray,
    rootCount: Int,
): RootNeeds? {

    val instructions = function.instructions
    val positions = instructions.size
    if (positions == 0 || rootCount == 0) {
        return null
    }

    val wordsPerNode = (rootCount + 63) / 64
    if (graph.nodes.toLong() * wordsPerNode * BITSETS_PER_NODE + graph.sizeInWords > MAX_ANALYSIS_WORDS) {
        return null
    }

    val bitsPerNode = wordsPerNode * Long.SIZE_BITS
    val gen = LongArray(positions * wordsPerNode)
    val kills = LongArray(positions * wordsPerNode)
    val allRoots = LongArray(wordsPerNode).also { bits -> for (root in 0 until rootCount) bits.setBit(root) }
    val slotReads = table.reads
    val writes = table.writes

    for (position in 0 until positions) {

        for (index in slotReads.start[position] until slotReads.end(position)) {
            val slot = slotReads.slots[index]
            if (slot in isReference.indices && isReference[slot]) {
                gen.setBit(position * bitsPerNode + rootIndex[slot])
            }
        }
        if (slotReads.unknown?.get(position) == true) {
            allRoots.copyInto(gen, position * wordsPerNode)
        }

        val mode = writes.modes[position]
        if (mode == SlotWriteTable.CONDITIONAL || mode == SlotWriteTable.SELECT) {
            continue
        }

        for (index in writes.start[position] until writes.end(position)) {
            val slot = writes.slots[index]
            if (writes.sources[index] != slot && slot in isReference.indices && isReference[slot]) {
                kills.setBit(position * bitsPerNode + rootIndex[slot])
            }
        }
    }

    val liveIn = graph.solve(gen, kills, wordsPerNode)
    for (position in 0 until positions) {
        val offset = position * wordsPerNode
        if (instructions[position].mayCollectWhileFrameLive(mayCollect)) {
            liveIn.copyInto(gen, offset, offset, offset + wordsPerNode)
        } else {
            gen.fill(0L, offset, offset + wordsPerNode)
        }
    }

    val neededIn = graph.solve(gen, kills, wordsPerNode)
    val neededAfter = LongArray(positions * wordsPerNode)
    for (position in 0 until positions) {
        graph.successors.forEachReached(position) { successor ->
            neededIn.orInto(neededAfter, wordsPerNode, successor * wordsPerNode, position * wordsPerNode)
        }
    }

    return RootNeeds(neededIn.copyOf(wordsPerNode), neededAfter, wordsPerNode, positions)
}

private fun FlowGraph.solve(
    gen: LongArray,
    kills: LongArray,
    wordsPerNode: Int,
): LongArray {

    val solution = LongArray(nodes * wordsPerNode)
    val next = LongArray(wordsPerNode)
    val pending = IntArray(nodes) { node -> node }
    val queued = BooleanArray(nodes) { true }
    var pendingCount = nodes

    while (pendingCount > 0) {

        val node = pending[--pendingCount]
        queued[node] = false
        next.fill(0L)
        val offset = node * wordsPerNode

        if (node < positions) {
            successors.forEachReached(node) { successor ->
                val from = successor * wordsPerNode
                if (successor < positions) {
                    for (word in 0 until wordsPerNode) next[word] = next[word] or (solution[from + word] and kills[offset + word].inv())
                } else {
                    solution.orInto(next, wordsPerNode, from)
                }
            }
            gen.orInto(next, wordsPerNode, offset)
        } else {
            successors.forEachReached(node) { successor -> solution.orInto(next, wordsPerNode, successor * wordsPerNode) }
        }

        if (next.contentEqualsRange(solution, offset)) {
            continue
        }
        next.copyInto(solution, offset)

        predecessors.forEachReached(node) { predecessor ->
            if (!queued[predecessor]) {
                queued[predecessor] = true
                pending[pendingCount++] = predecessor
            }
        }
    }

    return solution
}

private fun LongArray.contentEqualsRange(
    other: LongArray,
    offset: Int,
): Boolean {
    for (word in indices) {
        if (this[word] != other[offset + word]) {
            return false
        }
    }
    return true
}

private const val BITSETS_PER_NODE = 5

private const val MAX_ANALYSIS_WORDS = 1L shl 22
