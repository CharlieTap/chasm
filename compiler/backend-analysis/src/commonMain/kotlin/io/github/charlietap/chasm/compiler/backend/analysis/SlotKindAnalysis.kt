package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType

class SlotKinds(
    private val kinds: ByteArray,
    private val accountedWrites: Set<Long>,
    private val shadows: ByteArray = ByteArray(kinds.size),
) {

    fun kind(
        slot: Int,
    ): Byte = kinds[slot]

    fun floatShadow(
        slot: Int,
    ): Boolean = shadows[slot].toInt() and SHADOW_FLOAT != 0

    fun doubleShadow(
        slot: Int,
    ): Boolean = shadows[slot].toInt() and SHADOW_DOUBLE != 0

    fun accountsFor(
        position: Int,
        slot: Int,
    ): Boolean = mixWriteKey(writeKey(position, slot)) in accountedWrites

    companion object {
        const val LONG: Byte = 0
        const val INT: Byte = 1
        const val FLOAT: Byte = 2
        const val DOUBLE: Byte = 3

        const val PROLOGUE = -1

        internal const val SHADOW_FLOAT = 1
        internal const val SHADOW_DOUBLE = 2

        fun wide(
            frameSlots: Int,
        ) = SlotKinds(ByteArray(frameSlots), emptySet())
    }
}

class NarrowHomeMismatch(
    slot: Int,
    position: Int,
) : Exception("unexpected write of slot $slot at $position")

private fun writeKey(
    position: Int,
    slot: Int,
) = (position.toLong() shl 32) or (slot.toLong() and 0xFFFF_FFFFL)

private fun writeKeySlot(
    key: Long,
) = (key and 0xFFFF_FFFFL).toInt()

private fun mixWriteKey(
    key: Long,
) = key * -0x61c8864680b583ebL

private class SlotCopy(
    val position: Int,
    val source: Int,
    val destination: Int,
)

private const val WRITE_I32 = 1
private const val WRITE_F32 = 2
private const val WRITE_I64 = 4
private const val WRITE_F64 = 8
private const val WRITE_OTHER = 16
private const val WRITE_WIDE_CONSTANT = 32
private const val WRITES_WIDE = WRITE_I64 or WRITE_F64 or WRITE_OTHER or WRITE_WIDE_CONSTANT

