package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.compiler.backend.analysis.SlotReadVisitor
import io.github.charlietap.chasm.compiler.backend.analysis.SlotWriteVisitor
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.jumpTargets
import io.github.charlietap.chasm.compiler.backend.analysis.visitSlotReads
import io.github.charlietap.chasm.compiler.backend.analysis.visitSlotWrites
import io.github.charlietap.chasm.type.ValueType

internal class SlotLiveness(
    private val liveIn: Array<LongArray>,
) {

    fun isLive(
        position: Int,
        slot: Int,
    ): Boolean = liveIn[position].hasBit(slot)

    fun liveCount(
        position: Int,
    ): Int = if (position < liveIn.size) liveIn[position].sumOf { word -> word.countOneBits() } else 0
}

internal fun functionLiveness(
    module: ModuleLowering,
    function: CirFunction,
    slots: Int,
): SlotLiveness? {
    if (function.exceptions != null) return null
    val words = (slots + 63) / 64
    if (function.instructions.size.toLong() * words > MAX_LIVENESS_WORDS) return null
    return SlotLiveness(liveIn(module, function, slots, 0, function.instructions.size, LongArray(words)))
}

internal fun rangeLiveness(
    module: ModuleLowering,
    function: CirFunction,
    slots: Int,
    start: Int,
    end: Int,
    liveOnExit: (slot: Int) -> Boolean,
): SlotLiveness {
    val exit = LongArray((slots + 63) / 64).also { bits -> for (slot in 0 until slots) if (liveOnExit(slot)) bits.setBit(slot) }
    return SlotLiveness(liveIn(module, function, slots, start, end, exit))
}

private fun liveIn(
    module: ModuleLowering,
    function: CirFunction,
    slots: Int,
    start: Int,
    end: Int,
    exit: LongArray,
): Array<LongArray> {
    val words = (slots + 63) / 64
    val count = end - start
    val everything = LongArray(words).also { bits -> for (slot in 0 until slots) bits.setBit(slot) }
    val reads = Array(count) { LongArray(words) }
    val kills = Array(count) { LongArray(words) }
    for (index in 0 until count) {
        val instruction = function.instructions[start + index]
        val read = reads[index]
        val modelled = visitSlotReads(instruction, module.declarations, SlotBits(read))
        if (!modelled) everything.copyInto(read)
        val conditional = instruction is AdminInstruction.JumpIfCopyI || instruction is AdminInstruction.JumpIfCopyS
        if (!conditional) visitSlotWrites(instruction, function, module.declarations, WrittenBits(kills[index]))
    }
    val liveIn = Array(count) { LongArray(words) }
    val out = LongArray(words)
    var changed = true
    while (changed) {
        changed = false
        for (index in count - 1 downTo 0) {
            val instruction = function.instructions[start + index]
            out.fill(0L)
            val successor = { target: Int ->
                val source = if (target in start until end) liveIn[target - start] else exit
                for (word in 0 until words) out[word] = out[word] or source[word]
            }
            if (!instruction.terminates()) successor(start + index + 1)
            for (target in instruction.jumpTargets()) successor(target)
            if (instruction is AdminInstruction.JumpTableS) successor(end)
            val live = liveIn[index]
            for (word in 0 until words) {
                val next = reads[index][word] or (out[word] and kills[index][word].inv())
                if (next != live[word]) {
                    live[word] = next
                    changed = true
                }
            }
        }
    }
    return liveIn
}

private fun Instruction.terminates(): Boolean = when (this) {
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

private fun LongArray.setBit(
    bit: Int,
) {
    this[bit ushr 6] = this[bit ushr 6] or (1L shl (bit and 63))
}

private fun LongArray.hasBit(
    bit: Int,
): Boolean = this[bit ushr 6] and (1L shl (bit and 63)) != 0L

private class SlotBits(
    private val bits: LongArray,
) : SlotReadVisitor {
    override fun read(
        slot: Int,
    ) = bits.setBit(slot)
}

private class WrittenBits(
    private val bits: LongArray,
) : SlotWriteVisitor {
    override fun value(
        slot: Int,
        type: ValueType,
    ) = bits.setBit(slot)

    override fun opaque(
        slot: Int,
        reference: Boolean,
    ) = bits.setBit(slot)

    override fun copy(
        source: Operand,
        destination: Int,
    ) = bits.setBit(destination)
}

private const val MAX_LIVENESS_WORDS = 16_000_000L
