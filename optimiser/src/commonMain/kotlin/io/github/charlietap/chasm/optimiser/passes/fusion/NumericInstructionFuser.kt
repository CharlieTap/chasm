package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Abs
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Add
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Ceil
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Div
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Eq
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Floor
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Ge
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Gt
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Le
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Lt
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Mul
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Ne
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Nearest
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Neg
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Sqrt
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Sub
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Trunc
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Abs
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Add
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Ceil
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Div
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Eq
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Floor
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Ge
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Gt
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Le
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Lt
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Mul
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Ne
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Nearest
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Neg
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Sqrt
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Sub
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Trunc
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Add
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32And
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Clz
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Ctz
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32DivS
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32DivU
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Eq
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Eqz
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Extend16S
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Extend8S
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
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Popcnt
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Shl
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32ShrS
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32ShrU
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Sub
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Xor
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Add
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Clz
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Ctz
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64DivS
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64DivU
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Eq
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Eqz
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Extend16S
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Extend32S
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Extend8S
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64GeS
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64GeU
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64GtS
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64GtU
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64LeS
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64LeU
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64LtS
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64LtU
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Mul
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Ne
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Popcnt
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
    is NumericInstruction.I32Clz -> unop(index, instruction, input, output, ::I32Clz)
    is NumericInstruction.I32Ctz -> unop(index, instruction, input, output, ::I32Ctz)
    is NumericInstruction.I32Popcnt -> unop(index, instruction, input, output, ::I32Popcnt)
    is NumericInstruction.I64Clz -> unop(index, instruction, input, output, ::I64Clz)
    is NumericInstruction.I64Ctz -> unop(index, instruction, input, output, ::I64Ctz)
    is NumericInstruction.I64Popcnt -> unop(index, instruction, input, output, ::I64Popcnt)
    is NumericInstruction.I32Extend8S -> unop(index, instruction, input, output, ::I32Extend8S)
    is NumericInstruction.I32Extend16S -> unop(index, instruction, input, output, ::I32Extend16S)
    is NumericInstruction.I64Extend8S -> unop(index, instruction, input, output, ::I64Extend8S)
    is NumericInstruction.I64Extend16S -> unop(index, instruction, input, output, ::I64Extend16S)
    is NumericInstruction.I64Extend32S -> unop(index, instruction, input, output, ::I64Extend32S)
    is NumericInstruction.F32Abs -> unop(index, instruction, input, output, ::F32Abs)
    is NumericInstruction.F32Add -> commutativeBinop(index, instruction, input, output, ::F32Add)
    is NumericInstruction.F32Sub -> nonCommutativeBinop(index, instruction, input, output, ::F32Sub)
    is NumericInstruction.F32Mul -> commutativeBinop(index, instruction, input, output, ::F32Mul)
    is NumericInstruction.F32Div -> nonCommutativeBinop(index, instruction, input, output, ::F32Div)
    is NumericInstruction.F32Neg -> unop(index, instruction, input, output, ::F32Neg)
    is NumericInstruction.F32Ceil -> unop(index, instruction, input, output, ::F32Ceil)
    is NumericInstruction.F32Floor -> unop(index, instruction, input, output, ::F32Floor)
    is NumericInstruction.F32Trunc -> unop(index, instruction, input, output, ::F32Trunc)
    is NumericInstruction.F32Sqrt -> unop(index, instruction, input, output, ::F32Sqrt)
    is NumericInstruction.F32Nearest -> unop(index, instruction, input, output, ::F32Nearest)
    is NumericInstruction.F64Add -> nonCommutativeBinop(index, instruction, input, output, ::F64Add)
    is NumericInstruction.F64Sub -> nonCommutativeBinop(index, instruction, input, output, ::F64Sub)
    is NumericInstruction.F64Mul -> commutativeBinop(index, instruction, input, output, ::F64Mul)
    is NumericInstruction.F64Div -> nonCommutativeBinop(index, instruction, input, output, ::F64Div)
    is NumericInstruction.F64Eq -> commutativeBinop(index, instruction, input, output, ::F64Eq)
    is NumericInstruction.F64Ne -> commutativeBinop(index, instruction, input, output, ::F64Ne)
    is NumericInstruction.F64Lt -> nonCommutativeBinop(index, instruction, input, output, ::F64Lt)
    is NumericInstruction.F64Gt -> nonCommutativeBinop(index, instruction, input, output, ::F64Gt)
    is NumericInstruction.F64Le -> nonCommutativeBinop(index, instruction, input, output, ::F64Le)
    is NumericInstruction.F64Ge -> nonCommutativeBinop(index, instruction, input, output, ::F64Ge)
    is NumericInstruction.F64Neg -> unop(index, instruction, input, output, ::F64Neg)
    is NumericInstruction.F64Abs -> unop(index, instruction, input, output, ::F64Abs)
    is NumericInstruction.F64Ceil -> unop(index, instruction, input, output, ::F64Ceil)
    is NumericInstruction.F64Floor -> unop(index, instruction, input, output, ::F64Floor)
    is NumericInstruction.F64Trunc -> unop(index, instruction, input, output, ::F64Trunc)
    is NumericInstruction.F64Sqrt -> unop(index, instruction, input, output, ::F64Sqrt)
    is NumericInstruction.F64Nearest -> unop(index, instruction, input, output, ::F64Nearest)
    is NumericInstruction.F32Eq -> commutativeBinop(index, instruction, input, output, ::F32Eq)
    is NumericInstruction.F32Ne -> commutativeBinop(index, instruction, input, output, ::F32Ne)
    is NumericInstruction.F32Lt -> nonCommutativeBinop(index, instruction, input, output, ::F32Lt)
    is NumericInstruction.F32Gt -> nonCommutativeBinop(index, instruction, input, output, ::F32Gt)
    is NumericInstruction.F32Le -> nonCommutativeBinop(index, instruction, input, output, ::F32Le)
    is NumericInstruction.F32Ge -> nonCommutativeBinop(index, instruction, input, output, ::F32Ge)
    is NumericInstruction.I64Eq -> commutativeBinop(index, instruction, input, output, ::I64Eq)
    is NumericInstruction.I64Ne -> commutativeBinop(index, instruction, input, output, ::I64Ne)
    is NumericInstruction.I64LtS -> nonCommutativeBinop(index, instruction, input, output, ::I64LtS)
    is NumericInstruction.I64LtU -> nonCommutativeBinop(index, instruction, input, output, ::I64LtU)
    is NumericInstruction.I64GtS -> nonCommutativeBinop(index, instruction, input, output, ::I64GtS)
    is NumericInstruction.I64GtU -> nonCommutativeBinop(index, instruction, input, output, ::I64GtU)
    is NumericInstruction.I64LeS -> nonCommutativeBinop(index, instruction, input, output, ::I64LeS)
    is NumericInstruction.I64LeU -> nonCommutativeBinop(index, instruction, input, output, ::I64LeU)
    is NumericInstruction.I64GeS -> nonCommutativeBinop(index, instruction, input, output, ::I64GeS)
    is NumericInstruction.I64GeU -> nonCommutativeBinop(index, instruction, input, output, ::I64GeU)
    else -> {
        output.add(instruction)
        index
    }
}
