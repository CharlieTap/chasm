package io.github.charlietap.chasm.decoder.instruction.numeric

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.decoder.instruction.F32_ABS
import io.github.charlietap.chasm.decoder.instruction.F32_ADD
import io.github.charlietap.chasm.decoder.instruction.F32_CEIL
import io.github.charlietap.chasm.decoder.instruction.F32_CONST
import io.github.charlietap.chasm.decoder.instruction.F32_CONVERT_I32_S
import io.github.charlietap.chasm.decoder.instruction.F32_CONVERT_I32_U
import io.github.charlietap.chasm.decoder.instruction.F32_CONVERT_I64_S
import io.github.charlietap.chasm.decoder.instruction.F32_CONVERT_I64_U
import io.github.charlietap.chasm.decoder.instruction.F32_COPYSIGN
import io.github.charlietap.chasm.decoder.instruction.F32_DEMOTE_F64
import io.github.charlietap.chasm.decoder.instruction.F32_DIV
import io.github.charlietap.chasm.decoder.instruction.F32_EQ
import io.github.charlietap.chasm.decoder.instruction.F32_FLOOR
import io.github.charlietap.chasm.decoder.instruction.F32_GE
import io.github.charlietap.chasm.decoder.instruction.F32_GT
import io.github.charlietap.chasm.decoder.instruction.F32_LE
import io.github.charlietap.chasm.decoder.instruction.F32_LT
import io.github.charlietap.chasm.decoder.instruction.F32_MAX
import io.github.charlietap.chasm.decoder.instruction.F32_MIN
import io.github.charlietap.chasm.decoder.instruction.F32_MUL
import io.github.charlietap.chasm.decoder.instruction.F32_NE
import io.github.charlietap.chasm.decoder.instruction.F32_NEAREST
import io.github.charlietap.chasm.decoder.instruction.F32_NEG
import io.github.charlietap.chasm.decoder.instruction.F32_REINTERPRET_I32
import io.github.charlietap.chasm.decoder.instruction.F32_SQRT
import io.github.charlietap.chasm.decoder.instruction.F32_SUB
import io.github.charlietap.chasm.decoder.instruction.F32_TRUNC
import io.github.charlietap.chasm.decoder.instruction.F64_ABS
import io.github.charlietap.chasm.decoder.instruction.F64_ADD
import io.github.charlietap.chasm.decoder.instruction.F64_CEIL
import io.github.charlietap.chasm.decoder.instruction.F64_CONST
import io.github.charlietap.chasm.decoder.instruction.F64_CONVERT_I32_S
import io.github.charlietap.chasm.decoder.instruction.F64_CONVERT_I32_U
import io.github.charlietap.chasm.decoder.instruction.F64_CONVERT_I64_S
import io.github.charlietap.chasm.decoder.instruction.F64_CONVERT_I64_U
import io.github.charlietap.chasm.decoder.instruction.F64_COPYSIGN
import io.github.charlietap.chasm.decoder.instruction.F64_DIV
import io.github.charlietap.chasm.decoder.instruction.F64_EQ
import io.github.charlietap.chasm.decoder.instruction.F64_FLOOR
import io.github.charlietap.chasm.decoder.instruction.F64_GE
import io.github.charlietap.chasm.decoder.instruction.F64_GT
import io.github.charlietap.chasm.decoder.instruction.F64_LE
import io.github.charlietap.chasm.decoder.instruction.F64_LT
import io.github.charlietap.chasm.decoder.instruction.F64_MAX
import io.github.charlietap.chasm.decoder.instruction.F64_MIN
import io.github.charlietap.chasm.decoder.instruction.F64_MUL
import io.github.charlietap.chasm.decoder.instruction.F64_NE
import io.github.charlietap.chasm.decoder.instruction.F64_NEAREST
import io.github.charlietap.chasm.decoder.instruction.F64_NEG
import io.github.charlietap.chasm.decoder.instruction.F64_PROMOTE_F32
import io.github.charlietap.chasm.decoder.instruction.F64_REINTERPRET_I64
import io.github.charlietap.chasm.decoder.instruction.F64_SQRT
import io.github.charlietap.chasm.decoder.instruction.F64_SUB
import io.github.charlietap.chasm.decoder.instruction.F64_TRUNC
import io.github.charlietap.chasm.decoder.instruction.I32_ADD
import io.github.charlietap.chasm.decoder.instruction.I32_AND
import io.github.charlietap.chasm.decoder.instruction.I32_CLZ
import io.github.charlietap.chasm.decoder.instruction.I32_CONST
import io.github.charlietap.chasm.decoder.instruction.I32_CTZ
import io.github.charlietap.chasm.decoder.instruction.I32_DIV_S
import io.github.charlietap.chasm.decoder.instruction.I32_DIV_U
import io.github.charlietap.chasm.decoder.instruction.I32_EQ
import io.github.charlietap.chasm.decoder.instruction.I32_EQZ
import io.github.charlietap.chasm.decoder.instruction.I32_EXTEND16_S
import io.github.charlietap.chasm.decoder.instruction.I32_EXTEND8_S
import io.github.charlietap.chasm.decoder.instruction.I32_GE_S
import io.github.charlietap.chasm.decoder.instruction.I32_GE_U
import io.github.charlietap.chasm.decoder.instruction.I32_GT_S
import io.github.charlietap.chasm.decoder.instruction.I32_GT_U
import io.github.charlietap.chasm.decoder.instruction.I32_LE_S
import io.github.charlietap.chasm.decoder.instruction.I32_LE_U
import io.github.charlietap.chasm.decoder.instruction.I32_LT_S
import io.github.charlietap.chasm.decoder.instruction.I32_LT_U
import io.github.charlietap.chasm.decoder.instruction.I32_MUL
import io.github.charlietap.chasm.decoder.instruction.I32_NE
import io.github.charlietap.chasm.decoder.instruction.I32_OR
import io.github.charlietap.chasm.decoder.instruction.I32_POPCNT
import io.github.charlietap.chasm.decoder.instruction.I32_REINTERPRET_F32
import io.github.charlietap.chasm.decoder.instruction.I32_REM_S
import io.github.charlietap.chasm.decoder.instruction.I32_REM_U
import io.github.charlietap.chasm.decoder.instruction.I32_ROTL
import io.github.charlietap.chasm.decoder.instruction.I32_ROTR
import io.github.charlietap.chasm.decoder.instruction.I32_SHL
import io.github.charlietap.chasm.decoder.instruction.I32_SHR_S
import io.github.charlietap.chasm.decoder.instruction.I32_SHR_U
import io.github.charlietap.chasm.decoder.instruction.I32_SUB
import io.github.charlietap.chasm.decoder.instruction.I32_TRUNC_F32_S
import io.github.charlietap.chasm.decoder.instruction.I32_TRUNC_F32_U
import io.github.charlietap.chasm.decoder.instruction.I32_TRUNC_F64_S
import io.github.charlietap.chasm.decoder.instruction.I32_TRUNC_F64_U
import io.github.charlietap.chasm.decoder.instruction.I32_WRAP_I64
import io.github.charlietap.chasm.decoder.instruction.I32_XOR
import io.github.charlietap.chasm.decoder.instruction.I64_ADD
import io.github.charlietap.chasm.decoder.instruction.I64_AND
import io.github.charlietap.chasm.decoder.instruction.I64_CLZ
import io.github.charlietap.chasm.decoder.instruction.I64_CONST
import io.github.charlietap.chasm.decoder.instruction.I64_CTZ
import io.github.charlietap.chasm.decoder.instruction.I64_DIV_S
import io.github.charlietap.chasm.decoder.instruction.I64_DIV_U
import io.github.charlietap.chasm.decoder.instruction.I64_EQ
import io.github.charlietap.chasm.decoder.instruction.I64_EQZ
import io.github.charlietap.chasm.decoder.instruction.I64_EXTEND16_S
import io.github.charlietap.chasm.decoder.instruction.I64_EXTEND32_S
import io.github.charlietap.chasm.decoder.instruction.I64_EXTEND8_S
import io.github.charlietap.chasm.decoder.instruction.I64_EXTEND_I32_S
import io.github.charlietap.chasm.decoder.instruction.I64_EXTEND_I32_U
import io.github.charlietap.chasm.decoder.instruction.I64_GE_S
import io.github.charlietap.chasm.decoder.instruction.I64_GE_U
import io.github.charlietap.chasm.decoder.instruction.I64_GT_S
import io.github.charlietap.chasm.decoder.instruction.I64_GT_U
import io.github.charlietap.chasm.decoder.instruction.I64_LE_S
import io.github.charlietap.chasm.decoder.instruction.I64_LE_U
import io.github.charlietap.chasm.decoder.instruction.I64_LT_S
import io.github.charlietap.chasm.decoder.instruction.I64_LT_U
import io.github.charlietap.chasm.decoder.instruction.I64_MUL
import io.github.charlietap.chasm.decoder.instruction.I64_NE
import io.github.charlietap.chasm.decoder.instruction.I64_OR
import io.github.charlietap.chasm.decoder.instruction.I64_POPCNT
import io.github.charlietap.chasm.decoder.instruction.I64_REINTERPRET_F64
import io.github.charlietap.chasm.decoder.instruction.I64_REM_S
import io.github.charlietap.chasm.decoder.instruction.I64_REM_U
import io.github.charlietap.chasm.decoder.instruction.I64_ROTL
import io.github.charlietap.chasm.decoder.instruction.I64_ROTR
import io.github.charlietap.chasm.decoder.instruction.I64_SHL
import io.github.charlietap.chasm.decoder.instruction.I64_SHR_S
import io.github.charlietap.chasm.decoder.instruction.I64_SHR_U
import io.github.charlietap.chasm.decoder.instruction.I64_SUB
import io.github.charlietap.chasm.decoder.instruction.I64_TRUNC_F32_S
import io.github.charlietap.chasm.decoder.instruction.I64_TRUNC_F32_U
import io.github.charlietap.chasm.decoder.instruction.I64_TRUNC_F64_S
import io.github.charlietap.chasm.decoder.instruction.I64_TRUNC_F64_U
import io.github.charlietap.chasm.decoder.instruction.I64_XOR
import io.github.charlietap.chasm.error.InstructionDecodeError
import io.github.charlietap.chasm.error.WasmDecodeError
import io.github.charlietap.chasm.reader.WasmBinaryReader

