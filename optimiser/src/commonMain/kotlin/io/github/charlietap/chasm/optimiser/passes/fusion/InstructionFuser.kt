package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.ir.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.FusedControlInstruction
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.ir.instruction.FusedParametricInstruction
import io.github.charlietap.chasm.ir.instruction.FusedVariableInstruction
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction
import io.github.charlietap.chasm.ir.instruction.NumericInstruction
import io.github.charlietap.chasm.ir.instruction.ParametricInstruction
import io.github.charlietap.chasm.ir.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ir.instruction.TableInstruction
import io.github.charlietap.chasm.ir.instruction.VariableInstruction
import io.github.charlietap.chasm.ir.instruction.VectorInstruction

internal typealias InstructionFuser = (PassContext, Int, Instruction, List<Instruction>, MutableList<Instruction>) -> Int

internal fun InstructionFuser(
    context: PassContext,
    index: Int,
    instruction: Instruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
): Int = InstructionFuser(
    context = context,
    index = index,
    instruction = instruction,
    input = input,
    output = output,
    controlInstructionFuser = ::ControlInstructionFuser,
    numericInstructionFuser = ::NumericInstructionFuser,
    memoryInstructionFuser = ::MemoryInstructionFuser,
    parametricInstructionFuser = ::ParametricInstructionFuser,
    variableInstructionFuser = ::VariableInstructionFuser,
)

internal inline fun InstructionFuser(
    context: PassContext,
    index: Int,
    instruction: Instruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    controlInstructionFuser: ControlInstructionFuser,
    numericInstructionFuser: NumericInstructionFuser,
    memoryInstructionFuser: MemoryInstructionFuser,
    parametricInstructionFuser: ParametricInstructionFuser,
    variableInstructionFuser: VariableInstructionFuser,
): Int = when (instruction) {
    is NumericInstruction.I32Const,
    is NumericInstruction.I64Const,
    is NumericInstruction.F32Const,
    is NumericInstruction.F64Const,
    is VariableInstruction.GlobalGet,
    is VariableInstruction.LocalGet,
    is AggregateInstruction,
    is AtomicMemoryInstruction,
    is ReferenceInstruction,
    is TableInstruction,
    is VectorInstruction,
    is FusedControlInstruction,
    is FusedNumericInstruction,
    is FusedMemoryInstruction,
    is FusedParametricInstruction,
    is FusedVariableInstruction,
    -> {
        output.add(instruction)
        index
    }
    is ControlInstruction -> controlInstructionFuser(context, index, instruction, input, output)
    is NumericInstruction -> numericInstructionFuser(context, index, instruction, input, output)
    is MemoryInstruction -> memoryInstructionFuser(context, index, instruction, input, output)
    is ParametricInstruction -> parametricInstructionFuser(context, index, instruction, input, output)
    is VariableInstruction -> variableInstructionFuser(context, index, instruction, input, output)
}
