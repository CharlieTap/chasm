package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.access
import io.github.charlietap.chasm.type.ValueType

fun CirFunction.redundantBoundsChecks(
    declarations: ModuleIndexSpaces,
): BooleanArray {
    val size = instructions.size
    val targets = jumpTargets()
    val handlerEntries = BooleanArray(size + 1)
    exceptions?.regions?.forEach { region -> region.catches.forEach { handler -> handlerEntries[handler.target.index] = true } }
    val incomingByTarget = HashMap<Int, HashMap<Int, CheckedReachBySlot>>()
    val writes = PositionWrites(this, declarations)
    var redundant: BooleanArray
    var passes = 0
    var retainedFacts = 0L
    do {
        if (++passes > MAX_PASSES) return BooleanArray(size)
        var changed = false
        redundant = BooleanArray(size)
        var facts: CheckedReachBySlot? = HashMap()
        for (position in 0 until size) {
            if (position == 0 || handlerEntries[position]) {
                facts = HashMap()
            } else if (targets[position]) {
                facts = incomingByTarget[position]?.values.orEmpty().fold(facts) { met, carried -> meet(met, carried) }
            }
            val current = facts ?: HashMap()
            val instruction = instructions[position]
            val access = (instruction as? MemoryInstruction)?.access()
            val address = access?.address
            if (access != null && access.memory.index == 0 && address is Operand.Slot) {
                val reach = (access.offset.toLong() and 0xFFFF_FFFFL) + access.operation.width
                val proven = current[address.slot]
                if (proven != null && reach <= proven) {
                    redundant[position] = true
                } else {
                    current[address.slot] = maxOf(proven ?: 0L, reach)
                }
            }
            val jumps = instruction.jumpTargets()
            writes.apply(position, current, carryCopiedReach = jumps.isEmpty())
            var snapshot: CheckedReachBySlot? = null
            for (target in jumps) {
                val carried = incomingByTarget.getOrPut(target) { HashMap() }
                val previous = carried[position]
                if ((snapshot == null || previous !== snapshot) && previous != current) {
                    carried[position] = snapshot ?: HashMap(current).also { copy ->
                        snapshot = copy
                        retainedFacts += copy.size
                        if (retainedFacts > MAX_RETAINED_FACTS) return BooleanArray(size)
                    }
                    changed = true
                }
            }
            facts = if (instruction.endsPath()) null else current
        }
    } while (changed)
    return redundant
}

private const val MAX_PASSES = 64

private const val MAX_RETAINED_FACTS = 4_000_000L

private typealias CheckedReachBySlot = HashMap<Int, Long>

private fun meet(
    first: CheckedReachBySlot?,
    second: CheckedReachBySlot,
): CheckedReachBySlot {
    if (first == null) return HashMap(second)
    val iterator = first.entries.iterator()
    while (iterator.hasNext()) {
        val entry = iterator.next()
        val other = second[entry.key]
        if (other == null) iterator.remove() else entry.setValue(minOf(entry.value, other))
    }
    return first
}

private fun Instruction.endsPath() = when (this) {
    is AdminInstruction.Jump,
    is AdminInstruction.JumpCopies,
    is AdminInstruction.JumpTableS,
    is AdminInstruction.EndFunction,
    is ControlInstruction.FunctionReturn,
    is ControlInstruction.Unreachable,
    is ControlInstruction.Throw,
    is ControlInstruction.ThrowRefS,
    is ControlInstruction.ReturnCall,
    is ControlInstruction.ReturnCallIndirectI,
    is ControlInstruction.ReturnCallIndirectS,
    is ControlInstruction.ReturnCallRefS,
    -> true
    else -> false
}

private class PositionWrites(
    function: CirFunction,
    declarations: ModuleIndexSpaces,
) : SlotWriteVisitor {
    private val first = IntArray(function.instructions.size + 1)
    private val copiedFrom = IntArray(function.instructions.size) { -1 }
    private var slots = IntArray(function.instructions.size)
    private var count = 0
    private var position = 0

    init {
        function.instructions.forEachIndexed { index, instruction ->
            position = index
            first[index] = count
            visitSlotWrites(instruction, function, declarations, this)
        }
        first[function.instructions.size] = count
    }

    override fun value(
        slot: Int,
        type: ValueType,
    ) = add(slot)

    override fun opaque(
        slot: Int,
        reference: Boolean,
    ) = add(slot)

    override fun copy(
        source: Operand,
        destination: Int,
    ) {
        add(destination)
        copiedFrom[position] = (source as? Operand.Slot)?.slot ?: -1
    }

    private fun add(
        slot: Int,
    ) {
        if (count == slots.size) slots = slots.copyOf(maxOf(16, count * 2))
        slots[count++] = slot
    }

    fun apply(
        position: Int,
        facts: CheckedReachBySlot,
        carryCopiedReach: Boolean,
    ) {
        val start = first[position]
        val end = first[position + 1]
        val source = copiedFrom[position]
        val carried = if (carryCopiedReach && end - start == 1 && source >= 0) facts[source] else null
        for (index in start until end) facts.remove(slots[index])
        if (carried != null) facts[slots[start]] = carried
    }
}
