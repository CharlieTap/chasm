package io.github.charlietap.chasm.compiler.frontend.operand

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.compiler.frontend.ext.toCir
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.cir.ValueType as CirValueType

/** Compiler-selected storage and default initialization for defined locals. */
internal class DefinedLocalStorage(
    instructions: Array<Any?>,
    instructionCount: Int,
    locals: List<Local>,
    parameterCount: Int,
) {

    private val slots = IntArray(locals.size)
    private val slotTypes = arrayOfNulls<ValueType>(locals.size)
    private val readFirstEnd: Int
    private val untouchedEnd: Int
    private var untouchedUsed = false
    val slotCount: Int = locals.size

    init {
        val firstAccess = ByteArray(locals.size)
        for (index in 0 until instructionCount) {
            when (val instruction = instructions[index]) {
                is VariableInstruction.LocalGet -> firstAccess.record(instruction.localIdx.toInt() - parameterCount, READ)
                is VariableInstruction.LocalSet -> firstAccess.record(instruction.localIdx.toInt() - parameterCount, WRITTEN)
                is VariableInstruction.LocalTee -> firstAccess.record(instruction.localIdx.toInt() - parameterCount, WRITTEN)
                is ControlInstruction -> if (endsStraightLinePrefix(instruction)) break
                else -> Unit
            }
        }

        readFirstEnd = assignSlots(firstAccess, READ, locals, firstSlot = 0)
        untouchedEnd = assignSlots(firstAccess, UNTOUCHED, locals, firstSlot = readFirstEnd)
        assignSlots(firstAccess, WRITTEN, locals, firstSlot = untouchedEnd)
    }

    fun types(): List<CirValueType> = List(slotCount) { slot -> checkNotNull(slotTypes[slot]).toCir() }

    fun initialisedCount(): Int = if (untouchedUsed) untouchedEnd else readFirstEnd

    fun slot(
        definedLocalIndex: Int,
    ): Int {
        val slot = slots[definedLocalIndex]
        if (slot >= readFirstEnd && slot < untouchedEnd) untouchedUsed = true
        return slot
    }

    private fun assignSlots(
        firstAccess: ByteArray,
        access: Byte,
        locals: List<Local>,
        firstSlot: Int,
    ): Int {
        var nextSlot = firstSlot
        for (index in slots.indices) {
            if (firstAccess[index] != access) continue
            slots[index] = nextSlot
            slotTypes[nextSlot] = locals[index].type
            nextSlot++
        }
        return nextSlot
    }

    private fun ByteArray.record(
        definedIndex: Int,
        access: Byte,
    ) {
        if (definedIndex in indices && this[definedIndex] == UNTOUCHED) this[definedIndex] = access
    }

    private fun endsStraightLinePrefix(
        instruction: ControlInstruction,
    ): Boolean =
        instruction !is ControlInstruction.Call &&
            instruction !is ControlInstruction.CallIndirect &&
            instruction !is ControlInstruction.CallRef &&
            instruction !is ControlInstruction.Nop

    private companion object {
        const val UNTOUCHED: Byte = 0
        const val READ: Byte = 1
        const val WRITTEN: Byte = 2
    }
}
