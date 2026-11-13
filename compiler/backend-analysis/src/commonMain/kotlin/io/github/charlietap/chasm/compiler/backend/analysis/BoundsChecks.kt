package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.CirFunction

fun CirFunction.redundantBoundsChecks(table: InstructionTable): BooleanArray {

    val size = instructions.size
    val redundant = BooleanArray(size)
    val accesses = table.accesses
    var checked = 0
    for (index in 0 until accesses.count) {
        if (accesses.memories[index] == 0 && accesses.addresses[index] != MemoryAccessTable.CONSTANT_ADDRESS) {
            checked++
        }
    }
    if (checked == 0) {
        return redundant
    }

    val checkIndex = BoundsCheckIndex(this, table, checked)
    val loops = if (checkIndex.backJumps.size > 0) LoopWrites(checkIndex) else null
    val handlers = checkIndex.handlers
    val pending = ForwardBranchProofs(checkIndex.trackedCount, size)
    var current = ProvenBounds(checkIndex.trackedCount)
    var reachable = true
    val jumps = checkIndex.forwardJumps
    var access = 0
    var loop = 0
    var handler = 0
    var jump = 0

    for (position in 0 until size) {

        val atHandler = handler < handlers.size && handlers[handler] == position
        if (atHandler) {
            handler++
        }
        current = pending.arrive(position, current, reachable, atHandler)

        if (loops != null && loop < loops.count && loops.headers[loop] == position) {
            val end = loops.ends[loop]
            val entered = pending.hasPendingTargetBy(end) || (handler < handlers.size && handlers[handler] <= end)
            if (entered || loops.partlyOverlapped[loop]) {
                current.clear()
            } else {
                loops.forgetWrittenSlots(position, end, current)
            }
            loop++
        }

        if (access < checkIndex.accesses && checkIndex.accessPositions[access] == position) {
            val slot = checkIndex.accessSlots[access]
            val endOffset = checkIndex.accessEndOffsets[access]
            access++
            if (current.has(slot) && endOffset <= current.provenEnd[slot]) {
                redundant[position] = true
            } else {
                current.prove(slot, endOffset)
            }
        }

        checkIndex.apply(position, current)
        if (jump < jumps.size && high(jumps[jump]) == position) {
            val empty = current.isEmpty()
            while (jump < jumps.size && high(jumps[jump]) == position) pending.branch(low(jumps[jump++]), current, empty)
        }

        reachable = instructions[position].fallsThrough()
    }

    return redundant
}

private class ProvenBounds(
    trackedCount: Int,
) {
    val provenEnd = LongArray(trackedCount)
    val proven = LongArray((trackedCount + 63) ushr 6)

    fun has(slot: Int): Boolean {
        return proven.hasBit(slot)
    }

    fun prove(
        slot: Int,
        endOffset: Long,
    ) {
        provenEnd[slot] = endOffset
        proven.setBit(slot)
    }

    fun kill(slot: Int) {
        proven.clearBit(slot)
    }

    fun clear() {
        proven.fill(0L)
    }

    fun count(): Int {
        var proofs = 0
        for (word in proven) proofs += word.countOneBits()
        return proofs
    }

    fun isEmpty(): Boolean {
        for (word in proven) {
            if (word != 0L) {
                return false
            }
        }
        return true
    }

    fun copyFrom(other: ProvenBounds) {
        for (word in proven.indices) {
            var bits = other.proven[word]
            proven[word] = bits
            while (bits != 0L) {
                val slot = (word shl 6) + bits.countTrailingZeroBits()
                provenEnd[slot] = other.provenEnd[slot]
                bits = bits and (bits - 1)
            }
        }
    }

    fun meet(other: ProvenBounds) {
        for (word in proven.indices) {
            var bits = proven[word] and other.proven[word]
            proven[word] = bits
            while (bits != 0L) {
                val slot = (word shl 6) + bits.countTrailingZeroBits()
                if (other.provenEnd[slot] < provenEnd[slot]) {
                    provenEnd[slot] = other.provenEnd[slot]
                }
                bits = bits and (bits - 1)
            }
        }
    }

    inline fun forEachSlot(action: (slot: Int) -> Unit) {
        for (word in proven.indices) {
            var bits = proven[word]
            while (bits != 0L) {
                action((word shl 6) + bits.countTrailingZeroBits())
                bits = bits and (bits - 1)
            }
        }
    }
}

