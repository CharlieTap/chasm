package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.cir.ParametricInstruction
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryAccess
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.access
import io.github.charlietap.chasm.type.ValueType

class InstructionTable internal constructor(
    private val function: CirFunction,
    private val declarations: ModuleIndexSpaces,
    val writes: SlotWriteTable,
    val jumps: JumpTable,
    val accesses: MemoryAccessTable,
    private val innermostRegions: IntArray?,
) {
    val reads: SlotReadTable by lazy(LazyThreadSafetyMode.NONE) { slotReadTable(function, declarations) }

    fun innermostRegion(position: Int): Int {
        val regions = innermostRegions ?: return -1
        return regions[position]
    }

    fun restartsFunction(position: Int): Boolean {
        return function.instructions[position].isSelfTailCall(function, innermostRegion(position))
    }
}

class SlotWriteTable internal constructor(
    val modes: ByteArray,
    val start: IntArray,
    val slots: IntArray,
    val sources: IntArray,
    val kinds: ByteArray,
    val references: BooleanArray,
) {
    val count: Int = start[start.size - 1]

    fun end(position: Int): Int {
        return start[position + 1]
    }

    companion object {
        const val PARALLEL: Byte = 0
        const val SEQUENTIAL: Byte = 1
        const val SELECT: Byte = 2
        const val CONDITIONAL: Byte = 3

        const val NOT_A_COPY = -1
    }
}

class JumpTable internal constructor(
    val start: IntArray,
    val targets: IntArray,
) {
    val count: Int = start[start.size - 1]

    fun end(position: Int): Int {
        return start[position + 1]
    }
}

class MemoryAccessTable internal constructor(
    val start: IntArray,
    val positions: IntArray,
    val memories: IntArray,
    val addresses: IntArray,
    val endOffsets: LongArray,
) {
    val count: Int = positions.size

    fun end(position: Int): Int {
        return start[position + 1]
    }

    fun accessIndexAt(position: Int): Int {
        return if (end(position) > start[position]) start[position] else -1
    }

    fun usesMemory(
        from: Int,
        until: Int,
        memory: Int,
    ): Boolean {
        for (index in start[from] until start[until]) {
            if (memories[index] == memory) {
                return true
            }
        }
        return false
    }

    companion object {
        const val CONSTANT_ADDRESS = -1
    }
}

class SlotReadTable internal constructor(
    val start: IntArray,
    val slots: IntArray,
    val unknown: BooleanArray?,
) {
    fun end(position: Int): Int {
        return start[position + 1]
    }
}

fun instructionTable(
    function: CirFunction,
    declarations: ModuleIndexSpaces,
): InstructionTable {

    val instructions = function.instructions
    val size = instructions.size
    val modes = ByteArray(size)
    val writeStart = IntArray(size + 1)
    val targetStart = IntArray(size + 1)
    val accessStart = IntArray(size + 1)
    val writes = SlotWriteBuilder(size)
    val accesses = MemoryAccessBuilder()
    val targets = JumpTargetBuilder()

    for (position in 0 until size) {

        val instruction = instructions[position]
        writeStart[position] = writes.size
        targetStart[position] = targets.size
        accessStart[position] = accesses.size

        if (instruction is MemoryInstruction) {
            val access = instruction.access()
            if (access != null) {
                accesses.add(position, access)
            }
            visitMemoryWrites(instruction, access, writes)
        } else {
            visitSlotWrites(instruction, function, declarations, writes)
        }

        modes[position] = when (instruction) {
            is AdminInstruction.CopySlotSequence -> SlotWriteTable.SEQUENTIAL
            is ParametricInstruction -> SlotWriteTable.SELECT
            is AdminInstruction.JumpIfCopyI, is AdminInstruction.JumpIfCopyS -> SlotWriteTable.CONDITIONAL
            else -> SlotWriteTable.PARALLEL
        }

        if (instruction is AdminInstruction.JumpTableS) {
            for (target in instruction.targets) targets.add(target)
        } else {
            val target = instruction.jumpTarget()
            if (target >= 0) {
                targets.add(target)
            }
        }
    }
    writeStart[size] = writes.size
    targetStart[size] = targets.size
    accessStart[size] = accesses.size

    val regions = function.exceptions?.regions.orEmpty()
    return InstructionTable(
        function = function,
        declarations = declarations,
        writes = SlotWriteTable(modes, writeStart, writes.slots, writes.sources, writes.kinds, writes.references),
        jumps = JumpTable(targetStart, targets.targets),
        accesses = MemoryAccessTable(
            start = accessStart,
            positions = accesses.positions.copyOf(accesses.size),
            memories = accesses.memories.copyOf(accesses.size),
            addresses = accesses.addresses.copyOf(accesses.size),
            endOffsets = accesses.endOffsets.copyOf(accesses.size),
        ),
        innermostRegions = if (regions.isEmpty()) null else innermostRegions(regions, size),
    )
}

