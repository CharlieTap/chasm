package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.cir.ReferenceInstruction
import io.github.charlietap.chasm.cir.TableInstruction
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.access
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType

class References(
    val slots: IntArray,
    val writesThrough: Boolean,
    frameSlots: Int,
) {
    val isReference = BooleanArray(frameSlots).also { flags -> slots.forEach { flags[it] = true } }

    val rootIndex = IntArray(frameSlots).also { index -> slots.forEachIndexed { root, slot -> index[slot] = root } }
}

fun analyzeReferences(
    function: CirFunction,
    type: FunctionType,
    frameSlots: Int,
    declarations: ModuleIndexSpaces,
    publishesRegisters: Boolean,
): References {
    if (!publishesRegisters) return References(IntArray(0), writesThrough = false, frameSlots)
    val references = BooleanArray(frameSlots)
    function.exceptions?.regions?.forEach { region ->
        region.catches.forEach { handler -> handler.payloadSlots.forEach { references[it] = true } }
    }
    type.params.types.forEachIndexed { index, parameter -> if (collectable(parameter)) references[index] = true }
    val copies = ArrayList<Long>()
    val visitor = object : SlotWriteVisitor {
        override fun value(
            slot: Int,
            type: ValueType,
        ) {
            if (collectable(type)) references[slot] = true
        }

        override fun opaque(
            slot: Int,
            reference: Boolean,
        ) {
            if (reference) references[slot] = true
        }

        override fun copy(
            source: Operand,
            destination: Int,
        ) {
            if (source is Operand.Slot) copies += (source.slot.toLong() shl 32) or destination.toLong()
        }
    }
    function.instructions.forEach { instruction -> visitSlotWrites(instruction, function, declarations, visitor) }
    val graph = CopyGraph(
        slots = frameSlots,
        sources = IntArray(copies.size) { edge -> (copies[edge] ushr 32).toInt() },
        destinations = IntArray(copies.size) { edge -> copies[edge].toInt() },
    )
    graph.spread(references)
    val slots = references.indices.filter { references[it] }.toIntArray()
    val writesThrough = slots.isNotEmpty() && function.instructions.any(Instruction::mayCollect)
    return References(slots, writesThrough, frameSlots)
}

fun Instruction.mayCollect(): Boolean = when (this) {
    is ControlInstruction.Call, is ControlInstruction.CallIndirectI, is ControlInstruction.CallIndirectS,
    is ControlInstruction.CallRefS, is ControlInstruction.Throw, is ControlInstruction.ThrowRefS,
    is AggregateInstruction, is TableInstruction, is ReferenceInstruction,
    -> true
    is MemoryInstruction -> access() == null
    else -> false
}
