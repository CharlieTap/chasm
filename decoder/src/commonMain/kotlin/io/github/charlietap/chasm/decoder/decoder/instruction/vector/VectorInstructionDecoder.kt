package io.github.charlietap.chasm.decoder.decoder.instruction.vector

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.memory.MemArgWithIndex
import io.github.charlietap.chasm.decoder.decoder.instruction.memory.MemArgWithIndexDecoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun VectorInstructionDecoder(
    context: DecoderContext,
): Result<VectorInstruction, WasmDecodeError> =
    VectorInstructionDecoder(
        context = context,
        memArgWithIndexDecoder = ::MemArgWithIndexDecoder,
    )

internal inline fun VectorInstructionDecoder(
    context: DecoderContext,
    crossinline memArgWithIndexDecoder: Decoder<MemArgWithIndex>,
): Result<VectorInstruction, WasmDecodeError> = binding {
    val opcode = context.reader.uint().bind()

    when (opcode) {
        V128_LOAD -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            VectorInstruction.V128Load(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        V128_LOAD8X8_S -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            VectorInstruction.V128Load8x8S(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        V128_LOAD8X8_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            VectorInstruction.V128Load8x8U(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        V128_LOAD16X4_S -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            VectorInstruction.V128Load16x4S(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        V128_LOAD16X4_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            VectorInstruction.V128Load16x4U(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        V128_LOAD32X2_S -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            VectorInstruction.V128Load32x2S(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        V128_LOAD32X2_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            VectorInstruction.V128Load32x2U(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        V128_LOAD8_SPLAT -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            VectorInstruction.V128Load8Splat(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        V128_LOAD16_SPLAT -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            VectorInstruction.V128Load16Splat(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        V128_LOAD32_SPLAT -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            VectorInstruction.V128Load32Splat(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        V128_LOAD64_SPLAT -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            VectorInstruction.V128Load64Splat(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        V128_STORE -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            VectorInstruction.V128Store(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        V128_LOAD32_ZERO -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            VectorInstruction.V128Load32Zero(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        V128_LOAD64_ZERO -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            VectorInstruction.V128Load64Zero(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        V128_CONST -> {
            val bytes = context.reader.bytes(16).bind()
            VectorInstruction.V128Const(bytes)
        }
        I8X16_SHUFFLE -> {
            val laneIndices = context.reader.bytes(16).bind()
            VectorInstruction.I8x16Shuffle(laneIndices)
        }
        I8X16_SWIZZLE -> VectorInstruction.I8x16Swizzle
        I8X16_SPLAT -> VectorInstruction.I8x16Splat
        I16X8_SPLAT -> VectorInstruction.I16x8Splat
        I32X4_SPLAT -> VectorInstruction.I32x4Splat
        I64X2_SPLAT -> VectorInstruction.I64x2Splat
        F32X4_SPLAT -> VectorInstruction.F32x4Splat
        F64X2_SPLAT -> VectorInstruction.F64x2Splat
        I8X16_EXTRACT_LANE_S -> {
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.I8x16ExtractLaneS(laneIdx)
        }
        I8X16_EXTRACT_LANE_U -> {
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.I8x16ExtractLaneU(laneIdx)
        }
        I8X16_REPLACE_LANE -> {
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.I8x16ReplaceLane(laneIdx)
        }
        I16X8_EXTRACT_LANE_S -> {
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.I16x8ExtractLaneS(laneIdx)
        }
        I16X8_EXTRACT_LANE_U -> {
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.I16x8ExtractLaneU(laneIdx)
        }
        I16X8_REPLACE_LANE -> {
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.I16x8ReplaceLane(laneIdx)
        }
        I32X4_EXTRACT_LANE -> {
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.I32x4ExtractLane(laneIdx)
        }
        I32X4_REPLACE_LANE -> {
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.I32x4ReplaceLane(laneIdx)
        }
        I64X2_EXTRACT_LANE -> {
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.I64x2ExtractLane(laneIdx)
        }
        I64X2_REPLACE_LANE -> {
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.I64x2ReplaceLane(laneIdx)
        }
        F32X4_EXTRACT_LANE -> {
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.F32x4ExtractLane(laneIdx)
        }
        F32X4_REPLACE_LANE -> {
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.F32x4ReplaceLane(laneIdx)
        }
        F64X2_EXTRACT_LANE -> {
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.F64x2ExtractLane(laneIdx)
        }
        F64X2_REPLACE_LANE -> {
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.F64x2ReplaceLane(laneIdx)
        }
        V128_LOAD8_LANE -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.V128Load8Lane(memArgWithIndex.memoryIndex, memArgWithIndex.memArg, laneIdx)
        }
        V128_LOAD16_LANE -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.V128Load16Lane(memArgWithIndex.memoryIndex, memArgWithIndex.memArg, laneIdx)
        }
        V128_LOAD32_LANE -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.V128Load32Lane(memArgWithIndex.memoryIndex, memArgWithIndex.memArg, laneIdx)
        }
        V128_LOAD64_LANE -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.V128Load64Lane(memArgWithIndex.memoryIndex, memArgWithIndex.memArg, laneIdx)
        }
        V128_STORE8_LANE -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.V128Store8Lane(memArgWithIndex.memoryIndex, memArgWithIndex.memArg, laneIdx)
        }
        V128_STORE16_LANE -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.V128Store16Lane(memArgWithIndex.memoryIndex, memArgWithIndex.memArg, laneIdx)
        }
        V128_STORE32_LANE -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.V128Store32Lane(memArgWithIndex.memoryIndex, memArgWithIndex.memArg, laneIdx)
        }
        V128_STORE64_LANE -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            val laneIdx = context.reader.ubyte().bind().toByte()
            VectorInstruction.V128Store64Lane(memArgWithIndex.memoryIndex, memArgWithIndex.memArg, laneIdx)
        }
        I8X16_EQ -> VectorInstruction.I8x16Eq
        I8X16_NE -> VectorInstruction.I8x16Ne
        I8X16_LT_S -> VectorInstruction.I8x16LtS
        I8X16_LT_U -> VectorInstruction.I8x16LtU
        I8X16_GT_S -> VectorInstruction.I8x16GtS
        I8X16_GT_U -> VectorInstruction.I8x16GtU
        I8X16_LE_S -> VectorInstruction.I8x16LeS
        I8X16_LE_U -> VectorInstruction.I8x16LeU
        I8X16_GE_S -> VectorInstruction.I8x16GeS
        I8X16_GE_U -> VectorInstruction.I8x16GeU
        I16X8_EQ -> VectorInstruction.I16x8Eq
        I16X8_NE -> VectorInstruction.I16x8Ne
        I16X8_LT_S -> VectorInstruction.I16x8LtS
        I16X8_LT_U -> VectorInstruction.I16x8LtU
        I16X8_GT_S -> VectorInstruction.I16x8GtS
        I16X8_GT_U -> VectorInstruction.I16x8GtU
        I16X8_LE_S -> VectorInstruction.I16x8LeS
        I16X8_LE_U -> VectorInstruction.I16x8LeU
        I16X8_GE_S -> VectorInstruction.I16x8GeS
        I16X8_GE_U -> VectorInstruction.I16x8GeU
        I32X4_EQ -> VectorInstruction.I32x4Eq
        I32X4_NE -> VectorInstruction.I32x4Ne
        I32X4_LT_S -> VectorInstruction.I32x4LtS
        I32X4_LT_U -> VectorInstruction.I32x4LtU
        I32X4_GT_S -> VectorInstruction.I32x4GtS
        I32X4_GT_U -> VectorInstruction.I32x4GtU
        I32X4_LE_S -> VectorInstruction.I32x4LeS
        I32X4_LE_U -> VectorInstruction.I32x4LeU
        I32X4_GE_S -> VectorInstruction.I32x4GeS
        I32X4_GE_U -> VectorInstruction.I32x4GeU
        F32X4_EQ -> VectorInstruction.F32x4Eq
        F32X4_NE -> VectorInstruction.F32x4Ne
        F32X4_LT -> VectorInstruction.F32x4Lt
        F32X4_GT -> VectorInstruction.F32x4Gt
        F32X4_LE -> VectorInstruction.F32x4Le
        F32X4_GE -> VectorInstruction.F32x4Ge
        F64X2_EQ -> VectorInstruction.F64x2Eq
        F64X2_NE -> VectorInstruction.F64x2Ne
        F64X2_LT -> VectorInstruction.F64x2Lt
        F64X2_GT -> VectorInstruction.F64x2Gt
        F64X2_LE -> VectorInstruction.F64x2Le
        F64X2_GE -> VectorInstruction.F64x2Ge
        V128_NOT -> VectorInstruction.V128Not
        V128_AND -> VectorInstruction.V128And
        V128_ANDNOT -> VectorInstruction.V128AndNot
        V128_OR -> VectorInstruction.V128Or
        V128_XOR -> VectorInstruction.V128Xor
        V128_BITSELECT -> VectorInstruction.V128Bitselect
        V128_ANY_TRUE -> VectorInstruction.V128AnyTrue
        F32X4_DEMOTE_F64X2_ZERO -> VectorInstruction.F32x4DemoteF64x2Zero
        F64X2_PROMOTE_LOW_F32X4 -> VectorInstruction.F64x2PromoteLowF32x4
        I8X16_ABS -> VectorInstruction.I8x16Abs
        I8X16_NEG -> VectorInstruction.I8x16Neg
        I8X16_POPCNT -> VectorInstruction.I8x16Popcnt
        I8X16_ALL_TRUE -> VectorInstruction.I8x16AllTrue
        I8X16_BITMASK -> VectorInstruction.I8x16Bitmask
        I8X16_NARROW_I16X8_S -> VectorInstruction.I8x16NarrowI16x8S
        I8X16_NARROW_I16X8_U -> VectorInstruction.I8x16NarrowI16x8U
        F32X4_CEIL -> VectorInstruction.F32x4Ceil
        F32X4_FLOOR -> VectorInstruction.F32x4Floor
        F32X4_TRUNC -> VectorInstruction.F32x4Trunc
        F32X4_NEAREST -> VectorInstruction.F32x4Nearest
        I8X16_SHL -> VectorInstruction.I8x16Shl
        I8X16_SHR_S -> VectorInstruction.I8x16ShrS
        I8X16_SHR_U -> VectorInstruction.I8x16ShrU
        I8X16_ADD -> VectorInstruction.I8x16Add
        I8X16_ADD_SAT_S -> VectorInstruction.I8x16AddSatS
        I8X16_ADD_SAT_U -> VectorInstruction.I8x16AddSatU
        I8X16_SUB -> VectorInstruction.I8x16Sub
        I8X16_SUB_SAT_S -> VectorInstruction.I8x16SubSatS
        I8X16_SUB_SAT_U -> VectorInstruction.I8x16SubSatU
        F64X2_CEIL -> VectorInstruction.F64x2Ceil
        F64X2_FLOOR -> VectorInstruction.F64x2Floor
        I8X16_MIN_S -> VectorInstruction.I8x16MinS
        I8X16_MIN_U -> VectorInstruction.I8x16MinU
        I8X16_MAX_S -> VectorInstruction.I8x16MaxS
        I8X16_MAX_U -> VectorInstruction.I8x16MaxU
        F64X2_TRUNC -> VectorInstruction.F64x2Trunc
        I8X16_AVGR_U -> VectorInstruction.I8x16AvgrU
        I16X8_EXTADD_PAIRWISE_I8X16_S -> VectorInstruction.I16x8ExtaddPairwiseI8x16S
        I16X8_EXTADD_PAIRWISE_I8X16_U -> VectorInstruction.I16x8ExtaddPairwiseI8x16U
        I32X4_EXTADD_PAIRWISE_I16X8_S -> VectorInstruction.I32x4ExtaddPairwiseI16x8S
        I32X4_EXTADD_PAIRWISE_I16X8_U -> VectorInstruction.I32x4ExtaddPairwiseI16x8U
        I16X8_ABS -> VectorInstruction.I16x8Abs
        I16X8_NEG -> VectorInstruction.I16x8Neg
        I16X8_Q15MULR_SAT_S -> VectorInstruction.I16x8Q15mulrSatS
        I16X8_ALL_TRUE -> VectorInstruction.I16x8AllTrue
        I16X8_BITMASK -> VectorInstruction.I16x8Bitmask
        I16X8_NARROW_I32X4_S -> VectorInstruction.I16x8NarrowI32x4S
        I16X8_NARROW_I32X4_U -> VectorInstruction.I16x8NarrowI32x4U
        I16X8_EXTEND_LOW_I8X16_S -> VectorInstruction.I16x8ExtendLowI8x16S
        I16X8_EXTEND_HIGH_I8X16_S -> VectorInstruction.I16x8ExtendHighI8x16S
        I16X8_EXTEND_LOW_I8X16_U -> VectorInstruction.I16x8ExtendLowI8x16U
        I16X8_EXTEND_HIGH_I8X16_U -> VectorInstruction.I16x8ExtendHighI8x16U
        I16X8_SHL -> VectorInstruction.I16x8Shl
        I16X8_SHR_S -> VectorInstruction.I16x8ShrS
        I16X8_SHR_U -> VectorInstruction.I16x8ShrU
        I16X8_ADD -> VectorInstruction.I16x8Add
        I16X8_ADD_SAT_S -> VectorInstruction.I16x8AddSatS
        I16X8_ADD_SAT_U -> VectorInstruction.I16x8AddSatU
        I16X8_SUB -> VectorInstruction.I16x8Sub
        I16X8_SUB_SAT_S -> VectorInstruction.I16x8SubSatS
        I16X8_SUB_SAT_U -> VectorInstruction.I16x8SubSatU
        F64X2_NEAREST -> VectorInstruction.F64x2Nearest
        I16X8_MUL -> VectorInstruction.I16x8Mul
        I16X8_MIN_S -> VectorInstruction.I16x8MinS
        I16X8_MIN_U -> VectorInstruction.I16x8MinU
        I16X8_MAX_S -> VectorInstruction.I16x8MaxS
        I16X8_MAX_U -> VectorInstruction.I16x8MaxU
        I16X8_AVGR_U -> VectorInstruction.I16x8AvgrU
        I16X8_EXTMUL_LOW_I8X16_S -> VectorInstruction.I16x8ExtmulLowI8x16S
        I16X8_EXTMUL_HIGH_I8X16_S -> VectorInstruction.I16x8ExtmulHighI8x16S
        I16X8_EXTMUL_LOW_I8X16_U -> VectorInstruction.I16x8ExtmulLowI8x16U
        I16X8_EXTMUL_HIGH_I8X16_U -> VectorInstruction.I16x8ExtmulHighI8x16U
        I32X4_ABS -> VectorInstruction.I32x4Abs
        I32X4_NEG -> VectorInstruction.I32x4Neg
        I32X4_ALL_TRUE -> VectorInstruction.I32x4AllTrue
        I32X4_BITMASK -> VectorInstruction.I32x4Bitmask
        I32X4_EXTEND_LOW_I16X8_S -> VectorInstruction.I32x4ExtendLowI16x8S
        I32X4_EXTEND_HIGH_I16X8_S -> VectorInstruction.I32x4ExtendHighI16x8S
        I32X4_EXTEND_LOW_I16X8_U -> VectorInstruction.I32x4ExtendLowI16x8U
        I32X4_EXTEND_HIGH_I16X8_U -> VectorInstruction.I32x4ExtendHighI16x8U
        I32X4_SHL -> VectorInstruction.I32x4Shl
        I32X4_SHR_S -> VectorInstruction.I32x4ShrS
        I32X4_SHR_U -> VectorInstruction.I32x4ShrU
        I32X4_ADD -> VectorInstruction.I32x4Add
        I32X4_SUB -> VectorInstruction.I32x4Sub
        I32X4_MUL -> VectorInstruction.I32x4Mul
        I32X4_MIN_S -> VectorInstruction.I32x4MinS
        I32X4_MIN_U -> VectorInstruction.I32x4MinU
        I32X4_MAX_S -> VectorInstruction.I32x4MaxS
        I32X4_MAX_U -> VectorInstruction.I32x4MaxU
        I32X4_DOT_I16X8_S -> VectorInstruction.I32x4DotI16x8S
        I32X4_EXTMUL_LOW_I16X8_S -> VectorInstruction.I32x4ExtmulLowI16x8S
        I32X4_EXTMUL_HIGH_I16X8_S -> VectorInstruction.I32x4ExtmulHighI16x8S
        I32X4_EXTMUL_LOW_I16X8_U -> VectorInstruction.I32x4ExtmulLowI16x8U
        I32X4_EXTMUL_HIGH_I16X8_U -> VectorInstruction.I32x4ExtmulHighI16x8U
        I64X2_ABS -> VectorInstruction.I64x2Abs
        I64X2_NEG -> VectorInstruction.I64x2Neg
        I64X2_ALL_TRUE -> VectorInstruction.I64x2AllTrue
        I64X2_BITMASK -> VectorInstruction.I64x2Bitmask
        I64X2_EXTEND_LOW_I32X4_S -> VectorInstruction.I64x2ExtendLowI32x4S
        I64X2_EXTEND_HIGH_I32X4_S -> VectorInstruction.I64x2ExtendHighI32x4S
        I64X2_EXTEND_LOW_I32X4_U -> VectorInstruction.I64x2ExtendLowI32x4U
        I64X2_EXTEND_HIGH_I32X4_U -> VectorInstruction.I64x2ExtendHighI32x4U
        I64X2_SHL -> VectorInstruction.I64x2Shl
        I64X2_SHR_S -> VectorInstruction.I64x2ShrS
        I64X2_SHR_U -> VectorInstruction.I64x2ShrU
        I64X2_ADD -> VectorInstruction.I64x2Add
        I64X2_SUB -> VectorInstruction.I64x2Sub
        I64X2_MUL -> VectorInstruction.I64x2Mul
        I64X2_EQ -> VectorInstruction.I64x2Eq
        I64X2_NE -> VectorInstruction.I64x2Ne
        I64X2_LT_S -> VectorInstruction.I64x2LtS
        I64X2_GT_S -> VectorInstruction.I64x2GtS
        I64X2_LE_S -> VectorInstruction.I64x2LeS
        I64X2_GE_S -> VectorInstruction.I64x2GeS
        I64X2_EXTMUL_LOW_I32X4_S -> VectorInstruction.I64x2ExtmulLowI32x4S
        I64X2_EXTMUL_HIGH_I32X4_S -> VectorInstruction.I64x2ExtmulHighI32x4S
        I64X2_EXTMUL_LOW_I32X4_U -> VectorInstruction.I64x2ExtmulLowI32x4U
        I64X2_EXTMUL_HIGH_I32X4_U -> VectorInstruction.I64x2ExtmulHighI32x4U
        F32X4_ABS -> VectorInstruction.F32x4Abs
        F32X4_NEG -> VectorInstruction.F32x4Neg
        F32X4_SQRT -> VectorInstruction.F32x4Sqrt
        F32X4_ADD -> VectorInstruction.F32x4Add
        F32X4_SUB -> VectorInstruction.F32x4Sub
        F32X4_MUL -> VectorInstruction.F32x4Mul
        F32X4_DIV -> VectorInstruction.F32x4Div
        F32X4_MIN -> VectorInstruction.F32x4Min
        F32X4_MAX -> VectorInstruction.F32x4Max
        F32X4_PMIN -> VectorInstruction.F32x4PMin
        F32X4_PMAX -> VectorInstruction.F32x4PMax
        F64X2_ABS -> VectorInstruction.F64x2Abs
        F64X2_NEG -> VectorInstruction.F64x2Neg
        F64X2_SQRT -> VectorInstruction.F64x2Sqrt
        F64X2_ADD -> VectorInstruction.F64x2Add
        F64X2_SUB -> VectorInstruction.F64x2Sub
        F64X2_MUL -> VectorInstruction.F64x2Mul
        F64X2_DIV -> VectorInstruction.F64x2Div
        F64X2_MIN -> VectorInstruction.F64x2Min
        F64X2_MAX -> VectorInstruction.F64x2Max
        F64X2_PMIN -> VectorInstruction.F64x2PMin
        F64X2_PMAX -> VectorInstruction.F64x2PMax
        I32X4_TRUNC_SAT_F32X4_S -> VectorInstruction.I32x4TruncSatF32x4S
        I32X4_TRUNC_SAT_F32X4_U -> VectorInstruction.I32x4TruncSatF32x4U
        F32X4_CONVERT_I32X4_S -> VectorInstruction.F32x4ConvertI32x4S
        F32X4_CONVERT_I32X4_U -> VectorInstruction.F32x4ConvertI32x4U
        I32X4_TRUNC_SAT_F64X2_S_ZERO -> VectorInstruction.I32x4TruncSatF64x2SZero
        I32X4_TRUNC_SAT_F64X2_U_ZERO -> VectorInstruction.I32x4TruncSatF64x2UZero
        F64X2_CONVERT_LOW_I32X4_S -> VectorInstruction.F64x2ConvertLowI32x4S
        F64X2_CONVERT_LOW_I32X4_U -> VectorInstruction.F64x2ConvertLowI32x4U
        else -> Err(InstructionDecodeError.UnknownInstruction(opcode.toUByte())).bind<VectorInstruction>()
    }
}

private const val V128_LOAD: UInt = 0u
private const val V128_LOAD8X8_S: UInt = 1u
private const val V128_LOAD8X8_U: UInt = 2u
private const val V128_LOAD16X4_S: UInt = 3u
private const val V128_LOAD16X4_U: UInt = 4u
private const val V128_LOAD32X2_S: UInt = 5u
private const val V128_LOAD32X2_U: UInt = 6u
private const val V128_LOAD8_SPLAT: UInt = 7u
private const val V128_LOAD16_SPLAT: UInt = 8u
private const val V128_LOAD32_SPLAT: UInt = 9u
private const val V128_LOAD64_SPLAT: UInt = 10u
private const val V128_STORE: UInt = 11u
private const val V128_CONST: UInt = 12u
private const val I8X16_SHUFFLE: UInt = 13u
private const val I8X16_SWIZZLE: UInt = 14u
private const val I8X16_SPLAT: UInt = 15u
private const val I16X8_SPLAT: UInt = 16u
private const val I32X4_SPLAT: UInt = 17u
private const val I64X2_SPLAT: UInt = 18u
private const val F32X4_SPLAT: UInt = 19u
private const val F64X2_SPLAT: UInt = 20u
private const val I8X16_EXTRACT_LANE_S: UInt = 21u
private const val I8X16_EXTRACT_LANE_U: UInt = 22u
private const val I8X16_REPLACE_LANE: UInt = 23u
private const val I16X8_EXTRACT_LANE_S: UInt = 24u
private const val I16X8_EXTRACT_LANE_U: UInt = 25u
private const val I16X8_REPLACE_LANE: UInt = 26u
private const val I32X4_EXTRACT_LANE: UInt = 27u
private const val I32X4_REPLACE_LANE: UInt = 28u
private const val I64X2_EXTRACT_LANE: UInt = 29u
private const val I64X2_REPLACE_LANE: UInt = 30u
private const val F32X4_EXTRACT_LANE: UInt = 31u
private const val F32X4_REPLACE_LANE: UInt = 32u
private const val F64X2_EXTRACT_LANE: UInt = 33u
private const val F64X2_REPLACE_LANE: UInt = 34u
private const val I8X16_EQ: UInt = 35u
private const val I8X16_NE: UInt = 36u
private const val I8X16_LT_S: UInt = 37u
private const val I8X16_LT_U: UInt = 38u
private const val I8X16_GT_S: UInt = 39u
private const val I8X16_GT_U: UInt = 40u
private const val I8X16_LE_S: UInt = 41u
private const val I8X16_LE_U: UInt = 42u
private const val I8X16_GE_S: UInt = 43u
private const val I8X16_GE_U: UInt = 44u
private const val I16X8_EQ: UInt = 45u
private const val I16X8_NE: UInt = 46u
private const val I16X8_LT_S: UInt = 47u
private const val I16X8_LT_U: UInt = 48u
private const val I16X8_GT_S: UInt = 49u
private const val I16X8_GT_U: UInt = 50u
private const val I16X8_LE_S: UInt = 51u
private const val I16X8_LE_U: UInt = 52u
private const val I16X8_GE_S: UInt = 53u
private const val I16X8_GE_U: UInt = 54u
private const val I32X4_EQ: UInt = 55u
private const val I32X4_NE: UInt = 56u
private const val I32X4_LT_S: UInt = 57u
private const val I32X4_LT_U: UInt = 58u
private const val I32X4_GT_S: UInt = 59u
private const val I32X4_GT_U: UInt = 60u
private const val I32X4_LE_S: UInt = 61u
private const val I32X4_LE_U: UInt = 62u
private const val I32X4_GE_S: UInt = 63u
private const val I32X4_GE_U: UInt = 64u
private const val F32X4_EQ: UInt = 65u
private const val F32X4_NE: UInt = 66u
private const val F32X4_LT: UInt = 67u
private const val F32X4_GT: UInt = 68u
private const val F32X4_LE: UInt = 69u
private const val F32X4_GE: UInt = 70u
private const val F64X2_EQ: UInt = 71u
private const val F64X2_NE: UInt = 72u
private const val F64X2_LT: UInt = 73u
private const val F64X2_GT: UInt = 74u
private const val F64X2_LE: UInt = 75u
private const val F64X2_GE: UInt = 76u
private const val V128_NOT: UInt = 77u
private const val V128_AND: UInt = 78u
private const val V128_ANDNOT: UInt = 79u
private const val V128_OR: UInt = 80u
private const val V128_XOR: UInt = 81u
private const val V128_BITSELECT: UInt = 82u
private const val V128_ANY_TRUE: UInt = 83u
private const val V128_LOAD8_LANE: UInt = 84u
private const val V128_LOAD16_LANE: UInt = 85u
private const val V128_LOAD32_LANE: UInt = 86u
private const val V128_LOAD64_LANE: UInt = 87u
private const val V128_STORE8_LANE: UInt = 88u
private const val V128_STORE16_LANE: UInt = 89u
private const val V128_STORE32_LANE: UInt = 90u
private const val V128_STORE64_LANE: UInt = 91u
private const val V128_LOAD32_ZERO: UInt = 92u
private const val V128_LOAD64_ZERO: UInt = 93u
private const val F32X4_DEMOTE_F64X2_ZERO: UInt = 94u
private const val F64X2_PROMOTE_LOW_F32X4: UInt = 95u
private const val I8X16_ABS: UInt = 96u
private const val I8X16_NEG: UInt = 97u
private const val I8X16_POPCNT: UInt = 98u
private const val I8X16_ALL_TRUE: UInt = 99u
private const val I8X16_BITMASK: UInt = 100u
private const val I8X16_NARROW_I16X8_S: UInt = 101u
private const val I8X16_NARROW_I16X8_U: UInt = 102u
private const val F32X4_CEIL: UInt = 103u
private const val F32X4_FLOOR: UInt = 104u
private const val F32X4_TRUNC: UInt = 105u
private const val F32X4_NEAREST: UInt = 106u
private const val I8X16_SHL: UInt = 107u
private const val I8X16_SHR_S: UInt = 108u
private const val I8X16_SHR_U: UInt = 109u
private const val I8X16_ADD: UInt = 110u
private const val I8X16_ADD_SAT_S: UInt = 111u
private const val I8X16_ADD_SAT_U: UInt = 112u
private const val I8X16_SUB: UInt = 113u
private const val I8X16_SUB_SAT_S: UInt = 114u
private const val I8X16_SUB_SAT_U: UInt = 115u
private const val F64X2_CEIL: UInt = 116u
private const val F64X2_FLOOR: UInt = 117u
private const val I8X16_MIN_S: UInt = 118u
private const val I8X16_MIN_U: UInt = 119u
private const val I8X16_MAX_S: UInt = 120u
private const val I8X16_MAX_U: UInt = 121u
private const val F64X2_TRUNC: UInt = 122u
private const val I8X16_AVGR_U: UInt = 123u
private const val I16X8_EXTADD_PAIRWISE_I8X16_S: UInt = 124u
private const val I16X8_EXTADD_PAIRWISE_I8X16_U: UInt = 125u
private const val I32X4_EXTADD_PAIRWISE_I16X8_S: UInt = 126u
private const val I32X4_EXTADD_PAIRWISE_I16X8_U: UInt = 127u
private const val I16X8_ABS: UInt = 128u
private const val I16X8_NEG: UInt = 129u
private const val I16X8_Q15MULR_SAT_S: UInt = 130u
private const val I16X8_ALL_TRUE: UInt = 131u
private const val I16X8_BITMASK: UInt = 132u
private const val I16X8_NARROW_I32X4_S: UInt = 133u
private const val I16X8_NARROW_I32X4_U: UInt = 134u
private const val I16X8_EXTEND_LOW_I8X16_S: UInt = 135u
private const val I16X8_EXTEND_HIGH_I8X16_S: UInt = 136u
private const val I16X8_EXTEND_LOW_I8X16_U: UInt = 137u
private const val I16X8_EXTEND_HIGH_I8X16_U: UInt = 138u
private const val I16X8_SHL: UInt = 139u
private const val I16X8_SHR_S: UInt = 140u
private const val I16X8_SHR_U: UInt = 141u
private const val I16X8_ADD: UInt = 142u
private const val I16X8_ADD_SAT_S: UInt = 143u
private const val I16X8_ADD_SAT_U: UInt = 144u
private const val I16X8_SUB: UInt = 145u
private const val I16X8_SUB_SAT_S: UInt = 146u
private const val I16X8_SUB_SAT_U: UInt = 147u
private const val F64X2_NEAREST: UInt = 148u
private const val I16X8_MUL: UInt = 149u
private const val I16X8_MIN_S: UInt = 150u
private const val I16X8_MIN_U: UInt = 151u
private const val I16X8_MAX_S: UInt = 152u
private const val I16X8_MAX_U: UInt = 153u
private const val I16X8_AVGR_U: UInt = 155u
private const val I16X8_EXTMUL_LOW_I8X16_S: UInt = 156u
private const val I16X8_EXTMUL_HIGH_I8X16_S: UInt = 157u
private const val I16X8_EXTMUL_LOW_I8X16_U: UInt = 158u
private const val I16X8_EXTMUL_HIGH_I8X16_U: UInt = 159u
private const val I32X4_ABS: UInt = 160u
private const val I32X4_NEG: UInt = 161u
private const val I32X4_ALL_TRUE: UInt = 163u
private const val I32X4_BITMASK: UInt = 164u
private const val I32X4_EXTEND_LOW_I16X8_S: UInt = 167u
private const val I32X4_EXTEND_HIGH_I16X8_S: UInt = 168u
private const val I32X4_EXTEND_LOW_I16X8_U: UInt = 169u
private const val I32X4_EXTEND_HIGH_I16X8_U: UInt = 170u
private const val I32X4_SHL: UInt = 171u
private const val I32X4_SHR_S: UInt = 172u
private const val I32X4_SHR_U: UInt = 173u
private const val I32X4_ADD: UInt = 174u
private const val I32X4_SUB: UInt = 177u
private const val I32X4_MUL: UInt = 181u
private const val I32X4_MIN_S: UInt = 182u
private const val I32X4_MIN_U: UInt = 183u
private const val I32X4_MAX_S: UInt = 184u
private const val I32X4_MAX_U: UInt = 185u
private const val I32X4_DOT_I16X8_S: UInt = 186u
private const val I32X4_EXTMUL_LOW_I16X8_S: UInt = 188u
private const val I32X4_EXTMUL_HIGH_I16X8_S: UInt = 189u
private const val I32X4_EXTMUL_LOW_I16X8_U: UInt = 190u
private const val I32X4_EXTMUL_HIGH_I16X8_U: UInt = 191u
private const val I64X2_ABS: UInt = 192u
private const val I64X2_NEG: UInt = 193u
private const val I64X2_ALL_TRUE: UInt = 195u
private const val I64X2_BITMASK: UInt = 196u
private const val I64X2_EXTEND_LOW_I32X4_S: UInt = 199u
private const val I64X2_EXTEND_HIGH_I32X4_S: UInt = 200u
private const val I64X2_EXTEND_LOW_I32X4_U: UInt = 201u
private const val I64X2_EXTEND_HIGH_I32X4_U: UInt = 202u
private const val I64X2_SHL: UInt = 203u
private const val I64X2_SHR_S: UInt = 204u
private const val I64X2_SHR_U: UInt = 205u
private const val I64X2_ADD: UInt = 206u
private const val I64X2_SUB: UInt = 209u
private const val I64X2_MUL: UInt = 213u
private const val I64X2_EQ: UInt = 214u
private const val I64X2_NE: UInt = 215u
private const val I64X2_LT_S: UInt = 216u
private const val I64X2_GT_S: UInt = 217u
private const val I64X2_LE_S: UInt = 218u
private const val I64X2_GE_S: UInt = 219u
private const val I64X2_EXTMUL_LOW_I32X4_S: UInt = 220u
private const val I64X2_EXTMUL_HIGH_I32X4_S: UInt = 221u
private const val I64X2_EXTMUL_LOW_I32X4_U: UInt = 222u
private const val I64X2_EXTMUL_HIGH_I32X4_U: UInt = 223u
private const val F32X4_ABS: UInt = 224u
private const val F32X4_NEG: UInt = 225u
private const val F32X4_SQRT: UInt = 227u
private const val F32X4_ADD: UInt = 228u
private const val F32X4_SUB: UInt = 229u
private const val F32X4_MUL: UInt = 230u
private const val F32X4_DIV: UInt = 231u
private const val F32X4_MIN: UInt = 232u
private const val F32X4_MAX: UInt = 233u
private const val F32X4_PMIN: UInt = 234u
private const val F32X4_PMAX: UInt = 235u
private const val F64X2_ABS: UInt = 236u
private const val F64X2_NEG: UInt = 237u
private const val F64X2_SQRT: UInt = 239u
private const val F64X2_ADD: UInt = 240u
private const val F64X2_SUB: UInt = 241u
private const val F64X2_MUL: UInt = 242u
private const val F64X2_DIV: UInt = 243u
private const val F64X2_MIN: UInt = 244u
private const val F64X2_MAX: UInt = 245u
private const val F64X2_PMIN: UInt = 246u
private const val F64X2_PMAX: UInt = 247u
private const val I32X4_TRUNC_SAT_F32X4_S: UInt = 248u
private const val I32X4_TRUNC_SAT_F32X4_U: UInt = 249u
private const val F32X4_CONVERT_I32X4_S: UInt = 250u
private const val F32X4_CONVERT_I32X4_U: UInt = 251u
private const val I32X4_TRUNC_SAT_F64X2_S_ZERO: UInt = 252u
private const val I32X4_TRUNC_SAT_F64X2_U_ZERO: UInt = 253u
private const val F64X2_CONVERT_LOW_I32X4_S: UInt = 254u
private const val F64X2_CONVERT_LOW_I32X4_U: UInt = 255u
