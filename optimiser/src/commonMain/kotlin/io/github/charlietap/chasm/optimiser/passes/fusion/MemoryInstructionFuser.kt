package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction.I32Load
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction.I32Load16S
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction.I32Load16U
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction.I32Load8S
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction.I32Load8U
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction.I64Load
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction.I64Load16S
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction.I64Load16U
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction.I64Load32S
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction.I64Load32U
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction.I64Load8S
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction.I64Load8U
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
        is MemoryInstruction.Load.I32Load8S -> loadFuser(index, instruction, input, output, ::I32Load8S)
        is MemoryInstruction.Load.I32Load8U -> loadFuser(index, instruction, input, output, ::I32Load8U)
        is MemoryInstruction.Load.I32Load16S -> loadFuser(index, instruction, input, output, ::I32Load16S)
        is MemoryInstruction.Load.I32Load16U -> loadFuser(index, instruction, input, output, ::I32Load16U)
        is MemoryInstruction.Load.I64Load -> loadFuser(index, instruction, input, output, ::I64Load)
        is MemoryInstruction.Load.I64Load8S -> loadFuser(index, instruction, input, output, ::I64Load8S)
        is MemoryInstruction.Load.I64Load8U -> loadFuser(index, instruction, input, output, ::I64Load8U)
        is MemoryInstruction.Load.I64Load16S -> loadFuser(index, instruction, input, output, ::I64Load16S)
        is MemoryInstruction.Load.I64Load16U -> loadFuser(index, instruction, input, output, ::I64Load16U)
        is MemoryInstruction.Load.I64Load32S -> loadFuser(index, instruction, input, output, ::I64Load32S)
        is MemoryInstruction.Load.I64Load32U -> loadFuser(index, instruction, input, output, ::I64Load32U)

        else -> {
            output.add(instruction)
            index
        }
    }
}