fun analyzeSlotKinds(
    function: CirFunction,
    parameters: List<ValueType>,
    frameSlots: Int,
    localTypes: Array<ValueType?>,
    initialValues: LongArray,
    references: References,
    declarations: ModuleIndexSpaces,
    floatingHome: (Int) -> Boolean = { true },
): SlotKinds {
    val written = IntArray(frameSlots)
    val narrowWriteCandidates = ArrayList<Long>()
    val copies = ArrayList<SlotCopy>()

    fun write(
        position: Int,
        slot: Int,
        flag: Int,
    ) {
        written[slot] = written[slot] or flag
        if (flag == WRITE_I32 || flag == WRITE_F32) narrowWriteCandidates += writeKey(position, slot)
    }

    fun other(
        slot: Int,
    ) {
        written[slot] = written[slot] or WRITE_OTHER
    }

    parameters.forEachIndexed { index, type -> write(SlotKinds.PROLOGUE, index, writeFlag(type)) }
    initialValues.forEachIndexed { slot, value -> if (value.toInt().toLong() != value) other(slot) }
    function.exceptions?.regions?.forEach { region -> region.catches.forEach { handler -> handler.payloadSlots.forEach(::other) } }
    references.slots.forEach(::other)
    function.instructions.forEachIndexed { position, instruction ->
        visitSlotWrites(
            instruction,
            function,
            declarations,
            object : SlotWriteVisitor {
                override fun value(
                    slot: Int,
                    type: ValueType,
                ) = write(position, slot, writeFlag(type))

                override fun opaque(
                    slot: Int,
                    reference: Boolean,
                ) = other(slot)

                override fun copy(
                    source: Operand,
                    destination: Int,
                ) {
                    when (source) {
                        is Operand.Slot -> copies += SlotCopy(position, source.slot, destination)
                        is Operand.Immediate -> if (source.bits.toInt().toLong() == source.bits) {
                            narrowWriteCandidates += writeKey(position, destination)
                        } else {
                            written[destination] = written[destination] or WRITE_WIDE_CONSTANT
                        }
                    }
                }
            },
        )
    }
    val copied = BooleanArray(frameSlots).also { flags -> copies.forEach { copy -> flags[copy.destination] = true } }

    fun has(
        slot: Int,
        flags: Int,
    ) = written[slot] and flags != 0

    fun localType(
        slot: Int,
    ): NumberType? {
        val type = localTypes.getOrNull(slot) as? ValueType.Number ?: return null
        return type.numberType.takeUnless { has(slot, WRITE_OTHER or WRITE_WIDE_CONSTANT) }
    }
    val pinned = BooleanArray(frameSlots) { slot ->
        when (localType(slot)) {
            NumberType.I32, NumberType.F32 -> !has(slot, WRITE_I64 or WRITE_F64)
            NumberType.F64 -> !has(slot, WRITE_I32 or WRITE_F32 or WRITE_I64) && floatingHome(slot)
            else -> false
        }
    }

    val graph = CopyGraph(
        slots = frameSlots,
        sources = IntArray(copies.size) { edge -> copies[edge].source },
        destinations = IntArray(copies.size) { edge -> copies[edge].destination },
    )

    fun solve(
        seed: (Int) -> Boolean,
    ): BooleanArray {
        val lacks = BooleanArray(frameSlots) { slot -> !seed(slot) }
        graph.spread(lacks, blocked = pinned)
        return BooleanArray(frameSlots) { slot -> !lacks[slot] }
    }

    fun pinnedOr(
        slot: Int,
        vararg types: NumberType,
        otherwise: () -> Boolean,
    ) =
        if (pinned[slot]) localType(slot) in types else otherwise()
    val narrow = solve { slot ->
        pinnedOr(slot, NumberType.I32, NumberType.F32) {
            !has(slot, WRITES_WIDE) && (has(slot, WRITE_I32 or WRITE_F32) || copied[slot])
        }
    }
    val float = solve { slot ->
        pinnedOr(slot, NumberType.F32) { narrow[slot] && !has(slot, WRITE_I32) && (has(slot, WRITE_F32) || copied[slot]) }
    }
    val double = solve { slot ->
        pinnedOr(slot, NumberType.F64) {
            !narrow[slot] && !has(slot, WRITE_I32 or WRITE_F32 or WRITE_I64 or WRITE_OTHER) && (has(slot, WRITE_F64) || copied[slot]) &&
                floatingHome(slot)
        }
    }
    val kinds = ByteArray(frameSlots) { slot ->
        when {
            float[slot] && floatingHome(slot) -> SlotKinds.FLOAT
            narrow[slot] -> SlotKinds.INT
            double[slot] -> SlotKinds.DOUBLE
            else -> SlotKinds.LONG
        }
    }
    val accountedWrites = HashSet<Long>()
    narrowWriteCandidates.forEach { key -> if (narrow[writeKeySlot(key)]) accountedWrites += mixWriteKey(key) }
    copies.forEach { copy -> if (narrow[copy.destination]) accountedWrites += mixWriteKey(writeKey(copy.position, copy.destination)) }
    parameters.indices.forEach { if (narrow[it]) accountedWrites += mixWriteKey(writeKey(SlotKinds.PROLOGUE, it)) }
    return SlotKinds(kinds, accountedWrites, shadows(kinds, written, graph, floatingHome))
}

private fun shadows(
    kinds: ByteArray,
    written: IntArray,
    graph: CopyGraph,
    floatingHome: (Int) -> Boolean,
): ByteArray {
    val holdsFloat = BooleanArray(kinds.size) { slot -> written[slot] and WRITE_F32 != 0 || kinds[slot] == SlotKinds.FLOAT }
    val holdsDouble = BooleanArray(kinds.size) { slot -> written[slot] and WRITE_F64 != 0 || kinds[slot] == SlotKinds.DOUBLE }
    graph.spread(holdsFloat)
    graph.spread(holdsDouble)
    return ByteArray(kinds.size) { slot ->
        var shadow = 0
        if (holdsFloat[slot] && kinds[slot] != SlotKinds.FLOAT && floatingHome(slot)) shadow = shadow or SlotKinds.SHADOW_FLOAT
        if (holdsDouble[slot] && kinds[slot] != SlotKinds.DOUBLE && floatingHome(slot)) shadow = shadow or SlotKinds.SHADOW_DOUBLE
        shadow.toByte()
    }
}

private fun writeFlag(
    type: ValueType,
): Int = when {
    type is ValueType.Number && type.numberType == NumberType.I32 -> WRITE_I32
    type is ValueType.Number && type.numberType == NumberType.F32 -> WRITE_F32
    type is ValueType.Number && type.numberType == NumberType.I64 -> WRITE_I64
    type is ValueType.Number && type.numberType == NumberType.F64 -> WRITE_F64
    else -> WRITE_OTHER
}
