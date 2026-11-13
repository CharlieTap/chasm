package io.github.charlietap.chasm.compiler.backend.dex.analysis

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.compiler.backend.analysis.InstructionTable
import io.github.charlietap.chasm.compiler.backend.analysis.SlotReadTable
import io.github.charlietap.chasm.compiler.backend.analysis.SlotWriteTable
import io.github.charlietap.chasm.compiler.backend.analysis.WriteKinds
import io.github.charlietap.chasm.compiler.backend.analysis.fallsThrough
import io.github.charlietap.chasm.type.ValueType

internal class SlotKindFlow(
    private val blocks: BasicBlocks,
    private val blockStates: Array<ByteArray?>,
    private val writes: SlotWriteTable,
    private val writeSlots: IntArray,
    private val writeKinds: ByteArray,
    private val modes: ByteArray,
    private val homes: ByteArray,
    private val entry: ByteArray,
    private val initial: ByteArray,
    private val tracked: IntArray,
    private val dense: IntArray,
) {
    private val pins = ByteArray(homes.size)

    fun tracks(slot: Int): Boolean {
        return dense[slot] >= 0
    }

    fun initialKinds(into: ByteArray) {
        initial.copyInto(into)
    }

    fun homeMask(slot: Int): Int {
        return homes[slot].toInt()
    }

    fun entryKind(slot: Int): Byte {
        return entry[slot]
    }

    fun pin(slot: Int): Byte {
        return pins[slot]
    }

    internal fun pinConflicts(reads: SlotReadTable) {

        val state = initial.copyOf()
        for (block in blocks.leaders.indices) {

            val start = blockStates[block] ?: continue
            if (start.none { kind -> kind >= CONFLICT_NARROW }) {
                continue
            }

            before(blocks.leaders[block], state)
            for (position in blocks.leaders[block] until blocks.end(block)) {
                for (index in reads.start[position] until reads.end(position)) pinSameWidth(reads.slots[index], state[reads.slots[index]])
                for (index in writes.start[position] until writes.end(position)) pinSameWidth(writeSlots[index], writeKinds[index])
                advance(position, state)
            }
        }
    }

    private fun pinSameWidth(
        slot: Int,
        kind: Byte,
    ) {

        if ((kind != CONFLICT_NARROW && kind != CONFLICT_WIDE) || pins[slot] != UNDEFINED) {
            return
        }

        val pinned = pinnedKind(homes[slot].toInt())
        pins[slot] = pinned
        homes[slot] = homeBit(pinned).toByte()
    }

    fun leader(position: Int): Boolean {
        return position >= blocks.blockAt.size || blocks.leaders[blocks.blockAt[position]] == position
    }

    fun reached(position: Int): Boolean {
        return position < blocks.blockAt.size && blockStates[blocks.blockAt[position]] != null
    }

    fun before(
        position: Int,
        state: ByteArray,
    ) {

        val start = if (position < blocks.blockAt.size) blockStates[blocks.blockAt[position]] else null
        if (start == null) {
            for (slot in tracked) state[slot] = UNDEFINED
            return
        }

        for (index in tracked.indices) state[tracked[index]] = start[index]
        for (earlier in blocks.leaders[blocks.blockAt[position]] until position) advance(earlier, state)
    }

    fun advance(
        position: Int,
        state: ByteArray,
    ) {

        if (modes[position] == SlotWriteTable.CONDITIONAL) {
            return
        }

        for (index in writes.start[position] until writes.end(position)) {
            if (dense[writeSlots[index]] >= 0) {
                state[writeSlots[index]] = writeKinds[index]
            }
        }
    }

    fun advanced(
        position: Int,
        slot: Int,
        state: ByteArray,
    ): Byte {

        if (modes[position] == SlotWriteTable.CONDITIONAL || dense[slot] < 0) {
            return state[slot]
        }

        val kind = written(position, slot)

        return if (kind == NONE) state[slot] else kind
    }

    fun written(
        position: Int,
        slot: Int,
    ): Byte {

        var kind = NONE
        for (index in writes.start[position] until writes.end(position)) {
            if (writeSlots[index] == slot) {
                kind = writeKinds[index]
            }
        }

        return kind
    }

    companion object {
        const val NONE: Byte = -1
        const val UNDEFINED: Byte = WriteKinds.COPIED
        const val LONG: Byte = WriteKinds.LONG
        const val INT: Byte = WriteKinds.INT
        const val FLOAT: Byte = WriteKinds.FLOAT
        const val DOUBLE: Byte = WriteKinds.DOUBLE
        const val ANY_NARROW: Byte = WriteKinds.NARROW_CONSTANT
        const val ANY_WIDE: Byte = WriteKinds.WIDE_CONSTANT
        const val CONFLICT_NARROW: Byte = 7
        const val CONFLICT_WIDE: Byte = 8
        const val CONFLICT: Byte = 9

        fun homeIndex(kind: Byte): Int {
            return kind - LONG
        }

        fun homeBit(kind: Byte): Int {
            return 1 shl homeIndex(kind)
        }

        fun pinnedKind(homes: Int): Byte {
            return if (homes and (homeBit(LONG) or homeBit(DOUBLE)) == 0) INT else LONG
        }

        fun definite(kind: Byte): Boolean {
            return kind in LONG..DOUBLE
        }

        fun wide(kind: Byte): Boolean {
            return kind == LONG || kind == DOUBLE
        }
    }
}

