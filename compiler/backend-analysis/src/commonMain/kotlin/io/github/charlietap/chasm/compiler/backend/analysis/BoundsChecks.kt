package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.CirFunction

fun CirFunction.redundantBoundsChecks(table: InstructionTable): BooleanArray {

    val size = instructions.size
    val redundant = BooleanArray(size)
    val accesses = table.accesses
    var checked = 0
    for (index in 0 until accesses.count) {
        if (accesses.memories[index] == 0 && accesses.addresses[index] != MemoryAccessTable.IMMEDIATE) {
            checked++
        }
    }
    if (checked == 0) {
        return redundant
    }

    val effects = CheckEffects(this, table, checked)
    val loops = if (effects.backJumps.size > 0) LoopSurvival(effects) else null
    val handlers = effects.handlers
    val pending = PendingReach(effects.tracked, size)
    var current = CheckedReach(effects.tracked)
    var reachable = true
    val jumps = effects.forwardJumps
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
            val entered = pending.awaitsBy(end) || (handler < handlers.size && handlers[handler] <= end)
            if (entered || loops.crossing[loop]) {
                current.clear()
            } else {
                loops.forgetWrites(position, end, current)
            }
            loop++
        }

        if (access < effects.accesses && effects.accessPositions[access] == position) {
            val slot = effects.accessSlots[access]
            val reach = effects.accessReach[access]
            access++
            if (current.has(slot) && reach <= current.reach[slot]) {
                redundant[position] = true
            } else {
                current.prove(slot, reach)
            }
        }

        effects.apply(position, current)
        if (jump < jumps.size && high(jumps[jump]) == position) {
            val empty = current.isEmpty()
            while (jump < jumps.size && high(jumps[jump]) == position) pending.branch(low(jumps[jump++]), current, empty)
        }

        reachable = instructions[position].fallsThrough()
    }

    return redundant
}

private class CheckedReach(
    tracked: Int,
) {
    val reach = LongArray(tracked)
    val valid = LongArray((tracked + 63) ushr 6)

    fun has(slot: Int): Boolean {
        return valid.hasBit(slot)
    }

    fun prove(
        slot: Int,
        reach: Long,
    ) {
        this.reach[slot] = reach
        valid.setBit(slot)
    }

    fun kill(slot: Int) {
        valid.clearBit(slot)
    }

    fun clear() {
        valid.fill(0L)
    }

    fun count(): Int {
        var facts = 0
        for (word in valid) facts += word.countOneBits()
        return facts
    }

    fun isEmpty(): Boolean {
        for (word in valid) {
            if (word != 0L) {
                return false
            }
        }
        return true
    }

    fun copyFrom(other: CheckedReach) {
        for (word in valid.indices) {
            var bits = other.valid[word]
            valid[word] = bits
            while (bits != 0L) {
                val slot = (word shl 6) + bits.countTrailingZeroBits()
                reach[slot] = other.reach[slot]
                bits = bits and (bits - 1)
            }
        }
    }

    fun meet(other: CheckedReach) {
        for (word in valid.indices) {
            var bits = valid[word] and other.valid[word]
            valid[word] = bits
            while (bits != 0L) {
                val slot = (word shl 6) + bits.countTrailingZeroBits()
                if (other.reach[slot] < reach[slot]) {
                    reach[slot] = other.reach[slot]
                }
                bits = bits and (bits - 1)
            }
        }
    }

    inline fun forEachSlot(action: (slot: Int) -> Unit) {
        for (word in valid.indices) {
            var bits = valid[word]
            while (bits != 0L) {
                action((word shl 6) + bits.countTrailingZeroBits())
                bits = bits and (bits - 1)
            }
        }
    }
}

