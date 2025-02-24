package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Abs
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Add
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Div
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Mul
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Sub
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Add
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Div
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Mul
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Sub
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Add
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32And
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32DivS
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32DivU
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Eqz
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Mul
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Or
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Shl
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32ShrS
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32ShrU
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Sub
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Xor
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Add
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64DivS
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64DivU
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Eqz
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Mul
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Sub
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.NumericInstruction

internal typealias NumericInstructionFuser = (PassContext, Int, NumericInstruction, List<Instruction>, MutableList<Instruction>) -> Int

internal fun NumericInstructionFuser(
    context: PassContext,
    index: Int,
    instruction: NumericInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
): Int = NumericInstructionFuser(
    context = context,
    index = index,
    instruction = instruction,
    input = input,
    output = output,
    unop = ::UnopFuser,
    binop = ::BinopFuser,
)

internal inline fun NumericInstructionFuser(
    context: PassContext,
    index: Int,
    instruction: NumericInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    unop: UnopFuser,
    binop: BinopFuser,
): Int = when (instruction) {
    is NumericInstruction.I32Add -> binop(index, instruction, input, output, ::I32Add)
    is NumericInstruction.I32Sub -> binop(index, instruction, input, output, ::I32Sub)
    is NumericInstruction.I32Mul -> binop(index, instruction, input, output, ::I32Mul)
    is NumericInstruction.I32DivS -> binop(index, instruction, input, output, ::I32DivS)
    is NumericInstruction.I32DivU -> binop(index, instruction, input, output, ::I32DivU)
    is NumericInstruction.I32And -> binop(index, instruction, input, output, ::I32And)
    is NumericInstruction.I32Or -> binop(index, instruction, input, output, ::I32Or)
    is NumericInstruction.I32Xor -> binop(index, instruction, input, output, ::I32Xor)
    is NumericInstruction.I32Shl -> binop(index, instruction, input, output, ::I32Shl)
    is NumericInstruction.I32ShrS -> binop(index, instruction, input, output, ::I32ShrS)
    is NumericInstruction.I32ShrU -> binop(index, instruction, input, output, ::I32ShrU)
    is NumericInstruction.I32Eqz -> unop(index, instruction, input, output, ::I32Eqz)
    is NumericInstruction.I64Eqz -> unop(index, instruction, input, output, ::I64Eqz)
    is NumericInstruction.I64Add -> binop(index, instruction, input, output, ::I64Add)
    is NumericInstruction.I64Sub -> binop(index, instruction, input, output, ::I64Sub)
    is NumericInstruction.I64Mul -> binop(index, instruction, input, output, ::I64Mul)
    is NumericInstruction.I64DivS -> binop(index, instruction, input, output, ::I64DivS)
    is NumericInstruction.I64DivU -> binop(index, instruction, input, output, ::I64DivU)
    is NumericInstruction.F32Abs -> unop(index, instruction, input, output, ::F32Abs)
    is NumericInstruction.F32Add -> binop(index, instruction, input, output, ::F32Add)
    is NumericInstruction.F32Sub -> binop(index, instruction, input, output, ::F32Sub)
    is NumericInstruction.F32Mul -> binop(index, instruction, input, output, ::F32Mul)
    is NumericInstruction.F32Div -> binop(index, instruction, input, output, ::F32Div)
    is NumericInstruction.F64Add -> binop(index, instruction, input, output, ::F64Add)
    is NumericInstruction.F64Sub -> binop(index, instruction, input, output, ::F64Sub)
    is NumericInstruction.F64Mul -> binop(index, instruction, input, output, ::F64Mul)
    is NumericInstruction.F64Div -> binop(index, instruction, input, output, ::F64Div)
    else -> {
        output.add(instruction)
        index
    }
}
