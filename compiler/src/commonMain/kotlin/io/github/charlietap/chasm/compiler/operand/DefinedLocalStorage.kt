package io.github.charlietap.chasm.compiler.operand

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.runtime.ext.default

/** Compiler-selected storage and default initialization for defined locals. */
internal class DefinedLocalStorage(
    function: Function,
    parameterCount: Int,
) {

    private val slots = IntArray(function.locals.size) { NO_SLOT }
    val initialValues: LongArray
    val slotCount: Int

    init {
        val read = BooleanArray(function.locals.size)
        val assignedInStraightLinePrefix = BooleanArray(function.locals.size)
        val needsDefault = BooleanArray(function.locals.size)
        var straightLinePrefix = true

        for (instruction in function.body.instructions) {
            when (instruction) {
                is VariableInstruction.LocalGet -> {
                    val definedIndex = instruction.localIdx.toInt() - parameterCount
                    if (definedIndex in read.indices) {
                        if (!read[definedIndex]) {
                            needsDefault[definedIndex] =
                                !straightLinePrefix || !assignedInStraightLinePrefix[definedIndex]
                        }
                        read[definedIndex] = true
                    }
                }
                is VariableInstruction.LocalSet -> {
                    val definedIndex = instruction.localIdx.toInt() - parameterCount
                    if (straightLinePrefix && definedIndex in read.indices) {
                        assignedInStraightLinePrefix[definedIndex] = true
                    }
                }
                is VariableInstruction.LocalTee -> {
                    val definedIndex = instruction.localIdx.toInt() - parameterCount
                    if (straightLinePrefix && definedIndex in read.indices) {
                        assignedInStraightLinePrefix[definedIndex] = true
                    }
                }
                is ControlInstruction -> if (instruction !is ControlInstruction.Call &&
                    instruction !is ControlInstruction.CallIndirect &&
                    instruction !is ControlInstruction.CallRef &&
                    instruction !== ControlInstruction.Nop
                ) {
                    straightLinePrefix = false
                }
                else -> Unit
            }
        }

        var nextSlot = 0
        for (index in slots.indices) {
            if (read[index] && needsDefault[index]) slots[index] = nextSlot++
        }
        val initializedSlotCount = nextSlot
        for (index in slots.indices) {
            if (read[index] && !needsDefault[index]) slots[index] = nextSlot++
        }
        slotCount = nextSlot
        initialValues = LongArray(initializedSlotCount)
        for (index in slots.indices) {
            val slot = slots[index]
            if (slot in initialValues.indices) {
                initialValues[slot] = function.locals[index].type.default()
            }
        }
    }

    fun hasSlot(definedLocalIndex: Int): Boolean = slots[definedLocalIndex] != NO_SLOT

    fun slot(definedLocalIndex: Int): Int {
        val slot = slots[definedLocalIndex]
        check(slot != NO_SLOT) { "defined local has no storage" }
        return slot
    }

    private companion object {
        const val NO_SLOT = -1
    }
}