private class ForwardBranchProofs(
    private val trackedCount: Int,
    size: Int,
) {
    private val empty = ProvenBounds(0)
    private val states = arrayOfNulls<ProvenBounds>(size + 1)
    private var targets = IntArray(8)
    private var count = 0
    private val spare = ArrayList<ProvenBounds>()

    fun arrive(
        position: Int,
        current: ProvenBounds,
        reachable: Boolean,
        atHandler: Boolean,
    ): ProvenBounds {

        val carried = take(position)

        when {
            atHandler -> {
                current.clear()
                if (carried != null) {
                    recycle(carried)
                }
            }
            carried == null -> {
                if (!reachable) {
                    current.clear()
                }
            }
            carried === empty -> current.clear()
            reachable -> {
                current.meet(carried)
                recycle(carried)
            }
            else -> {
                recycle(current)
                return carried
            }
        }

        return current
    }

    fun hasPendingTargetBy(position: Int): Boolean {
        return count > 0 && targets[0] <= position
    }

    fun branch(
        target: Int,
        current: ProvenBounds,
        currentEmpty: Boolean,
    ) {
        val state = states[target]
        when {
            state == null -> {
                states[target] = if (currentEmpty) {
                    empty
                } else {
                    obtain().also { fresh -> fresh.copyFrom(current) }
                }
                push(target)
            }
            state === empty -> Unit
            currentEmpty -> {
                spare.add(state)
                states[target] = empty
            }
            else -> state.meet(current)
        }
    }

    private fun take(position: Int): ProvenBounds? {
        val state = states[position] ?: return null
        states[position] = null
        popNearest()
        return state
    }

    private fun recycle(state: ProvenBounds) {
        if (state !== empty) {
            spare.add(state)
        }
    }

    private fun obtain(): ProvenBounds {
        return if (spare.isEmpty()) {
            ProvenBounds(trackedCount)
        } else {
            spare.removeAt(spare.lastIndex)
        }
    }

    private fun push(target: Int) {

        if (count == targets.size) {
            targets = targets.copyOf(count * 2)
        }

        var index = count++
        while (index > 0) {
            val parent = (index - 1) ushr 1
            if (targets[parent] <= target) {
                break
            }
            targets[index] = targets[parent]
            index = parent
        }
        targets[index] = target
    }

    private fun popNearest() {
        val last = targets[--count]
        var index = 0
        while (true) {
            var child = 2 * index + 1
            if (child >= count) {
                break
            }
            if (child + 1 < count && targets[child + 1] < targets[child]) {
                child++
            }
            if (targets[child] >= last) {
                break
            }
            targets[index] = targets[child]
            index = child
        }
        targets[index] = last
    }
}

private fun pack(
    high: Int,
    low: Int,
): Long {
    return (high.toLong() shl 32) or low.toLong()
}

private fun high(packed: Long): Int {
    return (packed ushr 32).toInt()
}

private fun low(packed: Long): Int {
    return packed.toInt()
}

