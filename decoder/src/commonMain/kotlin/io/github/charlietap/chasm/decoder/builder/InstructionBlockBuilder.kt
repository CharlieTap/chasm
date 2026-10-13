package io.github.charlietap.chasm.decoder.builder

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction

/**
 * Accumulates the instructions of one expression in a buffer that is reused across expressions,
 * so each built list is allocated once at its exact size.
 */
internal class InstructionBlockBuilder {

    // Untyped so that appending does not pay for an element type check against the Instruction interface
    private var instructions = arrayOfNulls<Any>(INITIAL_CAPACITY)
    private var size = 0

    fun clear() {
        size = 0
    }

    fun append(instruction: Instruction) {
        if (size == instructions.size) {
            instructions = instructions.copyOf(size * 2)
        }
        instructions[size++] = instruction
    }

    fun appendEnd(count: Int) {
        append(ControlInstruction.End(count))
    }

    @Suppress("UNCHECKED_CAST")
    fun build(): List<Instruction> = instructions.copyOf(size).asList() as List<Instruction>
}

private const val INITIAL_CAPACITY = 256
