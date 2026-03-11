package io.github.charlietap.chasm.compiler.passes.fusion

import io.github.charlietap.chasm.compiler.passes.PassContext
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.ir.instruction.AggregateSuperInstruction
import io.github.charlietap.chasm.ir.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction
import io.github.charlietap.chasm.ir.instruction.MemorySuperInstruction
import io.github.charlietap.chasm.ir.instruction.NumericInstruction
import io.github.charlietap.chasm.ir.instruction.NumericSuperInstruction
import io.github.charlietap.chasm.ir.instruction.ParametricInstruction
import io.github.charlietap.chasm.ir.instruction.ParametricSuperInstruction
import io.github.charlietap.chasm.ir.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ir.instruction.ReferenceSuperInstruction
import io.github.charlietap.chasm.ir.instruction.TableInstruction
import io.github.charlietap.chasm.ir.instruction.TableSuperInstruction
import io.github.charlietap.chasm.ir.instruction.VariableInstruction
import io.github.charlietap.chasm.ir.instruction.VariableSuperInstruction
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
    aggregateInstructionFuser = ::AggregateInstructionFuser,
    controlInstructionFuser = ::ControlInstructionFuser,
    numericInstructionFuser = ::NumericInstructionFuser,
    memoryInstructionFuser = ::MemoryInstructionFuser,
    parametricInstructionFuser = ::ParametricInstructionFuser,
    referenceInstructionFuser = ::ReferenceInstructionFuser,
    tableInstructionFuser = ::TableInstructionFuser,
    variableInstructionFuser = ::VariableInstructionFuser,
)

internal inline fun InstructionFuser(
    context: PassContext,
    index: Int,
    instruction: Instruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    aggregateInstructionFuser: AggregateInstructionFuser,
    controlInstructionFuser: ControlInstructionFuser,
    numericInstructionFuser: NumericInstructionFuser,
    memoryInstructionFuser: MemoryInstructionFuser,
    parametricInstructionFuser: ParametricInstructionFuser,
    referenceInstructionFuser: ReferenceInstructionFuser,
    tableInstructionFuser: TableInstructionFuser,
    variableInstructionFuser: VariableInstructionFuser,
): Int = when (instruction) {
    is NumericInstruction.I32Const,
    is NumericInstruction.I64Const,
    is NumericInstruction.F32Const,
    is NumericInstruction.F64Const,
    is VariableInstruction.GlobalGet,
    is VariableInstruction.LocalGet,
    is AdminInstruction,
    is AtomicMemoryInstruction,
    is VectorInstruction,
    is AggregateSuperInstruction,
    is ControlSuperInstruction,
    is NumericSuperInstruction,
    is MemorySuperInstruction,
    is ParametricSuperInstruction,
    is ReferenceSuperInstruction,
    is TableSuperInstruction,
    is VariableSuperInstruction,
    -> {
        output.add(instruction)
        index
    }
    is AggregateInstruction -> aggregateInstructionFuser(context, index, instruction, input, output)
    is ControlInstruction -> controlInstructionFuser(context, index, instruction, input, output)
    is NumericInstruction -> numericInstructionFuser(context, index, instruction, input, output)
    is MemoryInstruction -> memoryInstructionFuser(context, index, instruction, input, output)
    is ParametricInstruction -> parametricInstructionFuser(context, index, instruction, input, output)
    is ReferenceInstruction -> referenceInstructionFuser(context, index, instruction, input, output)
    is TableInstruction -> tableInstructionFuser(context, index, instruction, input, output)
    is VariableInstruction -> variableInstructionFuser(context, index, instruction, input, output)
}