internal fun interface SlotKindAnalyzer {
    fun analyze(
        function: CirFunction,
        facts: FunctionFacts,
        initialised: BooleanArray,
    ): SlotKindFlow
}

internal class DataflowSlotKindAnalyzer : SlotKindAnalyzer {
    override fun analyze(
        function: CirFunction,
        facts: FunctionFacts,
        initialised: BooleanArray,
    ): SlotKindFlow {

        val table = facts.table
        val parameters = facts.type.params.types
        val frameSlots = facts.frameSlots
        val size = function.instructions.size
        val writes = table.writes

        val restarts = (0 until size).any { position -> table.restartsFunction(position) }
        val kinds = writes.kinds.copyOf(writes.count)
        val sources = if (restarts) writes.sources.copyOf(writes.count) else writes.sources
        if (restarts) {
            retypeRestarts(table, size, parameters, kinds, sources)
        }

        val blocks = basicBlocks(table, function)
        val entry = entryKinds(parameters, frameSlots, facts.initialValues, initialised)
        val fixed = fixedKinds(table, function, parameters, facts.localTypes, entry, kinds, sources)

        val dense = IntArray(frameSlots)
        var count = 0
        for (slot in 0 until frameSlots) dense[slot] = if (fixed[slot] == SlotKindFlow.CONFLICT) count++ else -1
        val tracked = IntArray(count)
        for (slot in 0 until frameSlots) {
            if (dense[slot] >= 0) {
                tracked[dense[slot]] = slot
            }
        }
        val initial = ByteArray(frameSlots) { slot -> if (SlotKindFlow.definite(fixed[slot])) fixed[slot] else entry[slot] }

        val dataflow = SlotKindDataflow(table, function, blocks, dense, count, kinds, sources, initial)
        dataflow.solve(entry, tracked)
        val states = dataflow.states
        val homes = homeKinds(table, function, blocks, states, parameters, facts.localTypes, entry, kinds)

        val conflicted = states.any { reached -> reached != null && reached.any { kind -> kind >= SlotKindFlow.CONFLICT_NARROW } }
        val flow = SlotKindFlow(
            blocks = blocks,
            blockStates = states,
            writes = writes,
            writeSlots = writes.slots,
            writeKinds = kinds,
            modes = writes.modes,
            homes = homes,
            entry = entry,
            initial = initial,
            tracked = tracked,
            dense = dense,
        )
        if (conflicted) {
            flow.pinConflicts(table.reads)
        }

        return flow
    }
}

private fun width(kind: Byte): Int {
    return when (kind) {
        SlotKindFlow.INT, SlotKindFlow.FLOAT, SlotKindFlow.CONFLICT_NARROW -> 1
        SlotKindFlow.LONG, SlotKindFlow.DOUBLE, SlotKindFlow.CONFLICT_WIDE -> 2
        SlotKindFlow.CONFLICT -> 3
        else -> 0
    }
}

private fun merge(
    first: Byte,
    second: Byte,
): Byte {

    if (first == second || second == SlotKindFlow.UNDEFINED) {
        return first
    }
    if (first == SlotKindFlow.UNDEFINED) {
        return second
    }

    if (first < SlotKindFlow.CONFLICT_NARROW && second < SlotKindFlow.CONFLICT_NARROW) {
        val firstAny = first >= SlotKindFlow.ANY_NARROW
        val secondAny = second >= SlotKindFlow.ANY_NARROW
        if (firstAny && secondAny) {
            return maxOf(first, second)
        }
        if (firstAny) {
            return second
        }
        if (secondAny) {
            return first
        }
    }

    return when (width(first) or width(second)) {
        1 -> SlotKindFlow.CONFLICT_NARROW
        2 -> SlotKindFlow.CONFLICT_WIDE
        else -> SlotKindFlow.CONFLICT
    }
}