private class BoundsCheckIndex(
    function: CirFunction,
    table: InstructionTable,
    checked: Int,
) {
    val size = function.instructions.size
    val writeOffsets = IntArray(size + 1)
    val writtenSlots: IntArray
    val writeCount: Int
    val forwardJumps = LongList()
    val backJumps = LongList()
    val handlers = handlerEntries(function)
    val accessPositions = IntArray(checked)
    val accessSlots = IntArray(checked)
    val accessEndOffsets = LongArray(checked)
    val accesses = checked
    val trackedCount: Int
    private val proofCopies = LongList()
    private val trackedIndex: IntArray
    private var proofCopy = 0

    init {

        collectAccesses(table.accesses)
        collectJumpsAndProofCopies(table.jumps, table.writes)

        val writes = table.writes
        var highest = -1
        for (index in 0 until writes.count) highest = maxOf(highest, writes.slots[index])
        for (index in 0 until accesses) highest = maxOf(highest, accessSlots[index])
        trackedIndex = IntArray(highest + 1) { -1 }
        var nextTracked = 0
        for (index in 0 until accesses) {
            val slot = accessSlots[index]
            if (trackedIndex[slot] < 0) {
                trackedIndex[slot] = nextTracked++
            }
            accessSlots[index] = trackedIndex[slot]
        }
        for (index in 0 until proofCopies.size) {
            val destination = writes.slots[writes.start[high(proofCopies[index])]]
            if (trackedIndex[destination] < 0) {
                trackedIndex[destination] = nextTracked++
            }
        }
        trackedCount = nextTracked

        var kept = 0
        for (index in 0 until writes.count) {
            if (trackedIndex[writes.slots[index]] >= 0) {
                kept++
            }
        }
        writtenSlots = IntArray(kept)
        kept = 0
        for (position in 0 until size) {
            writeOffsets[position] = kept
            for (index in writes.start[position] until writes.end(position)) {
                val slot = trackedIndex[writes.slots[index]]
                if (slot >= 0) {
                    writtenSlots[kept++] = slot
                }
            }
        }
        writeOffsets[size] = kept
        writeCount = kept
    }

    fun apply(
        position: Int,
        proofs: ProvenBounds,
    ) {

        var source = -1
        if (proofCopy < proofCopies.size && high(proofCopies[proofCopy]) == position) {
            val slot = low(proofCopies[proofCopy++])
            if (slot < trackedIndex.size) {
                source = trackedIndex[slot]
            }
        }

        val start = writeOffsets[position]
        val end = writeOffsets[position + 1]
        if (source >= 0 && proofs.has(source)) {
            proofs.prove(writtenSlots[start], proofs.provenEnd[source])
        } else {
            for (index in start until end) proofs.kill(writtenSlots[index])
        }
    }

    private fun collectAccesses(table: MemoryAccessTable) {
        var access = 0
        for (index in 0 until table.count) {
            if (table.memories[index] != 0 || table.addresses[index] == MemoryAccessTable.CONSTANT_ADDRESS) {
                continue
            }
            accessPositions[access] = table.positions[index]
            accessSlots[access] = table.addresses[index]
            accessEndOffsets[access] = table.endOffsets[index]
            access++
        }
    }

    private fun collectJumpsAndProofCopies(
        jumps: JumpTable,
        writes: SlotWriteTable,
    ) {

        for (position in 0 until size) {

            for (edge in jumps.start[position] until jumps.end(position)) {
                val target = jumps.targets[edge]
                if (target <= position) {
                    backJumps.add(pack(target, position))
                } else {
                    forwardJumps.add(pack(position, target))
                }
            }

            val write = writes.start[position]
            val jumpFree = jumps.end(position) == jumps.start[position]
            if (jumpFree && writes.end(position) - write == 1 && writes.sources[write] != SlotWriteTable.NOT_A_COPY) {
                proofCopies.add(pack(position, writes.sources[write]))
            }
        }

        backJumps.sort()
    }
}

private fun handlerEntries(function: CirFunction): IntArray {

    val regions = function.exceptions?.regions ?: return IntArray(0)
    var count = 0
    for (region in regions) count += region.catches.size

    val entries = IntArray(count)
    count = 0
    for (region in regions) for (handler in region.catches) entries[count++] = handler.target.index
    entries.sort()

    var distinct = 0
    for (index in entries.indices) {
        if (index == 0 || entries[index] != entries[index - 1]) {
            entries[distinct++] = entries[index]
        }
    }
    return entries.copyOf(distinct)
}