private class PendingReach(
    private val tracked: Int,
    size: Int,
) {
    private val empty = CheckedReach(0)
    private val states = arrayOfNulls<CheckedReach>(size + 1)
    private var targets = IntArray(8)
    private var count = 0
    private val spare = ArrayList<CheckedReach>()

    fun arrive(
        position: Int,
        current: CheckedReach,
        reachable: Boolean,
        atHandler: Boolean,
    ): CheckedReach {

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

    fun awaitsBy(position: Int): Boolean {
        return count > 0 && targets[0] <= position
    }

    fun branch(
        target: Int,
        current: CheckedReach,
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

    private fun take(position: Int): CheckedReach? {
        val state = states[position] ?: return null
        states[position] = null
        popNearest()
        return state
    }

    private fun recycle(state: CheckedReach) {
        if (state !== empty) {
            spare.add(state)
        }
    }

    private fun obtain(): CheckedReach {
        return if (spare.isEmpty()) {
            CheckedReach(tracked)
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

private class CheckEffects(
    function: CirFunction,
    table: InstructionTable,
    checked: Int,
) {
    val size = function.instructions.size
    val first = IntArray(size + 1)
    val slots: IntArray
    val count: Int
    val forwardJumps = LongList()
    val backJumps = LongList()
    val handlers = handlerEntries(function)
    val accessPositions = IntArray(checked)
    val accessSlots = IntArray(checked)
    val accessReach = LongArray(checked)
    val accesses = checked
    val tracked: Int
    private val carries = LongList()
    private val dense: IntArray
    private var carry = 0

    init {

        collectAccesses(table.accesses)
        collectJumpsAndCarries(table.jumps, table.writes)

        val writes = table.writes
        var highest = -1
        for (index in 0 until writes.count) highest = maxOf(highest, writes.slots[index])
        for (index in 0 until accesses) highest = maxOf(highest, accessSlots[index])
        dense = IntArray(highest + 1) { -1 }
        var tracking = 0
        for (index in 0 until accesses) {
            val slot = accessSlots[index]
            if (dense[slot] < 0) {
                dense[slot] = tracking++
            }
            accessSlots[index] = dense[slot]
        }
        for (index in 0 until carries.size) {
            val destination = writes.slots[writes.start[high(carries[index])]]
            if (dense[destination] < 0) {
                dense[destination] = tracking++
            }
        }
        tracked = tracking

        var kept = 0
        for (index in 0 until writes.count) {
            if (dense[writes.slots[index]] >= 0) {
                kept++
            }
        }
        slots = IntArray(kept)
        kept = 0
        for (position in 0 until size) {
            first[position] = kept
            for (index in writes.start[position] until writes.end(position)) {
                val slot = dense[writes.slots[index]]
                if (slot >= 0) {
                    slots[kept++] = slot
                }
            }
        }
        first[size] = kept
        count = kept
    }

    fun apply(
        position: Int,
        facts: CheckedReach,
    ) {

        var source = -1
        if (carry < carries.size && high(carries[carry]) == position) {
            val slot = low(carries[carry++])
            if (slot < dense.size) {
                source = dense[slot]
            }
        }

        val start = first[position]
        val end = first[position + 1]
        if (source >= 0 && facts.has(source)) {
            facts.prove(slots[start], facts.reach[source])
        } else {
            for (index in start until end) facts.kill(slots[index])
        }
    }

    private fun collectAccesses(table: MemoryAccessTable) {
        var access = 0
        for (index in 0 until table.count) {
            if (table.memories[index] != 0 || table.addresses[index] == MemoryAccessTable.IMMEDIATE) {
                continue
            }
            accessPositions[access] = table.positions[index]
            accessSlots[access] = table.addresses[index]
            accessReach[access] = table.reach[index]
            access++
        }
    }

    private fun collectJumpsAndCarries(
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
            if (jumpFree && writes.end(position) - write == 1 && writes.sources[write] != SlotWriteTable.TYPED) {
                carries.add(pack(position, writes.sources[write]))
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

private class LoopSurvival(
    private val effects: CheckEffects,
) {
    val headers: IntArray
    val ends: IntArray
    val count: Int
    val crossing: BooleanArray
    private var writeFirst = IntArray(0)
    private var writePositions = IntArray(0)

    init {

        val jumps = effects.backJumps
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

        crossing = crossedLoops(headers, ends)
    }

    fun forgetWrites(
        header: Int,
        end: Int,
        facts: CheckedReach,
    ) {

        val start = effects.first[header]
        val stop = effects.first[end + 1]
        if (start == stop) {
            return
        }

        val written = stop - start
        if (written <= facts.valid.size || written <= facts.count()) {
            for (index in start until stop) facts.kill(effects.slots[index])
        } else {
            if (writeFirst.isEmpty()) {
                indexWrites()
            }
            facts.forEachSlot { slot ->
                if (writtenWithin(slot, header, end)) {
                    facts.kill(slot)
                }
            }
        }
    }

    private fun writtenWithin(
        slot: Int,
        header: Int,
        end: Int,
    ): Boolean {

        var low = writeFirst[slot]
        val high = writeFirst[slot + 1]
        var bound = high
        while (low < bound) {
            val middle = (low + bound) ushr 1
            if (writePositions[middle] < header) {
                low = middle + 1
            } else {
                bound = middle
            }
        }

        return low < high && writePositions[low] <= end
    }

    private fun indexWrites() {

        writeFirst = IntArray(effects.tracked + 1)
        writePositions = IntArray(effects.count)
        val slots = effects.slots

        for (index in 0 until effects.count) writeFirst[slots[index] + 1]++
        for (slot in 1..effects.tracked) writeFirst[slot] += writeFirst[slot - 1]

        for (position in 0 until effects.size) {
            for (index in effects.first[position] until effects.first[position + 1]) {
                writePositions[writeFirst[slots[index]]++] = position
            }
        }

        for (slot in effects.tracked downTo 1) writeFirst[slot] = writeFirst[slot - 1]
        writeFirst[0] = 0
    }
}

private fun crossedLoops(
    headers: IntArray,
    ends: IntArray,
): BooleanArray {

    val crossed = BooleanArray(headers.size)
    val stack = IntArray(headers.size)
    var depth = 0

    for (loop in headers.size - 1 downTo 0) {
        while (depth > 0 && ends[stack[depth - 1]] <= ends[loop]) depth--
        crossed[loop] = depth > 0 && headers[stack[depth - 1]] <= ends[loop]
        stack[depth++] = loop
    }

    return crossed
}