private fun Instruction.alwaysJumps(): Boolean? {
    return when (this) {
        is AdminInstruction.JumpIfI -> operand != 0L
        is AdminInstruction.JumpIfZeroI -> operand == 0L
        is AdminInstruction.JumpIfCopyI -> operand != 0L
        else -> null
    }
}

private fun retypeRestarts(
    table: InstructionTable,
    size: Int,
    parameters: List<ValueType>,
    kinds: ByteArray,
    sources: IntArray,
) {

    val writes = table.writes

    for (position in 0 until size) {
        if (!table.restartsFunction(position)) {
            continue
        }
        for (index in writes.start[position] until writes.end(position)) {
            kinds[index] = WriteKinds.kindOf(parameters[writes.slots[index]])
            sources[index] = SlotWriteTable.NOT_A_COPY
        }
    }
}

private fun basicBlocks(
    table: InstructionTable,
    function: CirFunction,
): BasicBlocks {

    val instructions = function.instructions
    val size = instructions.size
    val jumps = table.jumps
    val isLeader = BooleanArray(size + 1)
    isLeader[0] = true
    for (position in 0 until size) {
        for (edge in jumps.start[position] until jumps.end(position)) isLeader[jumps.targets[edge]] = true
        if (jumps.end(position) > jumps.start[position] || !instructions[position].fallsThrough()) {
            isLeader[position + 1] = true
        }
    }
    function.exceptions?.regions.orEmpty().forEach { region ->
        isLeader[region.start.index] = true
        isLeader[region.end.index] = true
        region.catches.forEach { handler -> isLeader[handler.target.index] = true }
    }

    return basicBlocks(isLeader, size)
}

private fun entryKinds(
    parameters: List<ValueType>,
    frameSlots: Int,
    initialValues: LongArray,
    initialised: BooleanArray,
): ByteArray {

    val entry = ByteArray(frameSlots)
    parameters.forEachIndexed { index, type -> entry[index] = WriteKinds.kindOf(type) }
    for (slot in parameters.size until frameSlots) {
        if (initialised[slot]) {
            entry[slot] = WriteKinds.constantKind(initialValues[slot])
        }
    }

    return entry
}

private fun fixedKinds(
    table: InstructionTable,
    function: CirFunction,
    parameters: List<ValueType>,
    localTypes: Array<ValueType?>,
    entry: ByteArray,
    kinds: ByteArray,
    sources: IntArray,
): ByteArray {

    val frameSlots = entry.size
    val slots = table.writes.slots
    val writeCount = table.writes.count
    val fixed = ByteArray(frameSlots)
    val immediates = ByteArray(frameSlots)
    val written = BooleanArray(frameSlots)

    for (slot in 0 until frameSlots) see(fixed, immediates, slot, entry[slot])
    for (index in 0 until writeCount) {
        written[slots[index]] = true
        if (sources[index] == SlotWriteTable.NOT_A_COPY) {
            see(fixed, immediates, slots[index], kinds[index])
        }
    }
    function.exceptions?.regions.orEmpty().forEach { region ->
        region.catches.forEach { handler ->
            handler.payloadSlots.forEach { slot ->
                written[slot] = true
                see(fixed, immediates, slot, SlotKindFlow.LONG)
            }
        }
    }

    val spread = if (hasCopy(sources, writeCount)) CopyKindPropagation(slots, sources, writeCount, fixed) else null
    if (spread != null) {
        for (slot in 0 until frameSlots) {
            if (fixed[slot] != SlotKindFlow.UNDEFINED) {
                spread.push(slot)
            }
        }
        spread.drain()
    }

    for (slot in 0 until frameSlots) {
        val kind = fixed[slot]
        val mixed = when {
            kind == SlotKindFlow.CONFLICT -> false
            kind == SlotKindFlow.UNDEFINED ->
                immediates[slot] != SlotKindFlow.UNDEFINED && (written[slot] || slot < parameters.size || localTypes[slot] != null)
            else -> immediates[slot] == SlotKindFlow.ANY_WIDE && !SlotKindFlow.wide(kind)
        }
        if (mixed) {
            fixed[slot] = SlotKindFlow.CONFLICT
            spread?.push(slot)
        }
    }

    spread?.drain()

    return fixed
}

