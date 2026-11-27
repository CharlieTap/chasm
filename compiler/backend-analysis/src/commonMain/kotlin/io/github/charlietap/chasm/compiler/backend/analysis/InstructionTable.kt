package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.cir.ParametricInstruction
import io.github.charlietap.chasm.compiler.backend.analysis.form.InstructionForms
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryAccess
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.type.ValueType

@InternalChasmApi
class InstructionTable internal constructor(
    val forms: InstructionForms,
    val writes: SlotWriteTable,
    val jumps: JumpTable,
    val accesses: MemoryAccessTable,
    private val innermostRegions: IntArray?,
    private val storage: InstructionTableStorage,
) {
    val reads: SlotReadTable by lazy(LazyThreadSafetyMode.NONE) { slotReadTable(forms, storage) }

    fun innermostRegion(position: Int): Int {
        val regions = innermostRegions ?: return -1
        return regions[position]
    }

    fun restartsFunction(position: Int): Boolean {
        return forms.instruction(position).isSelfTailCall(forms.function, innermostRegion(position))
    }
}

@InternalChasmApi
class SlotWriteTable internal constructor(
    val modes: ByteArray,
    val start: IntArray,
    val slots: IntArray,
    val sources: IntArray,
    val kinds: ByteArray,
    val references: BooleanArray,
    val count: Int,
) {
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

@InternalChasmApi
class JumpTable internal constructor(
    val start: IntArray,
    val targets: IntArray,
    val fallsThrough: BooleanArray,
    val size: Int,
    val count: Int,
) {
    fun end(position: Int): Int {
        return start[position + 1]
    }
}

@InternalChasmApi
class MemoryAccessTable internal constructor(
    val start: IntArray,
    val positions: IntArray,
    val memories: IntArray,
    val addresses: IntArray,
    val endOffsets: LongArray,
    val count: Int,
) {
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

@InternalChasmApi
class SlotReadTable internal constructor(
    val start: IntArray,
    val slots: IntArray,
    val unknown: BooleanArray?,
) {
    fun end(position: Int): Int {
        return start[position + 1]
    }
}

@InternalChasmApi
class InstructionTableStorage {
    internal var modes = ByteArray(0)
    internal var writeStart = IntArray(0)
    internal var targetStart = IntArray(0)
    internal var accessStart = IntArray(0)
    internal var fallsThrough = BooleanArray(0)
    internal var writeSlots = IntArray(0)
    internal var writeSources = IntArray(0)
    internal var writeKinds = ByteArray(0)
    internal var writeReferences = BooleanArray(0)
    internal var targets = IntArray(0)
    internal var accessPositions = IntArray(0)
    internal var accessMemories = IntArray(0)
    internal var accessAddresses = IntArray(0)
    internal var accessEndOffsets = LongArray(0)
    internal var readStart = IntArray(0)
    internal var readSlots = IntArray(0)
}

@InternalChasmApi
fun instructionTable(
    forms: InstructionForms,
    storage: InstructionTableStorage = InstructionTableStorage(),
    objectReferences: Boolean = false,
): InstructionTable {

    val function = forms.function
    val instructions = function.instructions
    val size = instructions.size
    val modes = storage.modes.reusedFor(size).also { reused -> storage.modes = reused }
    val writeStart = storage.writeStart.reusedFor(size + 1).also { reused -> storage.writeStart = reused }
    val targetStart = storage.targetStart.reusedFor(size + 1).also { reused -> storage.targetStart = reused }
    val accessStart = storage.accessStart.reusedFor(size + 1).also { reused -> storage.accessStart = reused }
    val fallsThrough = storage.fallsThrough.reusedFor(size).also { reused -> storage.fallsThrough = reused }
    val writes = SlotWriteBuilder(storage, size, objectReferences)
    val accesses = MemoryAccessBuilder(storage)
    val targets = JumpTargetBuilder(storage)

    for (position in 0 until size) {

        val instruction = instructions[position]
        writeStart[position] = writes.size
        targetStart[position] = targets.size
        accessStart[position] = accesses.size

        if (instruction is MemoryInstruction) {
            val access = forms.access(position)
            if (access != null) {
                accesses.add(position, access)
            }
        }
        visitSlotWrites(position, forms, writes)

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
        fallsThrough[position] = instruction.fallsThrough()
    }
    writeStart[size] = writes.size
    targetStart[size] = targets.size
    accessStart[size] = accesses.size

    val regions = function.exceptions?.regions.orEmpty()
    return InstructionTable(
        forms = forms,
        writes = SlotWriteTable(modes, writeStart, writes.slots, writes.sources, writes.kinds, writes.references, writes.size),
        jumps = JumpTable(targetStart, targets.targets, fallsThrough, size, targets.size),
        accesses = MemoryAccessTable(
            start = accessStart,
            positions = accesses.positions,
            memories = accesses.memories,
            addresses = accesses.addresses,
            endOffsets = accesses.endOffsets,
            count = accesses.size,
        ),
        innermostRegions = if (regions.isEmpty()) null else innermostRegions(regions, size),
        storage = storage,
    )
}

private class SlotWriteBuilder(
    private val storage: InstructionTableStorage,
    instructions: Int,
    private val objectReferences: Boolean,
) : SlotWriteVisitor {
    var slots = storage.writeSlots.reusedFor(instructions)
    var sources = storage.writeSources.reusedFor(slots.size)
    var kinds = storage.writeKinds.reusedFor(slots.size)
    var references = storage.writeReferences.reusedFor(slots.size)
    var size = 0

    init {
        store()
    }

    private fun store() {
        storage.writeSlots = slots
        storage.writeSources = sources
        storage.writeKinds = kinds
        storage.writeReferences = references
    }

    private fun add(
        slot: Int,
        kind: Byte,
        source: Int,
        reference: Boolean,
    ) {

        if (size == slots.size || size == sources.size || size == kinds.size || size == references.size) {
            val capacity = size * 2
            slots = slots.copyOf(capacity)
            sources = sources.copyOf(capacity)
            kinds = kinds.copyOf(capacity)
            references = references.copyOf(capacity)
            store()
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
        add(slot, WriteKinds.kindOf(type, objectReferences), SlotWriteTable.NOT_A_COPY, collectable(type))
    }

    override fun opaque(
        slot: Int,
        reference: Boolean,
    ) {
        add(slot, if (objectReferences) WriteKinds.OBJECT else WriteKinds.LONG, SlotWriteTable.NOT_A_COPY, reference)
    }

    override fun helperResult(
        slot: Int,
        reference: Boolean,
    ) {
        val kind = when {
            !reference -> WriteKinds.INT
            objectReferences -> WriteKinds.OBJECT
            else -> WriteKinds.LONG
        }
        add(slot, kind, SlotWriteTable.NOT_A_COPY, reference)
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

private class JumpTargetBuilder(
    private val storage: InstructionTableStorage,
) {
    var targets = storage.targets.reusedFor(MINIMUM_CAPACITY).also { reused -> storage.targets = reused }
    var size = 0

    fun add(target: Int) {

        if (size == targets.size) {
            targets = targets.copyOf(size * 2)
            storage.targets = targets
        }

        targets[size++] = target
    }
}

private class MemoryAccessBuilder(
    private val storage: InstructionTableStorage,
) {
    var positions = storage.accessPositions.reusedFor(MINIMUM_CAPACITY)
    var memories = storage.accessMemories.reusedFor(positions.size)
    var addresses = storage.accessAddresses.reusedFor(positions.size)
    var endOffsets = storage.accessEndOffsets.reusedFor(positions.size)
    var size = 0

    init {
        store()
    }

    private fun store() {
        storage.accessPositions = positions
        storage.accessMemories = memories
        storage.accessAddresses = addresses
        storage.accessEndOffsets = endOffsets
    }

    fun add(
        position: Int,
        access: MemoryAccess,
    ) {

        if (size == positions.size || size == memories.size || size == addresses.size || size == endOffsets.size) {
            val capacity = size * 2
            positions = positions.copyOf(capacity)
            memories = memories.copyOf(capacity)
            addresses = addresses.copyOf(capacity)
            endOffsets = endOffsets.copyOf(capacity)
            store()
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
    forms: InstructionForms,
    storage: InstructionTableStorage,
): SlotReadTable {

    val size = forms.size
    val start = storage.readStart.reusedFor(size + 1).also { reused -> storage.readStart = reused }
    val reads = SlotReadBuilder(storage, size)
    var unknown: BooleanArray? = null

    for (position in 0 until size) {
        start[position] = reads.size
        if (!visitSlotReads(position, forms, reads)) {
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
    private val storage: InstructionTableStorage,
    instructions: Int,
) : SlotReadVisitor {
    var slots = storage.readSlots.reusedFor(instructions * 2).also { reused -> storage.readSlots = reused }
    var size = 0

    override fun read(slot: Int) {

        if (size == slots.size) {
            slots = slots.copyOf(size * 2)
            storage.readSlots = slots
        }

        slots[size++] = slot
    }
}

private fun ByteArray.reusedFor(size: Int): ByteArray {
    return if (fitsCapacity(this.size, size)) this else ByteArray(capacityFor(size))
}

private fun BooleanArray.reusedFor(size: Int): BooleanArray {
    return if (fitsCapacity(this.size, size)) this else BooleanArray(capacityFor(size))
}

private fun IntArray.reusedFor(size: Int): IntArray {
    return if (fitsCapacity(this.size, size)) this else IntArray(capacityFor(size))
}

private fun LongArray.reusedFor(size: Int): LongArray {
    return if (fitsCapacity(this.size, size)) this else LongArray(capacityFor(size))
}

private fun fitsCapacity(
    capacity: Int,
    size: Int,
): Boolean {
    return capacity >= size && capacity <= maxOf(size * RETAINED_GROWTH, RETAINED_CAPACITY)
}

private fun capacityFor(size: Int): Int {
    return maxOf(size, MINIMUM_CAPACITY)
}

private const val MINIMUM_CAPACITY = 16

private const val RETAINED_GROWTH = 4

private const val RETAINED_CAPACITY = 16_384
