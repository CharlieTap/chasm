package io.github.charlietap.chasm.optimiser.passes.fusion

import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Abs
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Add
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Ceil
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32ConvertI32S
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32ConvertI32U
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32ConvertI64S
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32ConvertI64U
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Copysign
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32DemoteF64
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Div
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Eq
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Floor
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Ge
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Gt
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Le
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Lt
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Max
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F32Min
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
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64ConvertI32S
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64ConvertI32U
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64ConvertI64S
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64ConvertI64U
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Copysign
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Div
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Eq
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Floor
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Ge
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Gt
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Le
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Lt
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Max
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Min
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Mul
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Ne
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Nearest
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64Neg
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.F64PromoteF32
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
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32RemS
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32RemU
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Rotl
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Rotr
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Shl
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32ShrS
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32ShrU
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Sub
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32TruncF32S
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32TruncF32U
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32TruncF64S
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32TruncF64U
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32TruncSatF32S
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32TruncSatF32U
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32TruncSatF64S
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32TruncSatF64U
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32WrapI64
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I32Xor
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Add
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64And
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Clz
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Ctz
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64DivS
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64DivU
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Eq
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Eqz
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Extend16S
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Extend32S
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Extend8S
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64ExtendI32S
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64ExtendI32U
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
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Or
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Popcnt
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64RemS
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64RemU
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Rotl
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Rotr
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Shl
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64ShrS
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64ShrU
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Sub
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64TruncF32S
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64TruncF32U
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64TruncF64S
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64TruncF64U
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64TruncSatF32S
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64TruncSatF32U
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64TruncSatF64S
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64TruncSatF64U
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction.I64Xor
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.NumericInstruction
import io.github.charlietap.chasm.optimiser.passes.PassContextt

internal typealias NumericInstructionFuser = (PassContextt, Int, NumericInstruction, List<Instruction>, MutableList<Instruction>) -> Int

internal fun NumericInstructionFuser(
    context: PassContextt,
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
    nonCommutativeBinop = ::NonCommutativeBinopFuser,
)