private class LoopWrites(
    private val checkIndex: BoundsCheckIndex,
) {
    val headers: IntArray
    val ends: IntArray
    val count: Int
    val partlyOverlapped: BooleanArray
    private var slotWriteOffsets = IntArray(0)
    private var slotWritePositions = IntArray(0)

    init {

        val jumps = checkIndex.backJumps
        var loops = 0
        for (index in 0 until jumps.size) {
            if (index + 1 == jumps.size || high(jumps[index + 1]) != high(jumps[index])) {
                loops++
            }
        }

        headers = IntArray(loops)
        ends = IntArray(loops)
        count = loops
        loops = 0
        for (index in 0 until jumps.size) {
            val packed = jumps[index]
            if (index + 1 == jumps.size || high(jumps[index + 1]) != high(packed)) {
                headers[loops] = high(packed)
                ends[loops++] = low(packed)
            }
        }

        partlyOverlapped = partlyOverlappedLoops(headers, ends)
    }

    fun forgetWrittenSlots(
        header: Int,
        end: Int,
        proofs: ProvenBounds,
    ) {

        val start = checkIndex.writeOffsets[header]
        val stop = checkIndex.writeOffsets[end + 1]
        if (start == stop) {
            return
        }

        val written = stop - start
        if (written <= proofs.proven.size || written <= proofs.count()) {
            for (index in start until stop) proofs.kill(checkIndex.writtenSlots[index])
        } else {
            if (slotWriteOffsets.isEmpty()) {
                indexWritesBySlot()
            }
            proofs.forEachSlot { slot ->
                if (writtenWithin(slot, header, end)) {
                    proofs.kill(slot)
                }
            }
        }
    }

    private fun writtenWithin(
        slot: Int,
        header: Int,
        end: Int,
    ): Boolean {

        var low = slotWriteOffsets[slot]
        val high = slotWriteOffsets[slot + 1]
        var bound = high
        while (low < bound) {
            val middle = (low + bound) ushr 1
            if (slotWritePositions[middle] < header) {
                low = middle + 1
            } else {
                bound = middle
            }
        }

        return low < high && slotWritePositions[low] <= end
    }

    private fun indexWritesBySlot() {

        slotWriteOffsets = IntArray(checkIndex.trackedCount + 1)
        slotWritePositions = IntArray(checkIndex.writeCount)
        val writtenSlots = checkIndex.writtenSlots

        for (index in 0 until checkIndex.writeCount) slotWriteOffsets[writtenSlots[index] + 1]++
        for (slot in 1..checkIndex.trackedCount) slotWriteOffsets[slot] += slotWriteOffsets[slot - 1]

        for (position in 0 until checkIndex.size) {
            for (index in checkIndex.writeOffsets[position] until checkIndex.writeOffsets[position + 1]) {
                slotWritePositions[slotWriteOffsets[writtenSlots[index]]++] = position
            }
        }

        for (slot in checkIndex.trackedCount downTo 1) slotWriteOffsets[slot] = slotWriteOffsets[slot - 1]
        slotWriteOffsets[0] = 0
    }
}

private fun partlyOverlappedLoops(
    headers: IntArray,
    ends: IntArray,
): BooleanArray {

    val overlapped = BooleanArray(headers.size)
    val stack = IntArray(headers.size)
    var depth = 0

    for (loop in headers.size - 1 downTo 0) {
        while (depth > 0 && ends[stack[depth - 1]] <= ends[loop]) depth--
        overlapped[loop] = depth > 0 && headers[stack[depth - 1]] <= ends[loop]
        stack[depth++] = loop
    }

    return overlapped
}
