package io.github.charlietap.chasm.ast.instruction

import io.github.charlietap.chasm.ast.module.Index
import kotlin.jvm.JvmInline

sealed interface VectorInstruction : Instruction {

    sealed interface MemoryAccess : VectorInstruction {
        val memoryIndex: Index.MemoryIndex
        val memArg: MemArg
    }

    sealed class Operator(
        val opcode: VectorOpcode,
    ) : VectorInstruction

    data class V128Load(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : MemoryAccess

    data class V128Load8x8S(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : MemoryAccess

    data class V128Load8x8U(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : MemoryAccess

    data class V128Load16x4S(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : MemoryAccess

    data class V128Load16x4U(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : MemoryAccess

    data class V128Load32x2S(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : MemoryAccess

    data class V128Load32x2U(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : MemoryAccess

    data class V128Load8Splat(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : MemoryAccess

    data class V128Load16Splat(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : MemoryAccess

    data class V128Load32Splat(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : MemoryAccess

    data class V128Load64Splat(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : MemoryAccess

    data class V128Load32Zero(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : MemoryAccess

    data class V128Load64Zero(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : MemoryAccess

    data class V128Store(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : MemoryAccess

    data class V128Load8Lane(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg, val laneIdx: Byte) : MemoryAccess

    data class V128Load16Lane(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg, val laneIdx: Byte) : MemoryAccess

    data class V128Load32Lane(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg, val laneIdx: Byte) : MemoryAccess

    data class V128Load64Lane(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg, val laneIdx: Byte) : MemoryAccess

    data class V128Store8Lane(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg, val laneIdx: Byte) : MemoryAccess

    data class V128Store16Lane(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg, val laneIdx: Byte) : MemoryAccess

    data class V128Store32Lane(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg, val laneIdx: Byte) : MemoryAccess

    data class V128Store64Lane(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg, val laneIdx: Byte) : MemoryAccess

    @JvmInline
    value class V128Const(val bytes: ByteArray) : VectorInstruction

    @JvmInline
    value class I8x16Shuffle(val laneIndices: ByteArray) : VectorInstruction

    data object I8x16Swizzle : Operator(VectorOpcode.I8x16Swizzle)

    data object I8x16Splat : Operator(VectorOpcode.I8x16Splat)

    data object I16x8Splat : Operator(VectorOpcode.I16x8Splat)

    data object I32x4Splat : Operator(VectorOpcode.I32x4Splat)

    data object I64x2Splat : Operator(VectorOpcode.I64x2Splat)

    data object F32x4Splat : Operator(VectorOpcode.F32x4Splat)

    data object F64x2Splat : Operator(VectorOpcode.F64x2Splat)

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

    data object I8x16Eq : Operator(VectorOpcode.I8x16Eq)

    data object I8x16Ne : Operator(VectorOpcode.I8x16Ne)

    data object I8x16LtS : Operator(VectorOpcode.I8x16LtS)

    data object I8x16LtU : Operator(VectorOpcode.I8x16LtU)

    data object I8x16GtS : Operator(VectorOpcode.I8x16GtS)

    data object I8x16GtU : Operator(VectorOpcode.I8x16GtU)

    data object I8x16LeS : Operator(VectorOpcode.I8x16LeS)

    data object I8x16LeU : Operator(VectorOpcode.I8x16LeU)

    data object I8x16GeS : Operator(VectorOpcode.I8x16GeS)

    data object I8x16GeU : Operator(VectorOpcode.I8x16GeU)

    data object I16x8Eq : Operator(VectorOpcode.I16x8Eq)

    data object I16x8Ne : Operator(VectorOpcode.I16x8Ne)

    data object I16x8LtS : Operator(VectorOpcode.I16x8LtS)

    data object I16x8LtU : Operator(VectorOpcode.I16x8LtU)

    data object I16x8GtS : Operator(VectorOpcode.I16x8GtS)

    data object I16x8GtU : Operator(VectorOpcode.I16x8GtU)

    data object I16x8LeS : Operator(VectorOpcode.I16x8LeS)

    data object I16x8LeU : Operator(VectorOpcode.I16x8LeU)

    data object I16x8GeS : Operator(VectorOpcode.I16x8GeS)

    data object I16x8GeU : Operator(VectorOpcode.I16x8GeU)

    data object I32x4Eq : Operator(VectorOpcode.I32x4Eq)

    data object I32x4Ne : Operator(VectorOpcode.I32x4Ne)

    data object I32x4LtS : Operator(VectorOpcode.I32x4LtS)

    data object I32x4LtU : Operator(VectorOpcode.I32x4LtU)

    data object I32x4GtS : Operator(VectorOpcode.I32x4GtS)

    data object I32x4GtU : Operator(VectorOpcode.I32x4GtU)

    data object I32x4LeS : Operator(VectorOpcode.I32x4LeS)

    data object I32x4LeU : Operator(VectorOpcode.I32x4LeU)

    data object I32x4GeS : Operator(VectorOpcode.I32x4GeS)

    data object I32x4GeU : Operator(VectorOpcode.I32x4GeU)

    data object I64x2Eq : Operator(VectorOpcode.I64x2Eq)

    data object I64x2Ne : Operator(VectorOpcode.I64x2Ne)

    data object I64x2LtS : Operator(VectorOpcode.I64x2LtS)

    data object I64x2GtS : Operator(VectorOpcode.I64x2GtS)

    data object I64x2LeS : Operator(VectorOpcode.I64x2LeS)

    data object I64x2GeS : Operator(VectorOpcode.I64x2GeS)

    data object F32x4Eq : Operator(VectorOpcode.F32x4Eq)

    data object F32x4Ne : Operator(VectorOpcode.F32x4Ne)

    data object F32x4Lt : Operator(VectorOpcode.F32x4Lt)

    data object F32x4Gt : Operator(VectorOpcode.F32x4Gt)

    data object F32x4Le : Operator(VectorOpcode.F32x4Le)

    data object F32x4Ge : Operator(VectorOpcode.F32x4Ge)

    data object F64x2Eq : Operator(VectorOpcode.F64x2Eq)

    data object F64x2Ne : Operator(VectorOpcode.F64x2Ne)

    data object F64x2Lt : Operator(VectorOpcode.F64x2Lt)

    data object F64x2Gt : Operator(VectorOpcode.F64x2Gt)

    data object F64x2Le : Operator(VectorOpcode.F64x2Le)

    data object F64x2Ge : Operator(VectorOpcode.F64x2Ge)

    data object V128Not : Operator(VectorOpcode.V128Not)

    data object V128And : Operator(VectorOpcode.V128And)

    data object V128AndNot : Operator(VectorOpcode.V128AndNot)

    data object V128Or : Operator(VectorOpcode.V128Or)

    data object V128Xor : Operator(VectorOpcode.V128Xor)

    data object V128Bitselect : Operator(VectorOpcode.V128Bitselect)

    data object V128AnyTrue : Operator(VectorOpcode.V128AnyTrue)

    data object I8x16Abs : Operator(VectorOpcode.I8x16Abs)

    data object I8x16Neg : Operator(VectorOpcode.I8x16Neg)

    data object I8x16Popcnt : Operator(VectorOpcode.I8x16Popcnt)

    data object I8x16AllTrue : Operator(VectorOpcode.I8x16AllTrue)

    data object I8x16Bitmask : Operator(VectorOpcode.I8x16Bitmask)

    data object I8x16NarrowI16x8S : Operator(VectorOpcode.I8x16NarrowI16x8S)

    data object I8x16NarrowI16x8U : Operator(VectorOpcode.I8x16NarrowI16x8U)

    data object I8x16Shl : Operator(VectorOpcode.I8x16Shl)

    data object I8x16ShrS : Operator(VectorOpcode.I8x16ShrS)

    data object I8x16ShrU : Operator(VectorOpcode.I8x16ShrU)

    data object I8x16Add : Operator(VectorOpcode.I8x16Add)

    data object I8x16AddSatS : Operator(VectorOpcode.I8x16AddSatS)

    data object I8x16AddSatU : Operator(VectorOpcode.I8x16AddSatU)

    data object I8x16Sub : Operator(VectorOpcode.I8x16Sub)

    data object I8x16SubSatS : Operator(VectorOpcode.I8x16SubSatS)

    data object I8x16SubSatU : Operator(VectorOpcode.I8x16SubSatU)

    data object I8x16MinS : Operator(VectorOpcode.I8x16MinS)

    data object I8x16MinU : Operator(VectorOpcode.I8x16MinU)

    data object I8x16MaxS : Operator(VectorOpcode.I8x16MaxS)

    data object I8x16MaxU : Operator(VectorOpcode.I8x16MaxU)

    data object I8x16AvgrU : Operator(VectorOpcode.I8x16AvgrU)

    data object I16x8ExtaddPairwiseI8x16S : Operator(VectorOpcode.I16x8ExtaddPairwiseI8x16S)

    data object I16x8ExtaddPairwiseI8x16U : Operator(VectorOpcode.I16x8ExtaddPairwiseI8x16U)

    data object I16x8Abs : Operator(VectorOpcode.I16x8Abs)

    data object I16x8Neg : Operator(VectorOpcode.I16x8Neg)

    data object I16x8Q15mulrSatS : Operator(VectorOpcode.I16x8Q15mulrSatS)

    data object I16x8AllTrue : Operator(VectorOpcode.I16x8AllTrue)

    data object I16x8Bitmask : Operator(VectorOpcode.I16x8Bitmask)

    data object I16x8NarrowI32x4S : Operator(VectorOpcode.I16x8NarrowI32x4S)

    data object I16x8NarrowI32x4U : Operator(VectorOpcode.I16x8NarrowI32x4U)

    data object I16x8ExtendLowI8x16S : Operator(VectorOpcode.I16x8ExtendLowI8x16S)

    data object I16x8ExtendHighI8x16S : Operator(VectorOpcode.I16x8ExtendHighI8x16S)

    data object I16x8ExtendLowI8x16U : Operator(VectorOpcode.I16x8ExtendLowI8x16U)

    data object I16x8ExtendHighI8x16U : Operator(VectorOpcode.I16x8ExtendHighI8x16U)

    data object I16x8Shl : Operator(VectorOpcode.I16x8Shl)

    data object I16x8ShrS : Operator(VectorOpcode.I16x8ShrS)

    data object I16x8ShrU : Operator(VectorOpcode.I16x8ShrU)

    data object I16x8Add : Operator(VectorOpcode.I16x8Add)

    data object I16x8AddSatS : Operator(VectorOpcode.I16x8AddSatS)

    data object I16x8AddSatU : Operator(VectorOpcode.I16x8AddSatU)

    data object I16x8Sub : Operator(VectorOpcode.I16x8Sub)

    data object I16x8SubSatS : Operator(VectorOpcode.I16x8SubSatS)

    data object I16x8SubSatU : Operator(VectorOpcode.I16x8SubSatU)

    data object I16x8Mul : Operator(VectorOpcode.I16x8Mul)

    data object I16x8MinS : Operator(VectorOpcode.I16x8MinS)

    data object I16x8MinU : Operator(VectorOpcode.I16x8MinU)

    data object I16x8MaxS : Operator(VectorOpcode.I16x8MaxS)

    data object I16x8MaxU : Operator(VectorOpcode.I16x8MaxU)

    data object I16x8AvgrU : Operator(VectorOpcode.I16x8AvgrU)

    data object I16x8ExtmulLowI8x16S : Operator(VectorOpcode.I16x8ExtmulLowI8x16S)

    data object I16x8ExtmulHighI8x16S : Operator(VectorOpcode.I16x8ExtmulHighI8x16S)

    data object I16x8ExtmulLowI8x16U : Operator(VectorOpcode.I16x8ExtmulLowI8x16U)

    data object I16x8ExtmulHighI8x16U : Operator(VectorOpcode.I16x8ExtmulHighI8x16U)

    data object I32x4ExtaddPairwiseI16x8S : Operator(VectorOpcode.I32x4ExtaddPairwiseI16x8S)

    data object I32x4ExtaddPairwiseI16x8U : Operator(VectorOpcode.I32x4ExtaddPairwiseI16x8U)

    data object I32x4Abs : Operator(VectorOpcode.I32x4Abs)

    data object I32x4Neg : Operator(VectorOpcode.I32x4Neg)

    data object I32x4AllTrue : Operator(VectorOpcode.I32x4AllTrue)

    data object I32x4Bitmask : Operator(VectorOpcode.I32x4Bitmask)

    data object I32x4ExtendLowI16x8S : Operator(VectorOpcode.I32x4ExtendLowI16x8S)

    data object I32x4ExtendHighI16x8S : Operator(VectorOpcode.I32x4ExtendHighI16x8S)

    data object I32x4ExtendLowI16x8U : Operator(VectorOpcode.I32x4ExtendLowI16x8U)

    data object I32x4ExtendHighI16x8U : Operator(VectorOpcode.I32x4ExtendHighI16x8U)

    data object I32x4Shl : Operator(VectorOpcode.I32x4Shl)

    data object I32x4ShrS : Operator(VectorOpcode.I32x4ShrS)

    data object I32x4ShrU : Operator(VectorOpcode.I32x4ShrU)

    data object I32x4Add : Operator(VectorOpcode.I32x4Add)

    data object I32x4Sub : Operator(VectorOpcode.I32x4Sub)

    data object I32x4Mul : Operator(VectorOpcode.I32x4Mul)

    data object I32x4MinS : Operator(VectorOpcode.I32x4MinS)

    data object I32x4MinU : Operator(VectorOpcode.I32x4MinU)

    data object I32x4MaxS : Operator(VectorOpcode.I32x4MaxS)

    data object I32x4MaxU : Operator(VectorOpcode.I32x4MaxU)

    data object I32x4DotI16x8S : Operator(VectorOpcode.I32x4DotI16x8S)

    data object I32x4ExtmulLowI16x8S : Operator(VectorOpcode.I32x4ExtmulLowI16x8S)

    data object I32x4ExtmulHighI16x8S : Operator(VectorOpcode.I32x4ExtmulHighI16x8S)

    data object I32x4ExtmulLowI16x8U : Operator(VectorOpcode.I32x4ExtmulLowI16x8U)

    data object I32x4ExtmulHighI16x8U : Operator(VectorOpcode.I32x4ExtmulHighI16x8U)

    data object I64x2Abs : Operator(VectorOpcode.I64x2Abs)

    data object I64x2Neg : Operator(VectorOpcode.I64x2Neg)

    data object I64x2AllTrue : Operator(VectorOpcode.I64x2AllTrue)

    data object I64x2Bitmask : Operator(VectorOpcode.I64x2Bitmask)

    data object I64x2ExtendLowI32x4S : Operator(VectorOpcode.I64x2ExtendLowI32x4S)

    data object I64x2ExtendHighI32x4S : Operator(VectorOpcode.I64x2ExtendHighI32x4S)

    data object I64x2ExtendLowI32x4U : Operator(VectorOpcode.I64x2ExtendLowI32x4U)

    data object I64x2ExtendHighI32x4U : Operator(VectorOpcode.I64x2ExtendHighI32x4U)

    data object I64x2Shl : Operator(VectorOpcode.I64x2Shl)

    data object I64x2ShrS : Operator(VectorOpcode.I64x2ShrS)

    data object I64x2ShrU : Operator(VectorOpcode.I64x2ShrU)

    data object I64x2Add : Operator(VectorOpcode.I64x2Add)

    data object I64x2Sub : Operator(VectorOpcode.I64x2Sub)

    data object I64x2Mul : Operator(VectorOpcode.I64x2Mul)

    data object I64x2ExtmulLowI32x4S : Operator(VectorOpcode.I64x2ExtmulLowI32x4S)

    data object I64x2ExtmulHighI32x4S : Operator(VectorOpcode.I64x2ExtmulHighI32x4S)

    data object I64x2ExtmulLowI32x4U : Operator(VectorOpcode.I64x2ExtmulLowI32x4U)

    data object I64x2ExtmulHighI32x4U : Operator(VectorOpcode.I64x2ExtmulHighI32x4U)

    data object F32x4Ceil : Operator(VectorOpcode.F32x4Ceil)

    data object F32x4Floor : Operator(VectorOpcode.F32x4Floor)

    data object F32x4Trunc : Operator(VectorOpcode.F32x4Trunc)

    data object F32x4Nearest : Operator(VectorOpcode.F32x4Nearest)

    data object F32x4Abs : Operator(VectorOpcode.F32x4Abs)

    data object F32x4Neg : Operator(VectorOpcode.F32x4Neg)

    data object F32x4Sqrt : Operator(VectorOpcode.F32x4Sqrt)

    data object F32x4Add : Operator(VectorOpcode.F32x4Add)

    data object F32x4Sub : Operator(VectorOpcode.F32x4Sub)

    data object F32x4Mul : Operator(VectorOpcode.F32x4Mul)

    data object F32x4Div : Operator(VectorOpcode.F32x4Div)

    data object F32x4Min : Operator(VectorOpcode.F32x4Min)

    data object F32x4Max : Operator(VectorOpcode.F32x4Max)

    data object F32x4PMin : Operator(VectorOpcode.F32x4PMin)

    data object F32x4PMax : Operator(VectorOpcode.F32x4PMax)

    data object F64x2Ceil : Operator(VectorOpcode.F64x2Ceil)

    data object F64x2Floor : Operator(VectorOpcode.F64x2Floor)

    data object F64x2Trunc : Operator(VectorOpcode.F64x2Trunc)

    data object F64x2Nearest : Operator(VectorOpcode.F64x2Nearest)

    data object F64x2Abs : Operator(VectorOpcode.F64x2Abs)

    data object F64x2Neg : Operator(VectorOpcode.F64x2Neg)

    data object F64x2Sqrt : Operator(VectorOpcode.F64x2Sqrt)

    data object F64x2Add : Operator(VectorOpcode.F64x2Add)

    data object F64x2Sub : Operator(VectorOpcode.F64x2Sub)

    data object F64x2Mul : Operator(VectorOpcode.F64x2Mul)

    data object F64x2Div : Operator(VectorOpcode.F64x2Div)

    data object F64x2Min : Operator(VectorOpcode.F64x2Min)

    data object F64x2Max : Operator(VectorOpcode.F64x2Max)

    data object F64x2PMin : Operator(VectorOpcode.F64x2PMin)

    data object F64x2PMax : Operator(VectorOpcode.F64x2PMax)

    data object I32x4TruncSatF32x4S : Operator(VectorOpcode.I32x4TruncSatF32x4S)

    data object I32x4TruncSatF32x4U : Operator(VectorOpcode.I32x4TruncSatF32x4U)

    data object F32x4ConvertI32x4S : Operator(VectorOpcode.F32x4ConvertI32x4S)

    data object F32x4ConvertI32x4U : Operator(VectorOpcode.F32x4ConvertI32x4U)

    data object I32x4TruncSatF64x2SZero : Operator(VectorOpcode.I32x4TruncSatF64x2SZero)

    data object I32x4TruncSatF64x2UZero : Operator(VectorOpcode.I32x4TruncSatF64x2UZero)

    data object F64x2ConvertLowI32x4S : Operator(VectorOpcode.F64x2ConvertLowI32x4S)

    data object F64x2ConvertLowI32x4U : Operator(VectorOpcode.F64x2ConvertLowI32x4U)

    data object F32x4DemoteF64x2Zero : Operator(VectorOpcode.F32x4DemoteF64x2Zero)

    data object F64x2PromoteLowF32x4 : Operator(VectorOpcode.F64x2PromoteLowF32x4)

    data object I8x16RelaxedSwizzle : Operator(VectorOpcode.I8x16RelaxedSwizzle)

    data object I32x4RelaxedTruncF32x4S : Operator(VectorOpcode.I32x4RelaxedTruncF32x4S)

    data object I32x4RelaxedTruncF32x4U : Operator(VectorOpcode.I32x4RelaxedTruncF32x4U)

    data object I32x4RelaxedTruncF64x2SZero : Operator(VectorOpcode.I32x4RelaxedTruncF64x2SZero)

    data object I32x4RelaxedTruncF64x2UZero : Operator(VectorOpcode.I32x4RelaxedTruncF64x2UZero)

    data object F32x4RelaxedMadd : Operator(VectorOpcode.F32x4RelaxedMadd)

    data object F32x4RelaxedNmadd : Operator(VectorOpcode.F32x4RelaxedNmadd)

    data object F64x2RelaxedMadd : Operator(VectorOpcode.F64x2RelaxedMadd)

    data object F64x2RelaxedNmadd : Operator(VectorOpcode.F64x2RelaxedNmadd)

    data object I8x16RelaxedLaneselect : Operator(VectorOpcode.I8x16RelaxedLaneselect)

    data object I16x8RelaxedLaneselect : Operator(VectorOpcode.I16x8RelaxedLaneselect)

    data object I32x4RelaxedLaneselect : Operator(VectorOpcode.I32x4RelaxedLaneselect)

    data object I64x2RelaxedLaneselect : Operator(VectorOpcode.I64x2RelaxedLaneselect)

    data object F32x4RelaxedMin : Operator(VectorOpcode.F32x4RelaxedMin)

    data object F32x4RelaxedMax : Operator(VectorOpcode.F32x4RelaxedMax)

    data object F64x2RelaxedMin : Operator(VectorOpcode.F64x2RelaxedMin)

    data object F64x2RelaxedMax : Operator(VectorOpcode.F64x2RelaxedMax)

    data object I16x8RelaxedQ15mulrS : Operator(VectorOpcode.I16x8RelaxedQ15mulrS)

    data object I16x8RelaxedDotI8x16I7x16S : Operator(VectorOpcode.I16x8RelaxedDotI8x16I7x16S)

    data object I32x4RelaxedDotI8x16I7x16AddS : Operator(VectorOpcode.I32x4RelaxedDotI8x16I7x16AddS)
}

enum class VectorOpcode {
    I8x16Swizzle,
    I8x16Splat,
    I16x8Splat,
    I32x4Splat,
    I64x2Splat,
    F32x4Splat,
    F64x2Splat,
    I8x16Eq,
    I8x16Ne,
    I8x16LtS,
    I8x16LtU,
    I8x16GtS,
    I8x16GtU,
    I8x16LeS,
    I8x16LeU,
    I8x16GeS,
    I8x16GeU,
    I16x8Eq,
    I16x8Ne,
    I16x8LtS,
    I16x8LtU,
    I16x8GtS,
    I16x8GtU,
    I16x8LeS,
    I16x8LeU,
    I16x8GeS,
    I16x8GeU,
    I32x4Eq,
    I32x4Ne,
    I32x4LtS,
    I32x4LtU,
    I32x4GtS,
    I32x4GtU,
    I32x4LeS,
    I32x4LeU,
    I32x4GeS,
    I32x4GeU,
    I64x2Eq,
    I64x2Ne,
    I64x2LtS,
    I64x2GtS,
    I64x2LeS,
    I64x2GeS,
    F32x4Eq,
    F32x4Ne,
    F32x4Lt,
    F32x4Gt,
    F32x4Le,
    F32x4Ge,
    F64x2Eq,
    F64x2Ne,
    F64x2Lt,
    F64x2Gt,
    F64x2Le,
    F64x2Ge,
    V128Not,
    V128And,
    V128AndNot,
    V128Or,
    V128Xor,
    V128Bitselect,
    V128AnyTrue,
    I8x16Abs,
    I8x16Neg,
    I8x16Popcnt,
    I8x16AllTrue,
    I8x16Bitmask,
    I8x16NarrowI16x8S,
    I8x16NarrowI16x8U,
    I8x16Shl,
    I8x16ShrS,
    I8x16ShrU,
    I8x16Add,
    I8x16AddSatS,
    I8x16AddSatU,
    I8x16Sub,
    I8x16SubSatS,
    I8x16SubSatU,
    I8x16MinS,
    I8x16MinU,
    I8x16MaxS,
    I8x16MaxU,
    I8x16AvgrU,
    I16x8ExtaddPairwiseI8x16S,
    I16x8ExtaddPairwiseI8x16U,
    I16x8Abs,
    I16x8Neg,
    I16x8Q15mulrSatS,
    I16x8AllTrue,
    I16x8Bitmask,
    I16x8NarrowI32x4S,
    I16x8NarrowI32x4U,
    I16x8ExtendLowI8x16S,
    I16x8ExtendHighI8x16S,
    I16x8ExtendLowI8x16U,
    I16x8ExtendHighI8x16U,
    I16x8Shl,
    I16x8ShrS,
    I16x8ShrU,
    I16x8Add,
    I16x8AddSatS,
    I16x8AddSatU,
    I16x8Sub,
    I16x8SubSatS,
    I16x8SubSatU,
    I16x8Mul,
    I16x8MinS,
    I16x8MinU,
    I16x8MaxS,
    I16x8MaxU,
    I16x8AvgrU,
    I16x8ExtmulLowI8x16S,
    I16x8ExtmulHighI8x16S,
    I16x8ExtmulLowI8x16U,
    I16x8ExtmulHighI8x16U,
    I32x4ExtaddPairwiseI16x8S,
    I32x4ExtaddPairwiseI16x8U,
    I32x4Abs,
    I32x4Neg,
    I32x4AllTrue,
    I32x4Bitmask,
    I32x4ExtendLowI16x8S,
    I32x4ExtendHighI16x8S,
    I32x4ExtendLowI16x8U,
    I32x4ExtendHighI16x8U,
    I32x4Shl,
    I32x4ShrS,
    I32x4ShrU,
    I32x4Add,
    I32x4Sub,
    I32x4Mul,
    I32x4MinS,
    I32x4MinU,
    I32x4MaxS,
    I32x4MaxU,
    I32x4DotI16x8S,
    I32x4ExtmulLowI16x8S,
    I32x4ExtmulHighI16x8S,
    I32x4ExtmulLowI16x8U,
    I32x4ExtmulHighI16x8U,
    I64x2Abs,
    I64x2Neg,
    I64x2AllTrue,
    I64x2Bitmask,
    I64x2ExtendLowI32x4S,
    I64x2ExtendHighI32x4S,
    I64x2ExtendLowI32x4U,
    I64x2ExtendHighI32x4U,
    I64x2Shl,
    I64x2ShrS,
    I64x2ShrU,
    I64x2Add,
    I64x2Sub,
    I64x2Mul,
    I64x2ExtmulLowI32x4S,
    I64x2ExtmulHighI32x4S,
    I64x2ExtmulLowI32x4U,
    I64x2ExtmulHighI32x4U,
    F32x4Ceil,
    F32x4Floor,
    F32x4Trunc,
    F32x4Nearest,
    F32x4Abs,
    F32x4Neg,
    F32x4Sqrt,
    F32x4Add,
    F32x4Sub,
    F32x4Mul,
    F32x4Div,
    F32x4Min,
    F32x4Max,
    F32x4PMin,
    F32x4PMax,
    F64x2Ceil,
    F64x2Floor,
    F64x2Trunc,
    F64x2Nearest,
    F64x2Abs,
    F64x2Neg,
    F64x2Sqrt,
    F64x2Add,
    F64x2Sub,
    F64x2Mul,
    F64x2Div,
    F64x2Min,
    F64x2Max,
    F64x2PMin,
    F64x2PMax,
    I32x4TruncSatF32x4S,
    I32x4TruncSatF32x4U,
    F32x4ConvertI32x4S,
    F32x4ConvertI32x4U,
    I32x4TruncSatF64x2SZero,
    I32x4TruncSatF64x2UZero,
    F64x2ConvertLowI32x4S,
    F64x2ConvertLowI32x4U,
    F32x4DemoteF64x2Zero,
    F64x2PromoteLowF32x4,
    I8x16RelaxedSwizzle,
    I32x4RelaxedTruncF32x4S,
    I32x4RelaxedTruncF32x4U,
    I32x4RelaxedTruncF64x2SZero,
    I32x4RelaxedTruncF64x2UZero,
    F32x4RelaxedMadd,
    F32x4RelaxedNmadd,
    F64x2RelaxedMadd,
    F64x2RelaxedNmadd,
    I8x16RelaxedLaneselect,
    I16x8RelaxedLaneselect,
    I32x4RelaxedLaneselect,
    I64x2RelaxedLaneselect,
    F32x4RelaxedMin,
    F32x4RelaxedMax,
    F64x2RelaxedMin,
    F64x2RelaxedMax,
    I16x8RelaxedQ15mulrS,
    I16x8RelaxedDotI8x16I7x16S,
    I32x4RelaxedDotI8x16I7x16AddS,
}