internal inline fun NumericInstructionFuser(
    context: PassContextt,
    index: Int,
    instruction: NumericInstruction,
    input: List<Instruction>,
    output: MutableList<Instruction>,
    unop: UnopFuser,
    nonCommutativeBinop: BinopFuser,
): Int = when (instruction) {
    is NumericInstruction.I32Add -> nonCommutativeBinop(index, instruction, input, output, ::I32Add)
    is NumericInstruction.I32Sub -> nonCommutativeBinop(index, instruction, input, output, ::I32Sub)
    is NumericInstruction.I32Mul -> nonCommutativeBinop(index, instruction, input, output, ::I32Mul)
    is NumericInstruction.I32DivS -> nonCommutativeBinop(index, instruction, input, output, ::I32DivS)
    is NumericInstruction.I32DivU -> nonCommutativeBinop(index, instruction, input, output, ::I32DivU)
    is NumericInstruction.I32And -> nonCommutativeBinop(index, instruction, input, output, ::I32And)
    is NumericInstruction.I32Or -> nonCommutativeBinop(index, instruction, input, output, ::I32Or)
    is NumericInstruction.I32Xor -> nonCommutativeBinop(index, instruction, input, output, ::I32Xor)
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
    is NumericInstruction.I32Eq -> nonCommutativeBinop(index, instruction, input, output, ::I32Eq)
    is NumericInstruction.I32Ne -> nonCommutativeBinop(index, instruction, input, output, ::I32Ne)
    is NumericInstruction.I32Eqz -> unop(index, instruction, input, output, ::I32Eqz)
    is NumericInstruction.I64Eqz -> unop(index, instruction, input, output, ::I64Eqz)
    is NumericInstruction.I64Add -> nonCommutativeBinop(index, instruction, input, output, ::I64Add)
    is NumericInstruction.I64Sub -> nonCommutativeBinop(index, instruction, input, output, ::I64Sub)
    is NumericInstruction.I64Mul -> nonCommutativeBinop(index, instruction, input, output, ::I64Mul)
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
    is NumericInstruction.F32Add -> nonCommutativeBinop(index, instruction, input, output, ::F32Add)
    is NumericInstruction.F32Sub -> nonCommutativeBinop(index, instruction, input, output, ::F32Sub)
    is NumericInstruction.F32Mul -> nonCommutativeBinop(index, instruction, input, output, ::F32Mul)
    is NumericInstruction.F32Div -> nonCommutativeBinop(index, instruction, input, output, ::F32Div)
    is NumericInstruction.F32Neg -> unop(index, instruction, input, output, ::F32Neg)
    is NumericInstruction.F32Ceil -> unop(index, instruction, input, output, ::F32Ceil)
    is NumericInstruction.F32Floor -> unop(index, instruction, input, output, ::F32Floor)
    is NumericInstruction.F32Trunc -> unop(index, instruction, input, output, ::F32Trunc)
    is NumericInstruction.F32Sqrt -> unop(index, instruction, input, output, ::F32Sqrt)
    is NumericInstruction.F32Nearest -> unop(index, instruction, input, output, ::F32Nearest)
    is NumericInstruction.F64Add -> nonCommutativeBinop(index, instruction, input, output, ::F64Add)
    is NumericInstruction.F64Sub -> nonCommutativeBinop(index, instruction, input, output, ::F64Sub)
    is NumericInstruction.F64Mul -> nonCommutativeBinop(index, instruction, input, output, ::F64Mul)
    is NumericInstruction.F64Div -> nonCommutativeBinop(index, instruction, input, output, ::F64Div)
    is NumericInstruction.F64Eq -> nonCommutativeBinop(index, instruction, input, output, ::F64Eq)
    is NumericInstruction.F64Ne -> nonCommutativeBinop(index, instruction, input, output, ::F64Ne)
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
    is NumericInstruction.F32Eq -> nonCommutativeBinop(index, instruction, input, output, ::F32Eq)
    is NumericInstruction.F32Ne -> nonCommutativeBinop(index, instruction, input, output, ::F32Ne)
    is NumericInstruction.F32Lt -> nonCommutativeBinop(index, instruction, input, output, ::F32Lt)
    is NumericInstruction.F32Gt -> nonCommutativeBinop(index, instruction, input, output, ::F32Gt)
    is NumericInstruction.F32Le -> nonCommutativeBinop(index, instruction, input, output, ::F32Le)
    is NumericInstruction.F32Ge -> nonCommutativeBinop(index, instruction, input, output, ::F32Ge)
    is NumericInstruction.I64Eq -> nonCommutativeBinop(index, instruction, input, output, ::I64Eq)
    is NumericInstruction.I64Ne -> nonCommutativeBinop(index, instruction, input, output, ::I64Ne)
    is NumericInstruction.I64LtS -> nonCommutativeBinop(index, instruction, input, output, ::I64LtS)
    is NumericInstruction.I64LtU -> nonCommutativeBinop(index, instruction, input, output, ::I64LtU)
    is NumericInstruction.I64GtS -> nonCommutativeBinop(index, instruction, input, output, ::I64GtS)
    is NumericInstruction.I64GtU -> nonCommutativeBinop(index, instruction, input, output, ::I64GtU)
    is NumericInstruction.I64LeS -> nonCommutativeBinop(index, instruction, input, output, ::I64LeS)
    is NumericInstruction.I64LeU -> nonCommutativeBinop(index, instruction, input, output, ::I64LeU)
    is NumericInstruction.I64GeS -> nonCommutativeBinop(index, instruction, input, output, ::I64GeS)
    is NumericInstruction.I64GeU -> nonCommutativeBinop(index, instruction, input, output, ::I64GeU)
    is NumericInstruction.F32ConvertI32S -> unop(index, instruction, input, output, ::F32ConvertI32S)
    is NumericInstruction.F32ConvertI32U -> unop(index, instruction, input, output, ::F32ConvertI32U)
    is NumericInstruction.F32ConvertI64S -> unop(index, instruction, input, output, ::F32ConvertI64S)
    is NumericInstruction.F32ConvertI64U -> unop(index, instruction, input, output, ::F32ConvertI64U)
    is NumericInstruction.F64ConvertI32S -> unop(index, instruction, input, output, ::F64ConvertI32S)
    is NumericInstruction.F64ConvertI32U -> unop(index, instruction, input, output, ::F64ConvertI32U)
    is NumericInstruction.F64ConvertI64S -> unop(index, instruction, input, output, ::F64ConvertI64S)
    is NumericInstruction.F64ConvertI64U -> unop(index, instruction, input, output, ::F64ConvertI64U)
    is NumericInstruction.F32DemoteF64 -> unop(index, instruction, input, output, ::F32DemoteF64)
    is NumericInstruction.F64PromoteF32 -> unop(index, instruction, input, output, ::F64PromoteF32)
    is NumericInstruction.I32WrapI64 -> unop(index, instruction, input, output, ::I32WrapI64)
    is NumericInstruction.I64ExtendI32S -> unop(index, instruction, input, output, ::I64ExtendI32S)
    is NumericInstruction.I64ExtendI32U -> unop(index, instruction, input, output, ::I64ExtendI32U)
    is NumericInstruction.I32TruncF32S -> unop(index, instruction, input, output, ::I32TruncF32S)
    is NumericInstruction.I32TruncF32U -> unop(index, instruction, input, output, ::I32TruncF32U)
    is NumericInstruction.I32TruncF64S -> unop(index, instruction, input, output, ::I32TruncF64S)
    is NumericInstruction.I32TruncF64U -> unop(index, instruction, input, output, ::I32TruncF64U)
    is NumericInstruction.I64TruncF32S -> unop(index, instruction, input, output, ::I64TruncF32S)
    is NumericInstruction.I64TruncF32U -> unop(index, instruction, input, output, ::I64TruncF32U)
    is NumericInstruction.I64TruncF64S -> unop(index, instruction, input, output, ::I64TruncF64S)
    is NumericInstruction.I64TruncF64U -> unop(index, instruction, input, output, ::I64TruncF64U)
    is NumericInstruction.I32TruncSatF32S -> unop(index, instruction, input, output, ::I32TruncSatF32S)
    is NumericInstruction.I32TruncSatF32U -> unop(index, instruction, input, output, ::I32TruncSatF32U)
    is NumericInstruction.I32TruncSatF64S -> unop(index, instruction, input, output, ::I32TruncSatF64S)
    is NumericInstruction.I32TruncSatF64U -> unop(index, instruction, input, output, ::I32TruncSatF64U)
    is NumericInstruction.I64TruncSatF32S -> unop(index, instruction, input, output, ::I64TruncSatF32S)
    is NumericInstruction.I64TruncSatF32U -> unop(index, instruction, input, output, ::I64TruncSatF32U)
    is NumericInstruction.I64TruncSatF64S -> unop(index, instruction, input, output, ::I64TruncSatF64S)
    is NumericInstruction.I64TruncSatF64U -> unop(index, instruction, input, output, ::I64TruncSatF64U)
    is NumericInstruction.I32RemS -> nonCommutativeBinop(index, instruction, input, output, ::I32RemS)
    is NumericInstruction.I32RemU -> nonCommutativeBinop(index, instruction, input, output, ::I32RemU)
    is NumericInstruction.I32Rotl -> nonCommutativeBinop(index, instruction, input, output, ::I32Rotl)
    is NumericInstruction.I32Rotr -> nonCommutativeBinop(index, instruction, input, output, ::I32Rotr)
    is NumericInstruction.I64And -> nonCommutativeBinop(index, instruction, input, output, ::I64And)
    is NumericInstruction.I64Or -> nonCommutativeBinop(index, instruction, input, output, ::I64Or)
    is NumericInstruction.I64Xor -> nonCommutativeBinop(index, instruction, input, output, ::I64Xor)
    is NumericInstruction.I64RemS -> nonCommutativeBinop(index, instruction, input, output, ::I64RemS)
    is NumericInstruction.I64RemU -> nonCommutativeBinop(index, instruction, input, output, ::I64RemU)
    is NumericInstruction.I64Rotl -> nonCommutativeBinop(index, instruction, input, output, ::I64Rotl)
    is NumericInstruction.I64Rotr -> nonCommutativeBinop(index, instruction, input, output, ::I64Rotr)
    is NumericInstruction.I64Shl -> nonCommutativeBinop(index, instruction, input, output, ::I64Shl)
    is NumericInstruction.I64ShrS -> nonCommutativeBinop(index, instruction, input, output, ::I64ShrS)
    is NumericInstruction.I64ShrU -> nonCommutativeBinop(index, instruction, input, output, ::I64ShrU)
    is NumericInstruction.F32Min -> nonCommutativeBinop(index, instruction, input, output, ::F32Min)
    is NumericInstruction.F32Max -> nonCommutativeBinop(index, instruction, input, output, ::F32Max)
    is NumericInstruction.F32Copysign -> nonCommutativeBinop(index, instruction, input, output, ::F32Copysign)
    is NumericInstruction.F64Min -> nonCommutativeBinop(index, instruction, input, output, ::F64Min)
    is NumericInstruction.F64Max -> nonCommutativeBinop(index, instruction, input, output, ::F64Max)
    is NumericInstruction.F64Copysign -> nonCommutativeBinop(index, instruction, input, output, ::F64Copysign)
    else -> {
        output.add(instruction)
        index
    }
}
