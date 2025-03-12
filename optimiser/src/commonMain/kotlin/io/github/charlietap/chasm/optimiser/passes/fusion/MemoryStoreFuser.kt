package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.MemArg
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction
import io.github.charlietap.chasm.ir.module.Index

internal typealias MemoryStoreInstructionFactory = (FusedOperand, FusedOperand, Index.MemoryIndex, MemArg) -> FusedMemoryInstruction
internal typealias MemoryStoreFuser = (Int, MemoryInstruction.Store, List<Instruction>, MutableList<Instruction>, MemoryStoreInstructionFactory) -> Int

internal fun MemoryStoreFuser(
    index: Int,
    instruction: MemoryInstruction.Store,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    fusedInstructionFactory: MemoryStoreInstructionFactory,
): Int = MemoryStoreFuser(
    index = index,
    instruction = instruction,
    input = input,
    output = output,
    fusedInstructionFactory = fusedInstructionFactory,
    operandFactory = ::FusedOperandFactory,
)

internal inline fun MemoryStoreFuser(
    index: Int,
    instruction: MemoryInstruction.Store,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    fusedInstructionFactory: MemoryStoreInstructionFactory,
    operandFactory: FusedOperandFactory,
): Int {

    val value = input.getOrNull(index - 1)?.let(operandFactory)
    val address = input.getOrNull(index - 2)?.let(operandFactory)

    val instruction = when {
        value == null -> instruction
        address == null -> {
            output.removeAt(output.lastIndex)
            fusedInstructionFactory(value, FusedOperand.ValueStack, instruction.memoryIndex, instruction.memArg)
        } else -> {
            output.removeAt(output.lastIndex)
            output.removeAt(output.lastIndex)
            fusedInstructionFactory(value, address, instruction.memoryIndex, instruction.memArg)
        }
    }

    output.add(instruction)

    return index
}
