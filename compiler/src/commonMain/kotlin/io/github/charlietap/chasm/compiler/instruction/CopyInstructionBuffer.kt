package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.compiler.diagnostic.CompilerInstructionObserver
import io.github.charlietap.chasm.compiler.emptyIntArray
import io.github.charlietap.chasm.compiler.program.ProgramBuilder
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.CopySlotDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.CopySlotSequenceDispatcher
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

internal class CopyInstructionBuffer(
    private val program: ProgramBuilder,
    private val instructionObserver: CompilerInstructionObserver?,
) {

    private var sourceSlots = emptyIntArray
    private var destinationSlots = emptyIntArray
    private var size = 0

    fun append(sourceSlot: Int, destinationSlot: Int) {
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
            val sourceSlot = sourceSlots[0]
            val destinationSlot = destinationSlots[0]
            val dispatchableInstruction = CopySlotDispatcher(sourceSlot, destinationSlot)
            instructionObserver?.onInstruction(
                dispatchableInstruction,
                AdminInstruction.CopySlot(sourceSlot, destinationSlot),
            )
            program.append(dispatchableInstruction)
        } else {
            val sources = sourceSlots.copyOf(size)
            val destinations = destinationSlots.copyOf(size)
            val dispatchableInstruction = CopySlotSequenceDispatcher(sources, destinations)
            instructionObserver?.onInstruction(
                dispatchableInstruction,
                AdminInstruction.CopySlots(sources, destinations),
            )
            program.append(dispatchableInstruction)
        }
        size = 0
    }

    private companion object {
        const val INITIAL_CAPACITY = 4
    }
}