private fun hasCopy(
    sources: IntArray,
    writeCount: Int,
): Boolean {

    for (index in 0 until writeCount) {
        if (sources[index] != SlotWriteTable.NOT_A_COPY) {
            return true
        }
    }

    return false
}

private fun see(
    fixed: ByteArray,
    immediates: ByteArray,
    slot: Int,
    kind: Byte,
) {
    when {
        SlotKindFlow.definite(kind) ->
            fixed[slot] = if (fixed[slot] == SlotKindFlow.UNDEFINED || fixed[slot] == kind) kind else SlotKindFlow.CONFLICT
        kind == SlotKindFlow.ANY_NARROW || kind == SlotKindFlow.ANY_WIDE -> immediates[slot] = maxOf(immediates[slot], kind)
    }
}

private class CopyKindPropagation(
    slots: IntArray,
    sources: IntArray,
    writeCount: Int,
    private val fixed: ByteArray,
) {
    private val start = IntArray(fixed.size + 1)
    private val destinations: IntArray
    private val pending = IntArray(fixed.size)
    private val queued = BooleanArray(fixed.size)
    private var top = 0

    init {

        for (index in 0 until writeCount) {
            if (sources[index] != SlotWriteTable.NOT_A_COPY) {
                start[sources[index] + 1]++
            }
        }
        for (slot in fixed.indices) start[slot + 1] += start[slot]

        destinations = IntArray(start[fixed.size])
        val next = start.copyOf(fixed.size)
        for (index in 0 until writeCount) {
            val source = sources[index]
            if (source != SlotWriteTable.NOT_A_COPY) {
                destinations[next[source]++] = slots[index]
            }
        }
    }

    fun push(slot: Int) {

        if (queued[slot]) {
            return
        }

        queued[slot] = true
        pending[top++] = slot
    }

    fun drain() {
        while (top > 0) {

            val source = pending[--top]
            queued[source] = false
            val kind = fixed[source]

            for (edge in start[source] until start[source + 1]) {
                val slot = destinations[edge]
                val current = fixed[slot]
                if (current == SlotKindFlow.CONFLICT) {
                    continue
                }
                val next = if (current == SlotKindFlow.UNDEFINED || current == kind) kind else SlotKindFlow.CONFLICT
                if (next != current) {
                    fixed[slot] = next
                    push(slot)
                }
            }
        }
    }
}