fun BinaryNumericInstructionDecoder(
    reader: WasmBinaryReader,
    opcode: UByte,
): Result<Instruction, WasmDecodeError> = binding {
    when (opcode) {
        I32_CONST -> {
            val const = reader.int().bind()
            NumericInstruction.I32Const(const)
        }
        I64_CONST -> {
            val const = reader.long().bind()
            NumericInstruction.I64Const(const)
        }
        F32_CONST -> {
            val const = reader.float().bind()
            NumericInstruction.F32Const(const)
        }
        F64_CONST -> {
            val const = reader.double().bind()
            NumericInstruction.F64Const(const)
        }
        I32_EQZ -> NumericInstruction.I32Eqz
        I32_EQ -> NumericInstruction.I32Eq
        I32_NE -> NumericInstruction.I32Ne
        I32_LT_S -> NumericInstruction.I32LtS
        I32_LT_U -> NumericInstruction.I32LtU
        I32_GT_S -> NumericInstruction.I32GtS
        I32_GT_U -> NumericInstruction.I32GtU
        I32_LE_S -> NumericInstruction.I32LeS
        I32_LE_U -> NumericInstruction.I32LeU
        I32_GE_S -> NumericInstruction.I32GeS
        I32_GE_U -> NumericInstruction.I32GeU

        I64_EQZ -> NumericInstruction.I64Eqz
        I64_EQ -> NumericInstruction.I64Eq
        I64_NE -> NumericInstruction.I64Ne
        I64_LT_S -> NumericInstruction.I64LtS
        I64_LT_U -> NumericInstruction.I64LtU
        I64_GT_S -> NumericInstruction.I64GtS
        I64_GT_U -> NumericInstruction.I64GtU
        I64_LE_S -> NumericInstruction.I64LeS
        I64_LE_U -> NumericInstruction.I64LeU
        I64_GE_S -> NumericInstruction.I64GeS
        I64_GE_U -> NumericInstruction.I64GeU

        F32_EQ -> NumericInstruction.F32Eq
        F32_NE -> NumericInstruction.F32Ne
        F32_LT -> NumericInstruction.F32Lt
        F32_GT -> NumericInstruction.F32Gt
        F32_LE -> NumericInstruction.F32Le
        F32_GE -> NumericInstruction.F32Ge

        F64_EQ -> NumericInstruction.F64Eq
        F64_NE -> NumericInstruction.F64Ne
        F64_LT -> NumericInstruction.F64Lt
        F64_GT -> NumericInstruction.F64Gt
        F64_LE -> NumericInstruction.F64Le
        F64_GE -> NumericInstruction.F64Ge

        I32_CLZ -> NumericInstruction.I32Clz
        I32_CTZ -> NumericInstruction.I32Ctz
        I32_POPCNT -> NumericInstruction.I32Popcnt
        I32_ADD -> NumericInstruction.I32Add
        I32_SUB -> NumericInstruction.I32Sub
        I32_MUL -> NumericInstruction.I32Mul
        I32_DIV_S -> NumericInstruction.I32DivS
        I32_DIV_U -> NumericInstruction.I32DivU
        I32_REM_S -> NumericInstruction.I32RemS
        I32_REM_U -> NumericInstruction.I32RemU
        I32_AND -> NumericInstruction.I32And
        I32_OR -> NumericInstruction.I32Or
        I32_XOR -> NumericInstruction.I32Xor
        I32_SHL -> NumericInstruction.I32Shl
        I32_SHR_S -> NumericInstruction.I32ShrS
        I32_SHR_U -> NumericInstruction.I32ShrU
        I32_ROTL -> NumericInstruction.I32Rotl
        I32_ROTR -> NumericInstruction.I32Rotr

        I64_CLZ -> NumericInstruction.I64Clz
        I64_CTZ -> NumericInstruction.I64Ctz
        I64_POPCNT -> NumericInstruction.I64Popcnt
        I64_ADD -> NumericInstruction.I64Add
        I64_SUB -> NumericInstruction.I64Sub
        I64_MUL -> NumericInstruction.I64Mul
        I64_DIV_S -> NumericInstruction.I64DivS
        I64_DIV_U -> NumericInstruction.I64DivU
        I64_REM_S -> NumericInstruction.I64RemS
        I64_REM_U -> NumericInstruction.I64RemU
        I64_AND -> NumericInstruction.I64And
        I64_OR -> NumericInstruction.I64Or
        I64_XOR -> NumericInstruction.I64Xor
        I64_SHL -> NumericInstruction.I64Shl
        I64_SHR_S -> NumericInstruction.I64ShrS
        I64_SHR_U -> NumericInstruction.I64ShrU
        I64_ROTL -> NumericInstruction.I64Rotl
        I64_ROTR -> NumericInstruction.I64Rotr

        F32_ABS -> NumericInstruction.F32Abs
        F32_NEG -> NumericInstruction.F32Neg
        F32_CEIL -> NumericInstruction.F32Ceil
        F32_FLOOR -> NumericInstruction.F32Floor
        F32_TRUNC -> NumericInstruction.F32Trunc
        F32_NEAREST -> NumericInstruction.F32Nearest
        F32_SQRT -> NumericInstruction.F32Sqrt
        F32_ADD -> NumericInstruction.F32Add
        F32_SUB -> NumericInstruction.F32Sub
        F32_MUL -> NumericInstruction.F32Mul
        F32_DIV -> NumericInstruction.F32Div
        F32_MIN -> NumericInstruction.F32Min
        F32_MAX -> NumericInstruction.F32Max
        F32_COPYSIGN -> NumericInstruction.F32Copysign

        F64_ABS -> NumericInstruction.F64Abs
        F64_NEG -> NumericInstruction.F64Neg
        F64_CEIL -> NumericInstruction.F64Ceil
        F64_FLOOR -> NumericInstruction.F64Floor
        F64_TRUNC -> NumericInstruction.F64Trunc
        F64_NEAREST -> NumericInstruction.F64Nearest
        F64_SQRT -> NumericInstruction.F64Sqrt
        F64_ADD -> NumericInstruction.F64Add
        F64_SUB -> NumericInstruction.F64Sub
        F64_MUL -> NumericInstruction.F64Mul
        F64_DIV -> NumericInstruction.F64Div
        F64_MIN -> NumericInstruction.F64Min
        F64_MAX -> NumericInstruction.F64Max
        F64_COPYSIGN -> NumericInstruction.F64Copysign

        I32_WRAP_I64 -> NumericInstruction.I32WrapI64
        I32_TRUNC_F32_S -> NumericInstruction.I32TruncF32S
        I32_TRUNC_F32_U -> NumericInstruction.I32TruncF32U
        I32_TRUNC_F64_S -> NumericInstruction.I32TruncF64S
        I32_TRUNC_F64_U -> NumericInstruction.I32TruncF64U
        I64_EXTEND_I32_S -> NumericInstruction.I64ExtendI32S
        I64_EXTEND_I32_U -> NumericInstruction.I64ExtendI32U
        I64_TRUNC_F32_S -> NumericInstruction.I64TruncF32S
        I64_TRUNC_F32_U -> NumericInstruction.I64TruncF32U
        I64_TRUNC_F64_S -> NumericInstruction.I64TruncF64S
        I64_TRUNC_F64_U -> NumericInstruction.I64TruncF64U
        F32_CONVERT_I32_S -> NumericInstruction.F32ConvertI32S
        F32_CONVERT_I32_U -> NumericInstruction.F32ConvertI32U
        F32_CONVERT_I64_S -> NumericInstruction.F32ConvertI64S
        F32_CONVERT_I64_U -> NumericInstruction.F32ConvertI64U
        F32_DEMOTE_F64 -> NumericInstruction.F32DemoteF64
        F64_CONVERT_I32_S -> NumericInstruction.F64ConvertI32S
        F64_CONVERT_I32_U -> NumericInstruction.F64ConvertI32U
        F64_CONVERT_I64_S -> NumericInstruction.F64ConvertI64S
        F64_CONVERT_I64_U -> NumericInstruction.F64ConvertI64U
        F64_PROMOTE_F32 -> NumericInstruction.F64PromoteF32
        I32_REINTERPRET_F32 -> NumericInstruction.I32ReinterpretF32
        I64_REINTERPRET_F64 -> NumericInstruction.I64ReinterpretF64
        F32_REINTERPRET_I32 -> NumericInstruction.F32ReinterpretI32
        F64_REINTERPRET_I64 -> NumericInstruction.F64ReinterpretI64

        I32_EXTEND8_S -> NumericInstruction.I32Extend8S
        I32_EXTEND16_S -> NumericInstruction.I32Extend16S
        I64_EXTEND8_S -> NumericInstruction.I64Extend8S
        I64_EXTEND16_S -> NumericInstruction.I64Extend16S
        I64_EXTEND32_S -> NumericInstruction.I64Extend32S

        else -> Err(InstructionDecodeError.InvalidNumericInstruction(opcode)).bind<Instruction>()
    }
}
