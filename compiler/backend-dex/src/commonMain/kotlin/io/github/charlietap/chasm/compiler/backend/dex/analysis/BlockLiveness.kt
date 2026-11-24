package io.github.charlietap.chasm.compiler.backend.dex.analysis

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.compiler.backend.analysis.BasicBlocks
import io.github.charlietap.chasm.compiler.backend.analysis.InstructionTable
import io.github.charlietap.chasm.compiler.backend.analysis.SlotWriteTable
import io.github.charlietap.chasm.compiler.backend.analysis.basicBlocks
import io.github.charlietap.chasm.compiler.backend.analysis.clearBit
import io.github.charlietap.chasm.compiler.backend.analysis.hasBit
import io.github.charlietap.chasm.compiler.backend.analysis.orInto
import io.github.charlietap.chasm.compiler.backend.analysis.setBit

internal class LivenessGraph(
    private val slots: Int,
    internal val blocks: BasicBlocks,
    internal val successorStart: IntArray,
    internal val successors: IntArray,
    internal val predecessorStart: IntArray,
    internal val predecessors: IntArray,
    private val modes: ByteArray,
    private val writeOffsets: IntArray,
    private val writeSlots: IntArray,
    private val readOffsets: IntArray,
    private val readSlots: IntArray,
    private val unknownReads: BooleanArray?,
) {
    val words = (slots + 63) / 64

    fun solve(
        start: Int,
        end: Int,
        exit: LongArray,
    ): BlockLiveness {
        return BlockLiveness(this, start, maxOf(start, end), exit)
    }

    internal fun transfer(
        position: Int,
        bits: LongArray,
    ) {

        if (modes[position] != SlotWriteTable.CONDITIONAL) {
            for (index in writeOffsets[position] until writeOffsets[position + 1]) bits.clearBit(writeSlots[index])
        }

        for (index in readOffsets[position] until readOffsets[position + 1]) bits.setBit(readSlots[index])
        if (unknownReads?.get(position) == true) {
            readEverything(bits)
        }
    }

    internal fun transferCounting(
        position: Int,
        bits: LongArray,
        count: Int,
    ): Int {

        var live = count
        if (modes[position] != SlotWriteTable.CONDITIONAL) {
            for (index in writeOffsets[position] until writeOffsets[position + 1]) {
                val slot = writeSlots[index]
                if (bits.hasBit(slot)) {
                    bits.clearBit(slot)
                    live--
                }
            }
        }

        for (index in readOffsets[position] until readOffsets[position + 1]) {
            val slot = readSlots[index]
            if (!bits.hasBit(slot)) {
                bits.setBit(slot)
                live++
            }
        }

        if (unknownReads?.get(position) != true) {
            return live
        }

        readEverything(bits)

        return bits.sumOf { word -> word.countOneBits() }
    }

    private fun readEverything(bits: LongArray) {
        for (word in 0 until slots / 64) bits[word] = -1L
        if (slots % 64 != 0) {
            bits[slots / 64] = bits[slots / 64] or ((1L shl (slots % 64)) - 1)
        }
    }
}

internal class BlockLiveness(
    private val graph: LivenessGraph,
    private val start: Int,
    private val end: Int,
    private val exit: LongArray,
) {
    private val blocks = graph.blocks
    private val words = graph.words
    private val first = if (start < end) blocks.blockAt[start] else 0
    private val liveIn = LongArray(if (start < end) (blocks.blockAt[end - 1] - first + 1) * words else 0)
    private val counts: IntArray by lazy(LazyThreadSafetyMode.NONE) { liveCounts() }

    init {
        if (start < end) {
            solve()
        }
    }

    fun liveCount(position: Int): Int {
        return if (position < start || position >= end) 0 else counts[position - start]
    }

    fun addLiveIn(
        position: Int,
        into: LongArray,
    ) {

        val block = blocks.blockAt[position]
        if (position == maxOf(blocks.leaders[block], start)) {
            liveIn.orInto(into, words, (block - first) * words)
            return
        }

        val bits = LongArray(words)
        liveOut(block, bits)
        for (index in blockEnd(block) - 1 downTo position) graph.transfer(index, bits)

        bits.orInto(into, words)
    }

    private fun solve() {

        val last = blocks.blockAt[end - 1]
        val count = last - first + 1
        val scratch = LongArray(words)
        val pending = IntArray(count)
        val queued = BooleanArray(count)
        var top = 0
        for (block in first..last) {
            pending[top++] = block
            queued[block - first] = true
        }

        while (top > 0) {

            val block = pending[--top]
            queued[block - first] = false
            liveOut(block, scratch)
            for (position in blockEnd(block) - 1 downTo maxOf(blocks.leaders[block], start)) graph.transfer(position, scratch)

            val offset = (block - first) * words
            var changed = false
            for (word in 0 until words) {
                if (scratch[word] != liveIn[offset + word]) {
                    liveIn[offset + word] = scratch[word]
                    changed = true
                }
            }
            if (!changed) {
                continue
            }

            for (edge in graph.predecessorStart[block] until graph.predecessorStart[block + 1]) {
                val predecessor = graph.predecessors[edge]
                if (predecessor < first || predecessor > last || queued[predecessor - first]) {
                    continue
                }
                queued[predecessor - first] = true
                pending[top++] = predecessor
            }
        }
    }

    private fun blockEnd(block: Int): Int {
        return minOf(blocks.end(block), end)
    }

    private fun liveOut(
        block: Int,
        into: LongArray,
    ) {

        if (end < blocks.end(block)) {
            exit.copyInto(into)
            return
        }

        into.fill(0L)
        val last = blocks.blockAt[end - 1]
        for (edge in graph.successorStart[block] until graph.successorStart[block + 1]) {
            val successor = graph.successors[edge]
            if (successor < first || successor > last || blocks.leaders[successor] < start) {
                exit.orInto(into, words)
            } else {
                liveIn.orInto(into, words, (successor - first) * words)
            }
        }
    }

    private fun liveCounts(): IntArray {

        val counts = IntArray(end - start)
        val bits = LongArray(words)
        for (block in first..blocks.blockAt[end - 1]) {

            liveOut(block, bits)
            var count = bits.sumOf { word -> word.countOneBits() }

            for (position in blockEnd(block) - 1 downTo maxOf(blocks.leaders[block], start)) {
                count = graph.transferCounting(position, bits, count)
                counts[position - start] = count
            }
        }

        return counts
    }
}