private class SlotKindDataflow(
    private val table: InstructionTable,
    function: CirFunction,
    private val blocks: BasicBlocks,
    private val dense: IntArray,
    private val count: Int,
    private val kinds: ByteArray,
    private val sources: IntArray,
    private val initial: ByteArray,
) {
    private val instructions = function.instructions
    private val size = instructions.size
    private val regions = function.exceptions?.regions.orEmpty()
    private val writes = table.writes
    private val slots = table.writes.slots
    private val modes = table.writes.modes
    private val jumps = table.jumps
    private val leaders = blocks.leaders
    private val blockAt = blocks.blockAt

    val states = arrayOfNulls<ByteArray>(leaders.size)
    private val worklist = BlockWorklist(leaders.size)
    private val caught = arrayOfNulls<ByteArray>(regions.size)

    fun solve(
        entry: ByteArray,
        tracked: IntArray,
    ) {

        val state = ByteArray(count)
        if (leaders.isNotEmpty()) {
            states[0] = ByteArray(count) { index -> entry[tracked[index]] }
            worklist.add(0)
        }

        var current = worklist.next()
        while (current >= 0) {

            val start = leaders[current]
            val last = blocks.end(current) - 1
            checkNotNull(states[current]).copyInto(state)
            val region = table.innermostRegion(start)
            if (region >= 0) {
                raise(region, state)
            }

            for (position in start until last) {
                transfer(position, state)
                if (region >= 0) {
                    raiseWrites(region, position, state)
                }
            }

            val instruction = instructions[last]
            val taken = instruction.alwaysJumps()
            if (modes[last] == SlotWriteTable.CONDITIONAL) {

                val index = writes.start[last]
                val slot = dense[slots[index]]
                kinds[index] = resolve(index, state)

                if (taken != false) {
                    val previous = if (slot >= 0) state[slot] else SlotKindFlow.UNDEFINED
                    assign(index, state)
                    for (edge in jumps.start[last] until jumps.end(last)) flowInto(jumps.targets[edge], state)
                    if (slot >= 0) {
                        state[slot] = previous
                    }
                }
                if (taken != true) {
                    flowInto(last + 1, state)
                }
            } else {

                transfer(last, state)
                if (region >= 0) {
                    raiseWrites(region, last, state)
                }

                if (taken != false) {
                    for (edge in jumps.start[last] until jumps.end(last)) flowInto(jumps.targets[edge], state)
                }
                if (taken != true && instruction.fallsThrough()) {
                    flowInto(last + 1, state)
                }
            }

            current = worklist.next()
        }
    }

    private fun flowInto(
        target: Int,
        state: ByteArray,
        payload: IntArray? = null,
    ) {

        if (target >= size) {
            return
        }

        val destination = blockAt[target]
        val existing = states[destination]
        if (existing == null) {
            val created = state.copyOf()
            payload?.forEach { slot ->
                if (dense[slot] >= 0) {
                    created[dense[slot]] = SlotKindFlow.LONG
                }
            }
            states[destination] = created
            worklist.add(destination)
            return
        }

        var changed = mergeInto(existing, state)
        if (payload != null) {
            for (slot in payload) {
                val index = dense[slot]
                if (index >= 0 && existing[index] != SlotKindFlow.LONG) {
                    existing[index] = SlotKindFlow.LONG
                    changed = true
                }
            }
        }

        if (changed) {
            worklist.add(destination)
        }
    }

    private fun handlers(
        region: Int,
        state: ByteArray,
    ) {
        regions[region].catches.forEach { handler -> flowInto(handler.target.index, state, handler.payloadSlots) }
    }

    private fun raise(
        region: Int,
        state: ByteArray,
    ) {
        var current = region
        while (current >= 0) {

            val existing = caught[current]
            if (existing == null) {
                val created = state.copyOf()
                caught[current] = created
                handlers(current, created)
            } else if (mergeInto(existing, state)) {
                handlers(current, existing)
            }

            current = regions[current].parent ?: -1
        }
    }

    private fun raiseWrites(
        region: Int,
        position: Int,
        state: ByteArray,
    ) {
        var current = region
        while (current >= 0) {

            val existing = checkNotNull(caught[current])
            var changed = false
            for (index in writes.start[position] until writes.end(position)) {
                val slot = dense[slots[index]]
                if (slot >= 0 && mergeAt(existing, slot, state[slot])) {
                    changed = true
                }
            }
            if (changed) {
                handlers(current, existing)
            }

            current = regions[current].parent ?: -1
        }
    }

    private fun mergeInto(
        existing: ByteArray,
        state: ByteArray,
    ): Boolean {

        var changed = false
        for (index in 0 until count) {
            if (mergeAt(existing, index, state[index])) {
                changed = true
            }
        }

        return changed
    }

    private fun resolve(
        index: Int,
        state: ByteArray,
    ): Byte {

        val source = sources[index]
        if (source == SlotWriteTable.NOT_A_COPY) {
            return kinds[index]
        }

        return if (dense[source] >= 0) state[dense[source]] else initial[source]
    }

    private fun assign(
        index: Int,
        state: ByteArray,
    ) {
        val slot = dense[slots[index]]
        if (slot >= 0) {
            state[slot] = kinds[index]
        }
    }

    private fun transfer(
        position: Int,
        state: ByteArray,
    ) {

        val from = writes.start[position]
        val to = writes.end(position)
        if (from == to) {
            return
        }

        if (to - from == 1 || modes[position] == SlotWriteTable.SEQUENTIAL) {
            for (index in from until to) {
                kinds[index] = resolve(index, state)
                assign(index, state)
            }
            return
        }

        if (modes[position] == SlotWriteTable.SELECT) {
            val first = resolve(from, state)
            val second = resolve(from + 1, state)
            val kind = if (SlotKindFlow.definite(first) && SlotKindFlow.definite(second)) first else merge(first, second)
            kinds[from] = kind
            kinds[from + 1] = kind
            assign(from, state)
            return
        }

        for (index in from until to) kinds[index] = resolve(index, state)
        for (index in from until to) assign(index, state)
    }
}

