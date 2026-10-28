package io.github.charlietap.chasm.compiler.frontend.instruction

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.compiler.frontend.emptyIntArray
import io.github.charlietap.chasm.compiler.frontend.program.ProgramBuilder

internal class CopyInstructionBuffer(
    private val program: ProgramBuilder,
) {

    private var sourceSlots = emptyIntArray
    private var destinationSlots = emptyIntArray
    private var size = 0

    fun append(
        sourceSlot: Int,
        destinationSlot: Int,
    ) {
        if (size == sourceSlots.size) {
            val capacity = maxOf(INITIAL_CAPACITY, sourceSlots.size * 2)
            sourceSlots = sourceSlots.copyOf(capacity)
            destinationSlots = destinationSlots.copyOf(capacity)
        }
        sourceSlots[size] = sourceSlot
        destinationSlots[size] = destinationSlot
        size++
    }

    fun flush() {
        if (size == 0) return

        if (size == 1) {
            program.append(AdminInstruction.CopySlot(sourceSlots[0], destinationSlots[0]))
        } else {
            program.append(AdminInstruction.CopySlotSequence(sourceSlots.copyOf(size), destinationSlots.copyOf(size)))
        }
        size = 0
    }

    private companion object {
        const val INITIAL_CAPACITY = 4
    }
}
