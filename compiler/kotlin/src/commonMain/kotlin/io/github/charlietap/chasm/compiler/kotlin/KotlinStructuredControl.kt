package io.github.charlietap.chasm.compiler.kotlin

/** A linear loop has one entry and only forward, back-to-header or exit edges. */
internal data class KotlinStructuredLoop(val blocks: List<KotlinBlock>) {
    val start: Int get() = blocks.first().startOffset
    val end: Int get() = blocks.last().endOffset
}

internal data class KotlinStructuredNode(val blocks: List<KotlinBlock>, val loop: KotlinStructuredLoop? = null) {
    val start: Int get() = blocks.first().startOffset
}

internal data class KotlinStructuredPlan(
    val nodes: List<KotlinStructuredNode>,
    val linearExit: Int?,
) {
    val loops: List<KotlinStructuredLoop> get() = nodes.mapNotNull { it.loop }
    val entryOffsets: List<Int> get() = if (linearExit != null) listOf(nodes.first().start) else nodes.map { it.start }
}

internal fun structuredControl(
    blocks: List<KotlinBlock>,
    branches: List<KotlinBranch?>,
    firstIp: Int,
    predecessors: Map<Int, List<Int>>,
    forcedEntries: Set<Int>,
): KotlinStructuredPlan {
    val starts = blocks.mapIndexed { index, block -> block.startOffset to index }.toMap()

    fun successors(block: KotlinBlock): Set<Int> {
        val branch = branches[block.endOffset - 1] ?: return setOf(block.endOffset)
        val targets = branch.targets.mapTo(mutableSetOf()) { it - firstIp }
        if (branch.condition != null && !branch.table) targets.add(block.endOffset)
        return targets
    }

    val candidates = blocks.mapIndexedNotNull { endIndex, latch ->
        val lastBranch = branches[latch.endOffset - 1] ?: return@mapIndexedNotNull null
        if (lastBranch.table) return@mapIndexedNotNull null
        val header = lastBranch.targets.singleOrNull()?.minus(firstIp) ?: return@mapIndexedNotNull null
        val startIndex = starts[header] ?: return@mapIndexedNotNull null
        if (startIndex > endIndex) return@mapIndexedNotNull null
        val body = blocks.subList(startIndex, endIndex + 1)
        if (body.zipWithNext().any { (left, right) -> left.endOffset != right.startOffset }) return@mapIndexedNotNull null
        val range = header until latch.endOffset
        if (body.drop(1).any { block ->
                block.startOffset in forcedEntries || predecessors[block.startOffset].orEmpty().any { it !in range }
            }
        ) {
            return@mapIndexedNotNull null
        }
        if (body.any { block ->
                val branch = branches[block.endOffset - 1]
                branch?.table == true || successors(block).any { it in range && it != header && it != block.endOffset } ||
                    (block != latch && branch?.condition == null && branch != null && header in successors(block))
            }
        ) {
            return@mapIndexedNotNull null
        }
        KotlinStructuredLoop(body)
    }.sortedBy { it.end - it.start }
    val loops = mutableListOf<KotlinStructuredLoop>()
    candidates.forEach { loop ->
        if (loops.none { it.start < loop.end && loop.start < it.end }) loops.add(loop)
    }
    val headers = loops.associateBy { it.start }
    val nodes = blocks.mapNotNull { block ->
        val loop = headers[block.startOffset]
        when {
            loop != null -> KotlinStructuredNode(loop.blocks, loop)
            loops.any { block.startOffset > it.start && block.startOffset < it.end } -> null
            else -> KotlinStructuredNode(listOf(block))
        }
    }
    val offsets = blocks.flatMapTo(mutableSetOf()) { (it.startOffset until it.endOffset).toList() }
    val singleEntry = blocks.drop(1).none { block ->
        block.startOffset in forcedEntries || block.startOffset - 1 !in offsets ||
            predecessors[block.startOffset].orEmpty().any { it !in offsets }
    }
    val exits = nodes.map { node ->
        val internal = node.loop?.let { it.start until it.end }
        node.blocks.flatMapTo(mutableSetOf()) { successors(it) }.filterTo(mutableSetOf()) { internal == null || it !in internal }
    }
    val linear = singleEntry && blocks.none { branches[it.endOffset - 1]?.table == true } &&
        (0 until nodes.lastIndex).all { exits[it] == setOf(nodes[it + 1].start) } &&
        exits.last().size == 1 && exits.last().single() !in offsets
    return KotlinStructuredPlan(nodes, exits.last().singleOrNull().takeIf { linear })
}