internal class BlockWorklist(
    blocks: Int,
) {
    private val levels: Array<LongArray>
    private var current = -1

    init {

        var depth = 1
        var words = (blocks + 63) ushr 6
        while (words > 1) {
            words = (words + 63) ushr 6
            depth++
        }

        var bits = blocks
        levels = Array(depth) { _ ->
            bits = (bits + 63) ushr 6
            LongArray(maxOf(bits, 1))
        }
    }

    fun add(block: Int) {

        var index = block
        for (level in levels) {
            val word = index ushr 6
            val before = level[word]
            level[word] = before or (1L shl index)
            if (before != 0L) {
                return
            }
            index = word
        }
    }

    fun next(): Int {

        var block = find(current + 1)
        if (block < 0) {
            block = find(0)
        }
        if (block < 0) {
            return -1
        }

        remove(block)
        current = block

        return block
    }

    private fun remove(block: Int) {

        var index = block
        for (level in levels) {
            val word = index ushr 6
            val after = level[word] and (1L shl index).inv()
            level[word] = after
            if (after != 0L) {
                return
            }
            index = word
        }
    }

    private fun find(from: Int): Int {

        var index = from
        var depth = 0
        while (true) {
            val level = levels[depth]
            val word = index ushr 6
            if (word >= level.size) {
                return -1
            }
            val bits = level[word] and (-1L shl index)
            if (bits != 0L) {
                index = (word shl 6) + bits.countTrailingZeroBits()
                break
            }
            if (depth == levels.size - 1) {
                return -1
            }
            index = word + 1
            depth++
        }

        while (depth > 0) {
            depth--
            index = (index shl 6) + levels[depth][index].countTrailingZeroBits()
        }

        return index
    }
}

private fun mergeAt(
    existing: ByteArray,
    index: Int,
    kind: Byte,
): Boolean {

    val merged = merge(existing[index], kind)
    if (merged == existing[index]) {
        return false
    }

    existing[index] = merged

    return true
}

private fun homeKinds(
    table: InstructionTable,
    function: CirFunction,
    blocks: BasicBlocks,
    states: Array<ByteArray?>,
    parameters: List<ValueType>,
    localTypes: Array<ValueType?>,
    entry: ByteArray,
    kinds: ByteArray,
): ByteArray {

    val frameSlots = entry.size
    val leaders = blocks.leaders
    val writes = table.writes
    val slots = writes.slots
    val homes = ByteArray(frameSlots)
    val anyNarrow = BooleanArray(frameSlots)
    val anyWide = BooleanArray(frameSlots)

    for (slot in 0 until frameSlots) {
        if (slot < parameters.size || localTypes[slot] != null) {
            note(homes, anyNarrow, anyWide, slot, entry[slot])
        }
    }
    for (current in leaders.indices) {
        if (states[current] == null) {
            continue
        }
        for (index in writes.start[leaders[current]] until writes.start[blocks.end(current)]) note(homes, anyNarrow, anyWide, slots[index], kinds[index])
    }
    function.exceptions?.regions.orEmpty().forEach { region ->
        region.catches.forEach { handler ->
            handler.payloadSlots.forEach { slot -> note(homes, anyNarrow, anyWide, slot, SlotKindFlow.LONG) }
        }
    }

    val wideHomes = SlotKindFlow.homeBit(SlotKindFlow.LONG) or SlotKindFlow.homeBit(SlotKindFlow.DOUBLE)
    for (slot in 0 until frameSlots) {

        if (!anyNarrow[slot] && !anyWide[slot]) {
            continue
        }

        var mask = homes[slot].toInt()
        if (mask == 0) {
            val declared = localTypes[slot]?.let(WriteKinds::kindOf)
            mask = SlotKindFlow.homeBit(declared ?: if (anyWide[slot]) SlotKindFlow.LONG else SlotKindFlow.INT)
        }
        if (anyWide[slot] && mask and wideHomes == 0) {
            mask = mask or SlotKindFlow.homeBit(SlotKindFlow.LONG)
        }

        homes[slot] = mask.toByte()
    }

    return homes
}

private fun note(
    homes: ByteArray,
    anyNarrow: BooleanArray,
    anyWide: BooleanArray,
    slot: Int,
    kind: Byte,
) {
    when {
        SlotKindFlow.definite(kind) -> homes[slot] = (homes[slot].toInt() or SlotKindFlow.homeBit(kind)).toByte()
        kind == SlotKindFlow.ANY_NARROW -> anyNarrow[slot] = true
        kind == SlotKindFlow.ANY_WIDE -> anyWide[slot] = true
    }
}
