package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Abs
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Add
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Div
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Mul
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Sub
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Add
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Div
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Ge
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Lt
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Mul
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Sub
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Add
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32And
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32DivS
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32DivU
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Eq
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Eqz
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32GeS
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32GeU
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32GtS
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32GtU
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32LeS
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32LeU
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32LtS
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32LtU
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Mul
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Ne
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
    commutativeBinop = ::CommutativeBinopFuser,
    nonCommutativeBinop = ::NonCommutativeBinopFuser,
)

internal inline fun NumericInstructionFuser(
    context: PassContext,
    index: Int,
    instruction: NumericInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    unop: UnopFuser,
    commutativeBinop: BinopFuser,
    nonCommutativeBinop: BinopFuser,
): Int = when (instruction) {
    is NumericInstruction.I32Add -> nonCommutativeBinop(index, instruction, input, output, ::I32Add)
    is NumericInstruction.I32Sub -> nonCommutativeBinop(index, instruction, input, output, ::I32Sub)
    is NumericInstruction.I32Mul -> commutativeBinop(index, instruction, input, output, ::I32Mul)
    is NumericInstruction.I32DivS -> nonCommutativeBinop(index, instruction, input, output, ::I32DivS)
    is NumericInstruction.I32DivU -> nonCommutativeBinop(index, instruction, input, output, ::I32DivU)
    is NumericInstruction.I32And -> commutativeBinop(index, instruction, input, output, ::I32And)
    is NumericInstruction.I32Or -> commutativeBinop(index, instruction, input, output, ::I32Or)
    is NumericInstruction.I32Xor -> commutativeBinop(index, instruction, input, output, ::I32Xor)
    is NumericInstruction.I32Shl -> nonCommutativeBinop(index, instruction, input, output, ::I32Shl)
    is NumericInstruction.I32ShrS -> nonCommutativeBinop(index, instruction, input, output, ::I32ShrS)
    is NumericInstruction.I32ShrU -> nonCommutativeBinop(index, instruction, input, output, ::I32ShrU)
    is NumericInstruction.I32LtS -> nonCommutativeBinop(index, instruction, input, output, ::I32LtS)
    is NumericInstruction.I32LtU -> nonCommutativeBinop(index, instruction, input, output, ::I32LtU)
    is NumericInstruction.I32GtS -> nonCommutativeBinop(index, instruction, input, output, ::I32GtS)
    is NumericInstruction.I32GtU -> nonCommutativeBinop(index, instruction, input, output, ::I32GtU)
    is NumericInstruction.I32LeS -> nonCommutativeBinop(index, instruction, input, output, ::I32LeS)
    is NumericInstruction.I32LeU -> nonCommutativeBinop(index, instruction, input, output, ::I32LeU)
    is NumericInstruction.I32GeS -> nonCommutativeBinop(index, instruction, input, output, ::I32GeS)
    is NumericInstruction.I32GeU -> nonCommutativeBinop(index, instruction, input, output, ::I32GeU)
    is NumericInstruction.I32Eq -> commutativeBinop(index, instruction, input, output, ::I32Eq)
    is NumericInstruction.I32Ne -> commutativeBinop(index, instruction, input, output, ::I32Ne)
    is NumericInstruction.I32Eqz -> unop(index, instruction, input, output, ::I32Eqz)
    is NumericInstruction.I64Eqz -> unop(index, instruction, input, output, ::I64Eqz)
    is NumericInstruction.I64Add -> nonCommutativeBinop(index, instruction, input, output, ::I64Add)
    is NumericInstruction.I64Sub -> nonCommutativeBinop(index, instruction, input, output, ::I64Sub)
    is NumericInstruction.I64Mul -> commutativeBinop(index, instruction, input, output, ::I64Mul)
    is NumericInstruction.I64DivS -> nonCommutativeBinop(index, instruction, input, output, ::I64DivS)
    is NumericInstruction.I64DivU -> nonCommutativeBinop(index, instruction, input, output, ::I64DivU)
    is NumericInstruction.F32Abs -> unop(index, instruction, input, output, ::F32Abs)
    is NumericInstruction.F32Add -> commutativeBinop(index, instruction, input, output, ::F32Add)
    is NumericInstruction.F32Sub -> nonCommutativeBinop(index, instruction, input, output, ::F32Sub)
    is NumericInstruction.F32Mul -> commutativeBinop(index, instruction, input, output, ::F32Mul)
    is NumericInstruction.F32Div -> nonCommutativeBinop(index, instruction, input, output, ::F32Div)
    is NumericInstruction.F64Add -> nonCommutativeBinop(index, instruction, input, output, ::F64Add)
    is NumericInstruction.F64Sub -> nonCommutativeBinop(index, instruction, input, output, ::F64Sub)
    is NumericInstruction.F64Mul -> commutativeBinop(index, instruction, input, output, ::F64Mul)
    is NumericInstruction.F64Div -> nonCommutativeBinop(index, instruction, input, output, ::F64Div)
    is NumericInstruction.F64Ge -> nonCommutativeBinop(index, instruction, input, output, ::F64Ge)
    is NumericInstruction.F64Lt -> nonCommutativeBinop(index, instruction, input, output, ::F64Lt)
    else -> {
        output.add(instruction)
        index
    }
}
