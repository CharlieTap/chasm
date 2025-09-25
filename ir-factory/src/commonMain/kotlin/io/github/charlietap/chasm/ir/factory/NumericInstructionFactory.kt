package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ir.instruction.NumericInstruction.F32Const
import io.github.charlietap.chasm.ir.instruction.NumericInstruction.F64Const
import io.github.charlietap.chasm.ir.instruction.NumericInstruction.I32Const
import io.github.charlietap.chasm.ir.instruction.NumericInstruction.I64Const
import io.github.charlietap.chasm.ir.instruction.NumericInstruction as IRNumericInstruction

internal fun NumericInstructionFactory(
    instruction: NumericInstruction,
): IRNumericInstruction {
    return when (instruction) {
        is NumericInstruction.I32Const -> I32Const(
            value = instruction.value,
        )

        is NumericInstruction.I64Const -> I64Const(
            value = instruction.value,
        )

        is NumericInstruction.F32Const -> F32Const(
            value = instruction.value,
            bits = instruction.bits,
        )

        is NumericInstruction.F64Const -> F64Const(
            value = instruction.value,
            bits = instruction.bits,
        )

        NumericInstruction.I32Eqz -> IRNumericInstruction.I32Eqz
        NumericInstruction.I32Eq -> IRNumericInstruction.I32Eq
        NumericInstruction.I32Ne -> IRNumericInstruction.I32Ne
        NumericInstruction.I32LtS -> IRNumericInstruction.I32LtS
        NumericInstruction.I32LtU -> IRNumericInstruction.I32LtU
        NumericInstruction.I32GtS -> IRNumericInstruction.I32GtS
        NumericInstruction.I32GtU -> IRNumericInstruction.I32GtU
        NumericInstruction.I32LeS -> IRNumericInstruction.I32LeS
        NumericInstruction.I32LeU -> IRNumericInstruction.I32LeU
        NumericInstruction.I32GeS -> IRNumericInstruction.I32GeS
        NumericInstruction.I32GeU -> IRNumericInstruction.I32GeU
        NumericInstruction.I64Eqz -> IRNumericInstruction.I64Eqz
        NumericInstruction.I64Eq -> IRNumericInstruction.I64Eq
        NumericInstruction.I64Ne -> IRNumericInstruction.I64Ne
        NumericInstruction.I64LtS -> IRNumericInstruction.I64LtS
        NumericInstruction.I64LtU -> IRNumericInstruction.I64LtU
        NumericInstruction.I64GtS -> IRNumericInstruction.I64GtS
        NumericInstruction.I64GtU -> IRNumericInstruction.I64GtU
        NumericInstruction.I64LeS -> IRNumericInstruction.I64LeS
        NumericInstruction.I64LeU -> IRNumericInstruction.I64LeU
        NumericInstruction.I64GeS -> IRNumericInstruction.I64GeS
        NumericInstruction.I64GeU -> IRNumericInstruction.I64GeU
        NumericInstruction.F32Eq -> IRNumericInstruction.F32Eq
        NumericInstruction.F32Ne -> IRNumericInstruction.F32Ne
        NumericInstruction.F32Lt -> IRNumericInstruction.F32Lt
        NumericInstruction.F32Gt -> IRNumericInstruction.F32Gt
        NumericInstruction.F32Le -> IRNumericInstruction.F32Le
        NumericInstruction.F32Ge -> IRNumericInstruction.F32Ge
        NumericInstruction.F64Eq -> IRNumericInstruction.F64Eq
        NumericInstruction.F64Ne -> IRNumericInstruction.F64Ne
        NumericInstruction.F64Lt -> IRNumericInstruction.F64Lt
        NumericInstruction.F64Gt -> IRNumericInstruction.F64Gt
        NumericInstruction.F64Le -> IRNumericInstruction.F64Le
        NumericInstruction.F64Ge -> IRNumericInstruction.F64Ge
        NumericInstruction.I32Clz -> IRNumericInstruction.I32Clz
        NumericInstruction.I32Ctz -> IRNumericInstruction.I32Ctz
        NumericInstruction.I32Popcnt -> IRNumericInstruction.I32Popcnt
        NumericInstruction.I32Add -> IRNumericInstruction.I32Add
        NumericInstruction.I32Sub -> IRNumericInstruction.I32Sub
        NumericInstruction.I32Mul -> IRNumericInstruction.I32Mul
        NumericInstruction.I32DivS -> IRNumericInstruction.I32DivS
        NumericInstruction.I32DivU -> IRNumericInstruction.I32DivU
        NumericInstruction.I32RemS -> IRNumericInstruction.I32RemS
        NumericInstruction.I32RemU -> IRNumericInstruction.I32RemU
        NumericInstruction.I32And -> IRNumericInstruction.I32And
        NumericInstruction.I32Or -> IRNumericInstruction.I32Or
        NumericInstruction.I32Xor -> IRNumericInstruction.I32Xor
        NumericInstruction.I32Shl -> IRNumericInstruction.I32Shl
        NumericInstruction.I32ShrS -> IRNumericInstruction.I32ShrS
        NumericInstruction.I32ShrU -> IRNumericInstruction.I32ShrU
        NumericInstruction.I32Rotl -> IRNumericInstruction.I32Rotl
        NumericInstruction.I32Rotr -> IRNumericInstruction.I32Rotr
        NumericInstruction.I64Clz -> IRNumericInstruction.I64Clz
        NumericInstruction.I64Ctz -> IRNumericInstruction.I64Ctz
        NumericInstruction.I64Popcnt -> IRNumericInstruction.I64Popcnt
        NumericInstruction.I64Add -> IRNumericInstruction.I64Add
        NumericInstruction.I64Sub -> IRNumericInstruction.I64Sub
        NumericInstruction.I64Mul -> IRNumericInstruction.I64Mul
        NumericInstruction.I64DivS -> IRNumericInstruction.I64DivS
        NumericInstruction.I64DivU -> IRNumericInstruction.I64DivU
        NumericInstruction.I64RemS -> IRNumericInstruction.I64RemS
        NumericInstruction.I64RemU -> IRNumericInstruction.I64RemU
        NumericInstruction.I64And -> IRNumericInstruction.I64And
        NumericInstruction.I64Or -> IRNumericInstruction.I64Or
        NumericInstruction.I64Xor -> IRNumericInstruction.I64Xor
        NumericInstruction.I64Shl -> IRNumericInstruction.I64Shl
        NumericInstruction.I64ShrS -> IRNumericInstruction.I64ShrS
        NumericInstruction.I64ShrU -> IRNumericInstruction.I64ShrU
        NumericInstruction.I64Rotl -> IRNumericInstruction.I64Rotl
        NumericInstruction.I64Rotr -> IRNumericInstruction.I64Rotr
        NumericInstruction.F32Abs -> IRNumericInstruction.F32Abs
        NumericInstruction.F32Neg -> IRNumericInstruction.F32Neg
        NumericInstruction.F32Ceil -> IRNumericInstruction.F32Ceil
        NumericInstruction.F32Floor -> IRNumericInstruction.F32Floor
        NumericInstruction.F32Trunc -> IRNumericInstruction.F32Trunc
        NumericInstruction.F32Nearest -> IRNumericInstruction.F32Nearest
        NumericInstruction.F32Sqrt -> IRNumericInstruction.F32Sqrt
        NumericInstruction.F32Add -> IRNumericInstruction.F32Add
        NumericInstruction.F32Sub -> IRNumericInstruction.F32Sub
        NumericInstruction.F32Mul -> IRNumericInstruction.F32Mul
        NumericInstruction.F32Div -> IRNumericInstruction.F32Div
        NumericInstruction.F32Min -> IRNumericInstruction.F32Min
        NumericInstruction.F32Max -> IRNumericInstruction.F32Max
        NumericInstruction.F32Copysign -> IRNumericInstruction.F32Copysign
        NumericInstruction.F64Abs -> IRNumericInstruction.F64Abs
        NumericInstruction.F64Neg -> IRNumericInstruction.F64Neg
        NumericInstruction.F64Ceil -> IRNumericInstruction.F64Ceil
        NumericInstruction.F64Floor -> IRNumericInstruction.F64Floor
        NumericInstruction.F64Trunc -> IRNumericInstruction.F64Trunc
        NumericInstruction.F64Nearest -> IRNumericInstruction.F64Nearest
        NumericInstruction.F64Sqrt -> IRNumericInstruction.F64Sqrt
        NumericInstruction.F64Add -> IRNumericInstruction.F64Add
        NumericInstruction.F64Sub -> IRNumericInstruction.F64Sub
        NumericInstruction.F64Mul -> IRNumericInstruction.F64Mul
        NumericInstruction.F64Div -> IRNumericInstruction.F64Div
        NumericInstruction.F64Min -> IRNumericInstruction.F64Min
        NumericInstruction.F64Max -> IRNumericInstruction.F64Max
        NumericInstruction.F64Copysign -> IRNumericInstruction.F64Copysign
        NumericInstruction.F32ConvertI32S -> IRNumericInstruction.F32ConvertI32S
        NumericInstruction.F32ConvertI32U -> IRNumericInstruction.F32ConvertI32U
        NumericInstruction.F32ConvertI64S -> IRNumericInstruction.F32ConvertI64S
        NumericInstruction.F32ConvertI64U -> IRNumericInstruction.F32ConvertI64U
        NumericInstruction.F32DemoteF64 -> IRNumericInstruction.F32DemoteF64
        NumericInstruction.F32ReinterpretI32 -> IRNumericInstruction.F32ReinterpretI32
        NumericInstruction.F64ConvertI32S -> IRNumericInstruction.F64ConvertI32S
        NumericInstruction.F64ConvertI32U -> IRNumericInstruction.F64ConvertI32U
        NumericInstruction.F64ConvertI64S -> IRNumericInstruction.F64ConvertI64S
        NumericInstruction.F64ConvertI64U -> IRNumericInstruction.F64ConvertI64U
        NumericInstruction.F64PromoteF32 -> IRNumericInstruction.F64PromoteF32
        NumericInstruction.F64ReinterpretI64 -> IRNumericInstruction.F64ReinterpretI64
        NumericInstruction.I32Extend16S -> IRNumericInstruction.I32Extend16S
        NumericInstruction.I32Extend8S -> IRNumericInstruction.I32Extend8S
        NumericInstruction.I32ReinterpretF32 -> IRNumericInstruction.I32ReinterpretF32
        NumericInstruction.I32TruncF32S -> IRNumericInstruction.I32TruncF32S
        NumericInstruction.I32TruncF32U -> IRNumericInstruction.I32TruncF32U
        NumericInstruction.I32TruncF64S -> IRNumericInstruction.I32TruncF64S
        NumericInstruction.I32TruncF64U -> IRNumericInstruction.I32TruncF64U
        NumericInstruction.I32TruncSatF32S -> IRNumericInstruction.I32TruncSatF32S
        NumericInstruction.I32TruncSatF32U -> IRNumericInstruction.I32TruncSatF32U
        NumericInstruction.I32TruncSatF64S -> IRNumericInstruction.I32TruncSatF64S
        NumericInstruction.I32TruncSatF64U -> IRNumericInstruction.I32TruncSatF64U
        NumericInstruction.I32WrapI64 -> IRNumericInstruction.I32WrapI64
        NumericInstruction.I64Extend16S -> IRNumericInstruction.I64Extend16S
        NumericInstruction.I64Extend32S -> IRNumericInstruction.I64Extend32S
        NumericInstruction.I64Extend8S -> IRNumericInstruction.I64Extend8S
        NumericInstruction.I64ExtendI32S -> IRNumericInstruction.I64ExtendI32S
        NumericInstruction.I64ExtendI32U -> IRNumericInstruction.I64ExtendI32U
        NumericInstruction.I64ReinterpretF64 -> IRNumericInstruction.I64ReinterpretF64
        NumericInstruction.I64TruncF32S -> IRNumericInstruction.I64TruncF32S
        NumericInstruction.I64TruncF32U -> IRNumericInstruction.I64TruncF32U
        NumericInstruction.I64TruncF64S -> IRNumericInstruction.I64TruncF64S
        NumericInstruction.I64TruncF64U -> IRNumericInstruction.I64TruncF64U
        NumericInstruction.I64TruncSatF32S -> IRNumericInstruction.I64TruncSatF32S
        NumericInstruction.I64TruncSatF32U -> IRNumericInstruction.I64TruncSatF32U
        NumericInstruction.I64TruncSatF64S -> IRNumericInstruction.I64TruncSatF64S
        NumericInstruction.I64TruncSatF64U -> IRNumericInstruction.I64TruncSatF64U
        NumericInstruction.I64Add128 -> TODO()
        NumericInstruction.I64MulWideS -> TODO()
        NumericInstruction.I64MulWideU -> TODO()
        NumericInstruction.I64Sub128 -> TODO()
    }
}
