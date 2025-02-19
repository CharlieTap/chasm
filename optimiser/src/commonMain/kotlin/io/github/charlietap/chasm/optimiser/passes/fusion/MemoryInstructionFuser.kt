package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction.I32Load
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction

internal typealias MemoryInstructionFuser = (Int, MemoryInstruction, List<Instruction>, MutableList<Instruction>) -> Int

internal fun MemoryInstructionFuser(
    index: Int,
    instruction: MemoryInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
): Int = MemoryInstructionFuser(
    index = index,
    instruction = instruction,
    input = input,
    output = output,
    loadFuser = ::MemoryLoadFuser,
)

internal inline fun MemoryInstructionFuser(
    index: Int,
    instruction: MemoryInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    loadFuser: MemoryLoadFuser,
): Int {

    return when (instruction) {
        is MemoryInstruction.Load.I32Load -> loadFuser(index, instruction, input, output, ::I32Load)
        else -> {
            output.add(instruction)
            index
        }
    }
}
