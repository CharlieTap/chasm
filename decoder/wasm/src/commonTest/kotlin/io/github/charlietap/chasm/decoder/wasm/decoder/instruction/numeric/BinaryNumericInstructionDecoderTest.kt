package io.github.charlietap.chasm.decoder.wasm.decoder.instruction.numeric

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_ABS
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_ADD
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_CEIL
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_CONST
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_CONVERT_I32_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_CONVERT_I32_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_CONVERT_I64_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_CONVERT_I64_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_COPYSIGN
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_DEMOTE_F64
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_DIV
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_EQ
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_FLOOR
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_GE
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_GT
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_LE
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_LT
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_MAX
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_MIN
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_MUL
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_NE
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_NEAREST
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_NEG
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_REINTERPRET_I32
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_SQRT
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_SUB
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F32_TRUNC
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_ABS
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_ADD
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_CEIL
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_CONST
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_CONVERT_I32_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_CONVERT_I32_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_CONVERT_I64_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_CONVERT_I64_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_COPYSIGN
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_DIV
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_EQ
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_FLOOR
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_GE
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_GT
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_LE
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_LT
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_MAX
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_MIN
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_MUL
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_NE
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_NEAREST
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_NEG
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_PROMOTE_F32
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_REINTERPRET_I64
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_SQRT
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_SUB
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.F64_TRUNC
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_ADD
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_AND
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_CLZ
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_CONST
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_CTZ
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_DIV_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_DIV_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_EQ
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_EQZ
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_EXTEND16_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_EXTEND8_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_GE_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_GE_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_GT_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_GT_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_LE_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_LE_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_LT_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_LT_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_MUL
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_NE
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_OR
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_POPCNT
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_REINTERPRET_F32
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_REM_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_REM_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_ROTL
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_ROTR
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_SHL
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_SHR_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_SHR_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_SUB
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_TRUNC_F32_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_TRUNC_F32_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_TRUNC_F64_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_TRUNC_F64_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_WRAP_I64
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I32_XOR
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_ADD
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_AND
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_CLZ
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_CONST
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_CTZ
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_DIV_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_DIV_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_EQ
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_EQZ
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_EXTEND16_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_EXTEND32_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_EXTEND8_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_EXTEND_I32_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_EXTEND_I32_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_GE_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_GE_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_GT_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_GT_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_LE_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_LE_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_LT_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_LT_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_MUL
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_NE
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_OR
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_POPCNT
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_REINTERPRET_F64
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_REM_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_REM_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_ROTL
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_ROTR
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_SHL
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_SHR_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_SHR_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_SUB
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_TRUNC_F32_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_TRUNC_F32_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_TRUNC_F64_S
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_TRUNC_F64_U
import io.github.charlietap.chasm.decoder.wasm.decoder.instruction.I64_XOR
import io.github.charlietap.chasm.decoder.wasm.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.FakeDoubleReader
import io.github.charlietap.chasm.decoder.wasm.reader.FakeFloatReader
import io.github.charlietap.chasm.decoder.wasm.reader.FakeIntReader
import io.github.charlietap.chasm.decoder.wasm.reader.FakeLongReader
import io.github.charlietap.chasm.decoder.wasm.reader.FakeWasmBinaryReader
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryNumericInstructionDecoderTest {

    @Test
    fun `can decode an I32 const instruction`() {

        val opcode = I32_CONST
        val reader = FakeIntReader {
            Ok(117)
        }
        val expected = Ok(NumericInstruction.I32Const(117))

        val actual = BinaryNumericInstructionDecoder(reader, opcode)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an I64 const instruction`() {

        val opcode = I64_CONST
        val reader = FakeLongReader {
            Ok(117)
        }
        val expected = Ok(NumericInstruction.I64Const(117))

        val actual = BinaryNumericInstructionDecoder(reader, opcode)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an F32 const instruction`() {

        val opcode = F32_CONST
        val reader = FakeFloatReader {
            Ok(117f)
        }
        val expected = Ok(NumericInstruction.F32Const(117f))

        val actual = BinaryNumericInstructionDecoder(reader, opcode)

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode an F64 const instruction`() {

        val opcode = F64_CONST
        val reader = FakeDoubleReader {
            Ok(117.0)
        }
        val expected = Ok(NumericInstruction.F64Const(117.0))

        val actual = BinaryNumericInstructionDecoder(reader, opcode)

        assertEquals(expected, actual)
    }

    @Test
    fun `can numeric instructions`() {

        val opcodeInstructionMap = mapOf(
            I32_EQZ to NumericInstruction.I32Eqz,
            I32_EQ to NumericInstruction.I32Eq,
            I32_NE to NumericInstruction.I32Ne,
            I32_LT_S to NumericInstruction.I32LtS,
            I32_LT_U to NumericInstruction.I32LtU,
            I32_GT_S to NumericInstruction.I32GtS,
            I32_GT_U to NumericInstruction.I32GtU,
            I32_LE_S to NumericInstruction.I32LeS,
            I32_LE_U to NumericInstruction.I32LeU,
            I32_GE_S to NumericInstruction.I32GeS,
            I32_GE_U to NumericInstruction.I32GeU,
            I64_EQZ to NumericInstruction.I64Eqz,
            I64_EQ to NumericInstruction.I64Eq,
            I64_NE to NumericInstruction.I64Ne,
            I64_LT_S to NumericInstruction.I64LtS,
            I64_LT_U to NumericInstruction.I64LtU,
            I64_GT_S to NumericInstruction.I64GtS,
            I64_GT_U to NumericInstruction.I64GtU,
            I64_LE_S to NumericInstruction.I64LeS,
            I64_LE_U to NumericInstruction.I64LeU,
            I64_GE_S to NumericInstruction.I64GeS,
            I64_GE_U to NumericInstruction.I64GeU,
            F32_EQ to NumericInstruction.F32Eq,
            F32_NE to NumericInstruction.F32Ne,
            F32_LT to NumericInstruction.F32Lt,
            F32_GT to NumericInstruction.F32Gt,
            F32_LE to NumericInstruction.F32Le,
            F32_GE to NumericInstruction.F32Ge,
            F64_EQ to NumericInstruction.F64Eq,
            F64_NE to NumericInstruction.F64Ne,
            F64_LT to NumericInstruction.F64Lt,
            F64_GT to NumericInstruction.F64Gt,
            F64_LE to NumericInstruction.F64Le,
            F64_GE to NumericInstruction.F64Ge,
            I32_CLZ to NumericInstruction.I32Clz,
            I32_CTZ to NumericInstruction.I32Ctz,
            I32_POPCNT to NumericInstruction.I32Popcnt,
            I32_ADD to NumericInstruction.I32Add,
            I32_SUB to NumericInstruction.I32Sub,
            I32_MUL to NumericInstruction.I32Mul,
            I32_DIV_S to NumericInstruction.I32DivS,
            I32_DIV_U to NumericInstruction.I32DivU,
            I32_REM_S to NumericInstruction.I32RemS,
            I32_REM_U to NumericInstruction.I32RemU,
            I32_AND to NumericInstruction.I32And,
            I32_OR to NumericInstruction.I32Or,
            I32_XOR to NumericInstruction.I32Xor,
            I32_SHL to NumericInstruction.I32Shl,
            I32_SHR_S to NumericInstruction.I32ShrS,
            I32_SHR_U to NumericInstruction.I32ShrU,
            I32_ROTL to NumericInstruction.I32Rotl,
            I32_ROTR to NumericInstruction.I32Rotr,
            I64_CLZ to NumericInstruction.I64Clz,
            I64_CTZ to NumericInstruction.I64Ctz,
            I64_POPCNT to NumericInstruction.I64Popcnt,
            I64_ADD to NumericInstruction.I64Add,
            I64_SUB to NumericInstruction.I64Sub,
            I64_MUL to NumericInstruction.I64Mul,
            I64_DIV_S to NumericInstruction.I64DivS,
            I64_DIV_U to NumericInstruction.I64DivU,
            I64_REM_S to NumericInstruction.I64RemS,
            I64_REM_U to NumericInstruction.I64RemU,
            I64_AND to NumericInstruction.I64And,
            I64_OR to NumericInstruction.I64Or,
            I64_XOR to NumericInstruction.I64Xor,
            I64_SHL to NumericInstruction.I64Shl,
            I64_SHR_S to NumericInstruction.I64ShrS,
            I64_SHR_U to NumericInstruction.I64ShrU,
            I64_ROTL to NumericInstruction.I64Rotl,
            I64_ROTR to NumericInstruction.I64Rotr,
            F32_ABS to NumericInstruction.F32Abs,
            F32_NEG to NumericInstruction.F32Neg,
            F32_CEIL to NumericInstruction.F32Ceil,
            F32_FLOOR to NumericInstruction.F32Floor,
            F32_TRUNC to NumericInstruction.F32Trunc,
            F32_NEAREST to NumericInstruction.F32Nearest,
            F32_SQRT to NumericInstruction.F32Sqrt,
            F32_ADD to NumericInstruction.F32Add,
            F32_SUB to NumericInstruction.F32Sub,
            F32_MUL to NumericInstruction.F32Mul,
            F32_DIV to NumericInstruction.F32Div,
            F32_MIN to NumericInstruction.F32Min,
            F32_MAX to NumericInstruction.F32Max,
            F32_COPYSIGN to NumericInstruction.F32Copysign,
            F64_ABS to NumericInstruction.F64Abs,
            F64_NEG to NumericInstruction.F64Neg,
            F64_CEIL to NumericInstruction.F64Ceil,
            F64_FLOOR to NumericInstruction.F64Floor,
            F64_TRUNC to NumericInstruction.F64Trunc,
            F64_NEAREST to NumericInstruction.F64Nearest,
            F64_SQRT to NumericInstruction.F64Sqrt,
            F64_ADD to NumericInstruction.F64Add,
            F64_SUB to NumericInstruction.F64Sub,
            F64_MUL to NumericInstruction.F64Mul,
            F64_DIV to NumericInstruction.F64Div,
            F64_MIN to NumericInstruction.F64Min,
            F64_MAX to NumericInstruction.F64Max,
            F64_COPYSIGN to NumericInstruction.F64Copysign,
            I32_WRAP_I64 to NumericInstruction.I32WrapI64,
            I32_TRUNC_F32_S to NumericInstruction.I32TruncF32S,
            I32_TRUNC_F32_U to NumericInstruction.I32TruncF32U,
            I32_TRUNC_F64_S to NumericInstruction.I32TruncF64S,
            I32_TRUNC_F64_U to NumericInstruction.I32TruncF64U,
            I64_EXTEND_I32_S to NumericInstruction.I64ExtendI32S,
            I64_EXTEND_I32_U to NumericInstruction.I64ExtendI32U,
            I64_TRUNC_F32_S to NumericInstruction.I64TruncF32S,
            I64_TRUNC_F32_U to NumericInstruction.I64TruncF32U,
            I64_TRUNC_F64_S to NumericInstruction.I64TruncF64S,
            I64_TRUNC_F64_U to NumericInstruction.I64TruncF64U,
            F32_CONVERT_I32_S to NumericInstruction.F32ConvertI32S,
            F32_CONVERT_I32_U to NumericInstruction.F32ConvertI32U,
            F32_CONVERT_I64_S to NumericInstruction.F32ConvertI64S,
            F32_CONVERT_I64_U to NumericInstruction.F32ConvertI64U,
            F32_DEMOTE_F64 to NumericInstruction.F32DemoteF64,
            F64_CONVERT_I32_S to NumericInstruction.F64ConvertI32S,
            F64_CONVERT_I32_U to NumericInstruction.F64ConvertI32U,
            F64_CONVERT_I64_S to NumericInstruction.F64ConvertI64S,
            F64_CONVERT_I64_U to NumericInstruction.F64ConvertI64U,
            F64_PROMOTE_F32 to NumericInstruction.F64PromoteF32,
            I32_REINTERPRET_F32 to NumericInstruction.I32ReinterpretF32,
            I64_REINTERPRET_F64 to NumericInstruction.I64ReinterpretF64,
            F32_REINTERPRET_I32 to NumericInstruction.F32ReinterpretI32,
            F64_REINTERPRET_I64 to NumericInstruction.F64ReinterpretI64,
            I32_EXTEND8_S to NumericInstruction.I32Extend8S,
            I32_EXTEND16_S to NumericInstruction.I32Extend16S,
            I64_EXTEND8_S to NumericInstruction.I64Extend8S,
            I64_EXTEND16_S to NumericInstruction.I64Extend16S,
            I64_EXTEND32_S to NumericInstruction.I64Extend32S,
        )

        opcodeInstructionMap.map { (opcode, instruction) ->
            val expected = Ok(instruction)

            val actual = BinaryNumericInstructionDecoder(FakeWasmBinaryReader(), opcode)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `returns an unknown numeric instruction error when the opcode doesn't match`() {

        val opcode = 0xFFu.toUByte()

        val expected = Err(InstructionDecodeError.InvalidNumericInstruction(opcode))

        val actual = BinaryNumericInstructionDecoder(FakeWasmBinaryReader(), opcode)

        assertEquals(expected, actual)
    }
}
