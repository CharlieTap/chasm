package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.MemArg
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction
import io.github.charlietap.chasm.ir.module.Index

internal typealias MemoryLoadInstructionFactory = (FusedOperand, FusedDestination, Index.MemoryIndex, MemArg) -> FusedMemoryInstruction
internal typealias MemoryLoadFuser = (Int, MemoryInstruction.Load, List<Instruction>, MutableList<Instruction>, MemoryLoadInstructionFactory) -> Int

internal fun MemoryLoadFuser(
    index: Int,
    instruction: MemoryInstruction.Load,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    fusedInstructionFactory: MemoryLoadInstructionFactory,
): Int = MemoryLoadFuser(
    index = index,
    instruction = instruction,
    input = input,
    output = output,
    fusedInstructionFactory = fusedInstructionFactory,
    operandFactory = ::FusedOperandFactory,
    destinationFactory = ::FusedDestinationFactory,
)

internal inline fun MemoryLoadFuser(
    index: Int,
    instruction: MemoryInstruction.Load,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    fusedInstructionFactory: MemoryLoadInstructionFactory,
    operandFactory: FusedOperandFactory,
    destinationFactory: FusedDestinationFactory,
): Int {

    var nextIndex = index

    val operand = input.getOrNull(index - 1)?.let(operandFactory)
    val destination = input.getOrNull(index + 1).let(destinationFactory)

    val instruction = when {
        operand == null && destination == FusedDestination.ValueStack -> instruction
        operand == null -> {
            fusedInstructionFactory(FusedOperand.ValueStack, destination, instruction.memoryIndex, instruction.memArg)
        }
        else -> {
            output.removeLast()
            fusedInstructionFactory(operand, destination, instruction.memoryIndex, instruction.memArg)
        }
    }

    output.add(instruction)

    if (destination != FusedDestination.ValueStack) {
        nextIndex++
    }

    return nextIndex
}
