package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.ir.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction
import io.github.charlietap.chasm.ir.instruction.NumericInstruction
import io.github.charlietap.chasm.ir.instruction.ParametricInstruction
import io.github.charlietap.chasm.ir.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ir.instruction.TableInstruction
import io.github.charlietap.chasm.ir.instruction.VariableInstruction
import io.github.charlietap.chasm.ir.instruction.VectorInstruction

internal typealias InstructionFuser = (Int, Instruction, List<Instruction>, MutableList<Instruction>) -> Int

internal fun InstructionFuser(
    index: Int,
    instruction: Instruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
): Int = InstructionFuser(
    index = index,
    instruction = instruction,
    input = input,
    output = output,
    controlInstructionFuser = ::ControlInstructionFuser,
    numericInstructionFuser = ::NumericInstructionFuser,
)

internal inline fun InstructionFuser(
    index: Int,
    instruction: Instruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    controlInstructionFuser: ControlInstructionFuser,
    numericInstructionFuser: NumericInstructionFuser,
): Int = when (instruction) {
    is NumericInstruction.I32Const,
    is NumericInstruction.I64Const,
    is NumericInstruction.F32Const,
    is NumericInstruction.F64Const,
    is VariableInstruction.GlobalGet,
    is VariableInstruction.GlobalSet,
    is VariableInstruction.LocalGet,
    is VariableInstruction.LocalSet,
    is AggregateInstruction,
    is AtomicMemoryInstruction,
    is MemoryInstruction,
    is ParametricInstruction,
    is ReferenceInstruction,
    is TableInstruction,
    is VariableInstruction,
    is VectorInstruction,
    is FusedNumericInstruction,
    -> {
        output.add(instruction)
        index
    }
    is ControlInstruction -> controlInstructionFuser(index, instruction, input, output)
    is NumericInstruction -> numericInstructionFuser(index, instruction, input, output)
}