internal fun livenessGraph(
    table: InstructionTable,
    function: CirFunction,
    slots: Int,
): LivenessGraph {

    val instructions = function.instructions
    val size = instructions.size
    val jumps = table.jumps
    val isLeader = BooleanArray(size + 1)
    isLeader[0] = true
    for (edge in 0 until jumps.count) isLeader[jumps.targets[edge]] = true
    function.exceptions?.regions?.forEach { region -> region.catches.forEach { handler -> isLeader[handler.target.index] = true } }
    for (position in 0 until size) {
        val jumping = jumps.end(position) > jumps.start[position]
        if (position + 1 < size && (!jumps.fallsThrough[position] || jumping)) {
            isLeader[position + 1] = true
        }
    }

    var length = 0
    for (position in 0 until size) {
        if (length == MAX_BLOCK_INSTRUCTIONS) {
            isLeader[position] = true
        }
        if (isLeader[position]) {
            length = 0
        }
        length++
    }

    val blocks = basicBlocks(isLeader, size)
    val leaders = blocks.leaders
    val blockAt = blocks.blockAt
    val count = leaders.size
    val successorStart = IntArray(count + 1)
    var successors = IntArray(count * 2)
    var edges = 0
    for (current in 0 until count) {

        val last = blocks.end(current) - 1
        val instruction = instructions[last]
        val targets = jumps.end(last) - jumps.start[last]
        if (edges + targets + 2 > successors.size) {
            successors = successors.copyOf(maxOf(successors.size * 2, edges + targets + 2))
        }

        if (jumps.fallsThrough[last]) {
            successors[edges++] = if (last + 1 < size) blockAt[last + 1] else EXIT
        }
        for (edge in jumps.start[last] until jumps.end(last)) {
            val target = jumps.targets[edge]
            successors[edges++] = if (target < size) blockAt[target] else EXIT
        }
        if (instruction is AdminInstruction.JumpTableS) {
            successors[edges++] = EXIT
        }
        successorStart[current + 1] = edges
    }

    val predecessorStart = IntArray(count + 1)
    for (edge in 0 until edges) {
        if (successors[edge] != EXIT) {
            predecessorStart[successors[edge] + 1]++
        }
    }
    for (current in 0 until count) predecessorStart[current + 1] += predecessorStart[current]
    val predecessors = IntArray(predecessorStart[count])
    val next = predecessorStart.copyOf(count)
    for (current in 0 until count) {
        for (edge in successorStart[current] until successorStart[current + 1]) {
            val successor = successors[edge]
            if (successor != EXIT) {
                predecessors[next[successor]++] = current
            }
        }
    }

    return LivenessGraph(
        slots = slots,
        blocks = blocks,
        successorStart = successorStart,
        successors = successors,
        predecessorStart = predecessorStart,
        predecessors = predecessors,
        modes = table.writes.modes,
        writeOffsets = table.writes.start,
        writeSlots = table.writes.slots,
        readOffsets = table.reads.start,
        readSlots = table.reads.slots,
        unknownReads = table.reads.unknown,
    )
}

private const val EXIT = -1

private const val MAX_BLOCK_INSTRUCTIONS = 128
