package io.github.charlietap.chasm.compiler.backend.jvm

import io.github.charlietap.chasm.compiler.backend.jvm.lower.Locals
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.IincInsnNode
import org.objectweb.asm.tree.JumpInsnNode
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.LookupSwitchInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.TableSwitchInsnNode
import org.objectweb.asm.tree.VarInsnNode

internal class SlotLiveness(
    private val liveIn: Array<LongArray>,
    private val slotBits: Array<IntArray>,
    private val liveSlots: IntArray,
) {

    fun isLive(
        position: Int,
        slot: Int,
    ): Boolean {
        if (position !in liveIn.indices) return true
        val live = liveIn[position]
        return slotBits[slot].any { bit -> live[bit / 64] and (1L shl (bit % 64)) != 0L }
    }

    fun liveCount(
        position: Int,
    ): Int = if (position in liveSlots.indices) liveSlots[position] else slotBits.size
}

internal fun slotLiveness(
    node: MethodNode,
    locals: Locals,
    frameSlots: Int,
    positions: Int,
    placement: (Int) -> Label,
): SlotLiveness {
    val instructions = node.instructions.toArray()
    val count = instructions.size
    val bits = IntArray(locals.size) { -1 }
    val bitSlots = ArrayList<Int>()
    for (local in 0 until locals.size) {
        val slot = locals.slotOf(local)
        if (slot < 0) continue
        bits[local] = bitSlots.size
        bitSlots += slot
    }
    val words = (bitSlots.size + 63) / 64
    val index = node.instructions::indexOf

    val successors = Array(count) { position -> successors(instructions, position, index) }
    val handlers = arrayOfNulls<IntArray>(count)
    node.tryCatchBlocks.forEach { block ->
        val handler = index(block.handler)
        for (position in index(block.start) until index(block.end)) {
            handlers[position] = (handlers[position] ?: IntArray(0)) + handler
        }
    }

    val liveIn = Array(count) { LongArray(words) }
    val scratch = LongArray(words)
    var changed = true
    while (changed) {
        changed = false
        for (position in count - 1 downTo 0) {
            scratch.fill(0)
            successors[position].forEach { successor -> or(scratch, liveIn[successor]) }
            val instruction = instructions[position]
            when (instruction) {
                is VarInsnNode -> {
                    val bit = bits.getOrElse(instruction.`var`) { -1 }
                    if (bit >= 0) {
                        if (instruction.opcode in Opcodes.ISTORE..Opcodes.ASTORE) clear(scratch, bit) else set(scratch, bit)
                    }
                }
                is IincInsnNode -> bits.getOrElse(instruction.`var`) { -1 }.let { bit -> if (bit >= 0) set(scratch, bit) }
            }
            handlers[position]?.forEach { handler -> or(scratch, liveIn[handler]) }
            if (!scratch.contentEquals(liveIn[position])) {
                scratch.copyInto(liveIn[position])
                changed = true
            }
        }
    }

    val atPositions = Array(positions) { position -> liveIn[index(placement(position).info as LabelNode)] }
    val bitsBySlot = Array(frameSlots) { ArrayList<Int>(1) }
    bitSlots.forEachIndexed { bit, slot -> bitsBySlot[slot] += bit }
    val slotBits = Array(frameSlots) { slot -> bitsBySlot[slot].toIntArray() }
    val counted = IntArray(frameSlots) { -1 }
    val liveSlots = IntArray(positions) { position ->
        var slots = 0
        atPositions[position].forEachIndexed { word, value ->
            var remaining = value
            while (remaining != 0L) {
                val slot = bitSlots[word * 64 + remaining.countTrailingZeroBits()]
                if (counted[slot] != position) {
                    counted[slot] = position
                    slots++
                }
                remaining = remaining and (remaining - 1)
            }
        }
        slots
    }
    return SlotLiveness(atPositions, slotBits, liveSlots)
}

private fun successors(
    instructions: Array<AbstractInsnNode>,
    position: Int,
    index: (AbstractInsnNode) -> Int,
): IntArray {
    val next = if (position + 1 < instructions.size) intArrayOf(position + 1) else IntArray(0)
    return when (val instruction = instructions[position]) {
        is JumpInsnNode -> {
            val target = index(instruction.label)
            if (instruction.opcode == Opcodes.GOTO) intArrayOf(target) else next + target
        }
        is TableSwitchInsnNode -> (instruction.labels + instruction.dflt).map(index).distinct().toIntArray()
        is LookupSwitchInsnNode -> (instruction.labels + instruction.dflt).map(index).distinct().toIntArray()
        else -> when (instruction.opcode) {
            in Opcodes.IRETURN..Opcodes.RETURN, Opcodes.ATHROW -> IntArray(0)
            else -> next
        }
    }
}

private fun or(
    into: LongArray,
    from: LongArray,
) {
    for (word in into.indices) into[word] = into[word] or from[word]
}

private fun set(
    bits: LongArray,
    bit: Int,
) {
    bits[bit / 64] = bits[bit / 64] or (1L shl (bit % 64))
}

private fun clear(
    bits: LongArray,
    bit: Int,
) {
    bits[bit / 64] = bits[bit / 64] and (1L shl (bit % 64)).inv()
}