private class SlotWriteBuilder(
    instructions: Int,
) : SlotWriteVisitor {
    var slots = IntArray(maxOf(16, instructions))
    var sources = IntArray(slots.size)
    var kinds = ByteArray(slots.size)
    var references = BooleanArray(slots.size)
    var size = 0

    private fun add(
        slot: Int,
        kind: Byte,
        source: Int,
        reference: Boolean,
    ) {

        if (size == slots.size) {
            slots = slots.copyOf(size * 2)
            sources = sources.copyOf(size * 2)
            kinds = kinds.copyOf(size * 2)
            references = references.copyOf(size * 2)
        }

        slots[size] = slot
        sources[size] = source
        kinds[size] = kind
        references[size] = reference
        size++
    }

    override fun value(
        slot: Int,
        type: ValueType,
    ) {
        add(slot, WriteKinds.kindOf(type), SlotWriteTable.NOT_A_COPY, collectable(type))
    }

    override fun opaque(
        slot: Int,
        reference: Boolean,
    ) {
        add(slot, WriteKinds.LONG, SlotWriteTable.NOT_A_COPY, reference)
    }

    override fun helperResult(
        slot: Int,
        reference: Boolean,
    ) {
        add(slot, if (reference) WriteKinds.LONG else WriteKinds.INT, SlotWriteTable.NOT_A_COPY, reference)
    }

    override fun copy(
        source: Operand,
        destination: Int,
    ) {
        when (source) {
            is Operand.Slot -> add(destination, WriteKinds.COPIED, source.slot, reference = false)
            is Operand.Immediate -> add(destination, WriteKinds.constantKind(source.bits), SlotWriteTable.NOT_A_COPY, reference = false)
        }
    }
}

private class JumpTargetBuilder {
    var targets = IntArray(16)
    var size = 0

    fun add(target: Int) {

        if (size == targets.size) {
            targets = targets.copyOf(size * 2)
        }

        targets[size++] = target
    }
}

private class MemoryAccessBuilder {
    var positions = IntArray(16)
    var memories = IntArray(16)
    var addresses = IntArray(16)
    var endOffsets = LongArray(16)
    var size = 0

    fun add(
        position: Int,
        access: MemoryAccess,
    ) {

        if (size == positions.size) {
            positions = positions.copyOf(size * 2)
            memories = memories.copyOf(size * 2)
            addresses = addresses.copyOf(size * 2)
            endOffsets = endOffsets.copyOf(size * 2)
        }

        val address = access.address
        positions[size] = position
        memories[size] = access.memory.index
        addresses[size] = if (address is Operand.Slot) address.slot else MemoryAccessTable.CONSTANT_ADDRESS
        endOffsets[size] = (access.offset.toLong() and 0xFFFF_FFFFL) + access.operation.width
        size++
    }
}

private fun slotReadTable(
    function: CirFunction,
    declarations: ModuleIndexSpaces,
): SlotReadTable {

    val instructions = function.instructions
    val size = instructions.size
    val start = IntArray(size + 1)
    val reads = SlotReadBuilder(size)
    var unknown: BooleanArray? = null

    for (position in 0 until size) {
        start[position] = reads.size
        if (!visitSlotReads(instructions[position], declarations, reads)) {
            reads.size = start[position]
            val flags = unknown ?: BooleanArray(size)
            flags[position] = true
            unknown = flags
        }
    }
    start[size] = reads.size

    return SlotReadTable(start, reads.slots, unknown)
}

private class SlotReadBuilder(
    instructions: Int,
) : SlotReadVisitor {
    var slots = IntArray(maxOf(16, instructions * 2))
    var size = 0

    override fun read(slot: Int) {

        if (size == slots.size) {
            slots = slots.copyOf(size * 2)
        }

        slots[size++] = slot
    }
}
