package io.github.charlietap.chasm.ast.instruction

import io.github.charlietap.chasm.ast.module.Index
import kotlin.jvm.JvmInline

sealed interface VectorInstruction : Instruction {

    data class V128Load(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : VectorInstruction

    data class V128Load8x8S(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : VectorInstruction

    data class V128Load8x8U(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : VectorInstruction

    data class V128Load16x4S(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : VectorInstruction

    data class V128Load16x4U(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : VectorInstruction

    data class V128Load32x2S(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : VectorInstruction

    data class V128Load32x2U(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : VectorInstruction

    data class V128Load8Splat(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : VectorInstruction

    data class V128Load16Splat(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : VectorInstruction

    data class V128Load32Splat(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : VectorInstruction

    data class V128Load64Splat(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : VectorInstruction

    data class V128Load32Zero(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : VectorInstruction

    data class V128Load64Zero(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : VectorInstruction

    data class V128Store(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : VectorInstruction

    data class V128Load8Lane(val memoryIndex: Index.MemoryIndex, val memArg: MemArg, val laneIdx: Byte) : VectorInstruction

    data class V128Load16Lane(val memoryIndex: Index.MemoryIndex, val memArg: MemArg, val laneIdx: Byte) : VectorInstruction

    data class V128Load32Lane(val memoryIndex: Index.MemoryIndex, val memArg: MemArg, val laneIdx: Byte) : VectorInstruction

    data class V128Load64Lane(val memoryIndex: Index.MemoryIndex, val memArg: MemArg, val laneIdx: Byte) : VectorInstruction

    data class V128Store8Lane(val memoryIndex: Index.MemoryIndex, val memArg: MemArg, val laneIdx: Byte) : VectorInstruction

    data class V128Store16Lane(val memoryIndex: Index.MemoryIndex, val memArg: MemArg, val laneIdx: Byte) : VectorInstruction

    data class V128Store32Lane(val memoryIndex: Index.MemoryIndex, val memArg: MemArg, val laneIdx: Byte) : VectorInstruction

    data class V128Store64Lane(val memoryIndex: Index.MemoryIndex, val memArg: MemArg, val laneIdx: Byte) : VectorInstruction

    @JvmInline
    value class V128Const(val bytes: ByteArray) : VectorInstruction

    @JvmInline
    value class I8x16Shuffle(val laneIndices: ByteArray) : VectorInstruction

    data object I8x16Swizzle : VectorInstruction

    data object I8x16Splat : VectorInstruction

    data object I16x8Splat : VectorInstruction

    data object I32x4Splat : VectorInstruction

    data object I64x2Splat : VectorInstruction

    data object F32x4Splat : VectorInstruction

    data object F64x2Splat : VectorInstruction

    @JvmInline
    value class I8x16ExtractLaneS(val laneIdx: Byte) : VectorInstruction

    @JvmInline
    value class I8x16ExtractLaneU(val laneIdx: Byte) : VectorInstruction

    @JvmInline
    value class I8x16ReplaceLane(val laneIdx: Byte) : VectorInstruction

    @JvmInline
    value class I16x8ExtractLaneS(val laneIdx: Byte) : VectorInstruction

    @JvmInline
    value class I16x8ExtractLaneU(val laneIdx: Byte) : VectorInstruction

    @JvmInline
    value class I16x8ReplaceLane(val laneIdx: Byte) : VectorInstruction

    @JvmInline
    value class I32x4ExtractLane(val laneIdx: Byte) : VectorInstruction

    @JvmInline
    value class I32x4ReplaceLane(val laneIdx: Byte) : VectorInstruction

    @JvmInline
    value class I64x2ExtractLane(val laneIdx: Byte) : VectorInstruction

    @JvmInline
    value class I64x2ReplaceLane(val laneIdx: Byte) : VectorInstruction

    @JvmInline
    value class F32x4ExtractLane(val laneIdx: Byte) : VectorInstruction

    @JvmInline
    value class F32x4ReplaceLane(val laneIdx: Byte) : VectorInstruction

    @JvmInline
    value class F64x2ExtractLane(val laneIdx: Byte) : VectorInstruction

    @JvmInline
    value class F64x2ReplaceLane(val laneIdx: Byte) : VectorInstruction

    data object I8x16Eq : VectorInstruction

    data object I8x16Ne : VectorInstruction

    data object I8x16LtS : VectorInstruction

    data object I8x16LtU : VectorInstruction

    data object I8x16GtS : VectorInstruction

    data object I8x16GtU : VectorInstruction

    data object I8x16LeS : VectorInstruction

    data object I8x16LeU : VectorInstruction

    data object I8x16GeS : VectorInstruction

    data object I8x16GeU : VectorInstruction

    data object I16x8Eq : VectorInstruction

    data object I16x8Ne : VectorInstruction

    data object I16x8LtS : VectorInstruction

    data object I16x8LtU : VectorInstruction

    data object I16x8GtS : VectorInstruction

    data object I16x8GtU : VectorInstruction

    data object I16x8LeS : VectorInstruction

    data object I16x8LeU : VectorInstruction

    data object I16x8GeS : VectorInstruction

    data object I16x8GeU : VectorInstruction

    data object I32x4Eq : VectorInstruction

    data object I32x4Ne : VectorInstruction

    data object I32x4LtS : VectorInstruction

    data object I32x4LtU : VectorInstruction

    data object I32x4GtS : VectorInstruction

    data object I32x4GtU : VectorInstruction

    data object I32x4LeS : VectorInstruction

    data object I32x4LeU : VectorInstruction

    data object I32x4GeS : VectorInstruction

    data object I32x4GeU : VectorInstruction

    data object I64x2Eq : VectorInstruction

    data object I64x2Ne : VectorInstruction

    data object I64x2LtS : VectorInstruction

    data object I64x2GtS : VectorInstruction

    data object I64x2LeS : VectorInstruction

    data object I64x2GeS : VectorInstruction

    data object F32x4Eq : VectorInstruction

    data object F32x4Ne : VectorInstruction

    data object F32x4Lt : VectorInstruction

    data object F32x4Gt : VectorInstruction

    data object F32x4Le : VectorInstruction

    data object F32x4Ge : VectorInstruction

    data object F64x2Eq : VectorInstruction

    data object F64x2Ne : VectorInstruction

    data object F64x2Lt : VectorInstruction

    data object F64x2Gt : VectorInstruction

    data object F64x2Le : VectorInstruction

    data object F64x2Ge : VectorInstruction

    data object V128Not : VectorInstruction

    data object V128And : VectorInstruction

    data object V128AndNot : VectorInstruction

    data object V128Or : VectorInstruction

    data object V128Xor : VectorInstruction

    data object V128Bitselect : VectorInstruction

    data object V128AnyTrue : VectorInstruction

    data object I8x16Abs : VectorInstruction

    data object I8x16Neg : VectorInstruction

    data object I8x16Popcnt : VectorInstruction

    data object I8x16AllTrue : VectorInstruction

    data object I8x16Bitmask : VectorInstruction

    data object I8x16NarrowI16x8S : VectorInstruction

    data object I8x16NarrowI16x8U : VectorInstruction

    data object I8x16Shl : VectorInstruction

    data object I8x16ShrS : VectorInstruction

    data object I8x16ShrU : VectorInstruction

    data object I8x16Add : VectorInstruction

    data object I8x16AddSatS : VectorInstruction

    data object I8x16AddSatU : VectorInstruction

    data object I8x16Sub : VectorInstruction

    data object I8x16SubSatS : VectorInstruction

    data object I8x16SubSatU : VectorInstruction

    data object I8x16MinS : VectorInstruction

    data object I8x16MinU : VectorInstruction

    data object I8x16MaxS : VectorInstruction

    data object I8x16MaxU : VectorInstruction

    data object I8x16AvgrU : VectorInstruction

    data object I16x8ExtaddPairwiseI8x16S : VectorInstruction

    data object I16x8ExtaddPairwiseI8x16U : VectorInstruction

    data object I16x8Abs : VectorInstruction

    data object I16x8Neg : VectorInstruction

    data object I16x8Q15mulrSatS : VectorInstruction

    data object I16x8AllTrue : VectorInstruction

    data object I16x8Bitmask : VectorInstruction

    data object I16x8NarrowI32x4S : VectorInstruction

    data object I16x8NarrowI32x4U : VectorInstruction

    data object I16x8ExtendLowI8x16S : VectorInstruction

    data object I16x8ExtendHighI8x16S : VectorInstruction

    data object I16x8ExtendLowI8x16U : VectorInstruction

    data object I16x8ExtendHighI8x16U : VectorInstruction

    data object I16x8Shl : VectorInstruction

    data object I16x8ShrS : VectorInstruction

    data object I16x8ShrU : VectorInstruction

    data object I16x8Add : VectorInstruction

    data object I16x8AddSatS : VectorInstruction

    data object I16x8AddSatU : VectorInstruction

    data object I16x8Sub : VectorInstruction

    data object I16x8SubSatS : VectorInstruction

    data object I16x8SubSatU : VectorInstruction

    data object I16x8Mul : VectorInstruction

    data object I16x8MinS : VectorInstruction

    data object I16x8MinU : VectorInstruction

    data object I16x8MaxS : VectorInstruction

    data object I16x8MaxU : VectorInstruction

    data object I16x8AvgrU : VectorInstruction

    data object I16x8ExtmulLowI8x16S : VectorInstruction

    data object I16x8ExtmulHighI8x16S : VectorInstruction

    data object I16x8ExtmulLowI8x16U : VectorInstruction

    data object I16x8ExtmulHighI8x16U : VectorInstruction

    data object I32x4ExtaddPairwiseI16x8S : VectorInstruction

    data object I32x4ExtaddPairwiseI16x8U : VectorInstruction

    data object I32x4Abs : VectorInstruction

    data object I32x4Neg : VectorInstruction

    data object I32x4AllTrue : VectorInstruction

    data object I32x4Bitmask : VectorInstruction

    data object I32x4ExtendLowI16x8S : VectorInstruction

    data object I32x4ExtendHighI16x8S : VectorInstruction

    data object I32x4ExtendLowI16x8U : VectorInstruction

    data object I32x4ExtendHighI16x8U : VectorInstruction

    data object I32x4Shl : VectorInstruction

    data object I32x4ShrS : VectorInstruction

    data object I32x4ShrU : VectorInstruction

    data object I32x4Add : VectorInstruction

    data object I32x4Sub : VectorInstruction

    data object I32x4Mul : VectorInstruction

    data object I32x4MinS : VectorInstruction

    data object I32x4MinU : VectorInstruction

    data object I32x4MaxS : VectorInstruction

    data object I32x4MaxU : VectorInstruction

    data object I32x4DotI16x8S : VectorInstruction

    data object I32x4ExtmulLowI16x8S : VectorInstruction

    data object I32x4ExtmulHighI16x8S : VectorInstruction

    data object I32x4ExtmulLowI16x8U : VectorInstruction

    data object I32x4ExtmulHighI16x8U : VectorInstruction

    data object I64x2Abs : VectorInstruction

    data object I64x2Neg : VectorInstruction

    data object I64x2AllTrue : VectorInstruction

    data object I64x2Bitmask : VectorInstruction

    data object I64x2ExtendLowI32x4S : VectorInstruction

    data object I64x2ExtendHighI32x4S : VectorInstruction

    data object I64x2ExtendLowI32x4U : VectorInstruction

    data object I64x2ExtendHighI32x4U : VectorInstruction

    data object I64x2Shl : VectorInstruction

    data object I64x2ShrS : VectorInstruction

    data object I64x2ShrU : VectorInstruction

    data object I64x2Add : VectorInstruction

    data object I64x2Sub : VectorInstruction

    data object I64x2Mul : VectorInstruction

    data object I64x2ExtmulLowI32x4S : VectorInstruction

    data object I64x2ExtmulHighI32x4S : VectorInstruction

    data object I64x2ExtmulLowI32x4U : VectorInstruction

    data object I64x2ExtmulHighI32x4U : VectorInstruction

    data object F32x4Ceil : VectorInstruction

    data object F32x4Floor : VectorInstruction

    data object F32x4Trunc : VectorInstruction

    data object F32x4Nearest : VectorInstruction

    data object F32x4Abs : VectorInstruction

    data object F32x4Neg : VectorInstruction

    data object F32x4Sqrt : VectorInstruction

    data object F32x4Add : VectorInstruction

    data object F32x4Sub : VectorInstruction

    data object F32x4Mul : VectorInstruction

    data object F32x4Div : VectorInstruction

    data object F32x4Min : VectorInstruction

    data object F32x4Max : VectorInstruction

    data object F32x4PMin : VectorInstruction

    data object F32x4PMax : VectorInstruction

    data object F64x2Ceil : VectorInstruction

    data object F64x2Floor : VectorInstruction

    data object F64x2Trunc : VectorInstruction

    data object F64x2Nearest : VectorInstruction

    data object F64x2Abs : VectorInstruction

    data object F64x2Neg : VectorInstruction

    data object F64x2Sqrt : VectorInstruction

    data object F64x2Add : VectorInstruction

    data object F64x2Sub : VectorInstruction

    data object F64x2Mul : VectorInstruction

    data object F64x2Div : VectorInstruction

    data object F64x2Min : VectorInstruction

    data object F64x2Max : VectorInstruction

    data object F64x2PMin : VectorInstruction

    data object F64x2PMax : VectorInstruction

    data object I32x4TruncSatF32x4S : VectorInstruction

    data object I32x4TruncSatF32x4U : VectorInstruction

    data object F32x4ConvertI32x4S : VectorInstruction

    data object F32x4ConvertI32x4U : VectorInstruction

    data object I32x4TruncSatF64x2SZero : VectorInstruction

    data object I32x4TruncSatF64x2UZero : VectorInstruction

    data object F64x2ConvertLowI32x4S : VectorInstruction

    data object F64x2ConvertLowI32x4U : VectorInstruction

    data object F32x4DemoteF64x2Zero : VectorInstruction

    data object F64x2PromoteLowF32x4 : VectorInstruction
}
