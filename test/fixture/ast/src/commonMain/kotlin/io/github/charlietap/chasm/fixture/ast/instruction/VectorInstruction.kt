package io.github.charlietap.chasm.fixture.ast.instruction

import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.fixture.ast.module.memoryIndex

fun vectorInstruction(): VectorInstruction = v128LoadInstruction()

fun v128LoadInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = VectorInstruction.V128Load(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun v128Load8x8SInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = VectorInstruction.V128Load8x8S(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun v128Load8x8UInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = VectorInstruction.V128Load8x8U(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun v128Load16x4SInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = VectorInstruction.V128Load16x4S(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun v128Load16x4UInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = VectorInstruction.V128Load16x4U(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun v128Load32x2SInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = VectorInstruction.V128Load32x2S(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun v128Load32x2UInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = VectorInstruction.V128Load32x2U(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun v128Load8SplatInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = VectorInstruction.V128Load8Splat(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun v128Load16SplatInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = VectorInstruction.V128Load16Splat(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun v128Load32SplatInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = VectorInstruction.V128Load32Splat(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun v128Load64SplatInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = VectorInstruction.V128Load64Splat(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun v128Load32ZeroInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = VectorInstruction.V128Load32Zero(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun v128Load64ZeroInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = VectorInstruction.V128Load64Zero(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun v128Load8LaneInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
    laneIdx: Byte = 0,
) = VectorInstruction.V128Load8Lane(
    memoryIndex = memoryIndex,
    memArg = memArg,
    laneIdx = laneIdx,
)

fun v128Load16LaneInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
    laneIdx: Byte = 0,
) = VectorInstruction.V128Load16Lane(
    memoryIndex = memoryIndex,
    memArg = memArg,
    laneIdx = laneIdx,
)

fun v128Load32LaneInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
    laneIdx: Byte = 0,
) = VectorInstruction.V128Load32Lane(
    memoryIndex = memoryIndex,
    memArg = memArg,
    laneIdx = laneIdx,
)

fun v128Load64LaneInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
    laneIdx: Byte = 0,
) = VectorInstruction.V128Load64Lane(
    memoryIndex = memoryIndex,
    memArg = memArg,
    laneIdx = laneIdx,
)

fun v128StoreInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = VectorInstruction.V128Store(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun v128Store8LaneInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
    laneIdx: Byte = 0,
) = VectorInstruction.V128Store8Lane(
    memoryIndex = memoryIndex,
    memArg = memArg,
    laneIdx = laneIdx,
)

fun v128Store16LaneInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
    laneIdx: Byte = 0,
) = VectorInstruction.V128Store16Lane(
    memoryIndex = memoryIndex,
    memArg = memArg,
    laneIdx = laneIdx,
)

fun v128Store32LaneInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
    laneIdx: Byte = 0,
) = VectorInstruction.V128Store32Lane(
    memoryIndex = memoryIndex,
    memArg = memArg,
    laneIdx = laneIdx,
)

fun v128Store64LaneInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
    laneIdx: Byte = 0,
) = VectorInstruction.V128Store64Lane(
    memoryIndex = memoryIndex,
    memArg = memArg,
    laneIdx = laneIdx,
)

fun v128ConstInstruction(
    bytes: ByteArray = ByteArray(16),
) = VectorInstruction.V128Const(
    bytes = bytes,
)

fun i8x16ShuffleInstruction(
    laneIndices: ByteArray = ByteArray(16) { it.toByte() },
) = VectorInstruction.I8x16Shuffle(
    laneIndices = laneIndices,
)

fun i8x16SwizzleInstruction() = VectorInstruction.I8x16Swizzle

fun i8x16SplatInstruction() = VectorInstruction.I8x16Splat

fun i16x8SplatInstruction() = VectorInstruction.I16x8Splat

fun i32x4SplatInstruction() = VectorInstruction.I32x4Splat

fun i64x2SplatInstruction() = VectorInstruction.I64x2Splat

fun f32x4SplatInstruction() = VectorInstruction.F32x4Splat

fun f64x2SplatInstruction() = VectorInstruction.F64x2Splat

fun i8x16ExtractLaneSInstruction(
    laneIdx: Byte = 0,
) = VectorInstruction.I8x16ExtractLaneS(
    laneIdx = laneIdx,
)

fun i8x16ExtractLaneUInstruction(
    laneIdx: Byte = 0,
) = VectorInstruction.I8x16ExtractLaneU(
    laneIdx = laneIdx,
)

fun i8x16ReplaceLaneInstruction(
    laneIdx: Byte = 0,
) = VectorInstruction.I8x16ReplaceLane(
    laneIdx = laneIdx,
)

fun i16x8ExtractLaneSInstruction(
    laneIdx: Byte = 0,
) = VectorInstruction.I16x8ExtractLaneS(
    laneIdx = laneIdx,
)

fun i16x8ExtractLaneUInstruction(
    laneIdx: Byte = 0,
) = VectorInstruction.I16x8ExtractLaneU(
    laneIdx = laneIdx,
)

fun i16x8ReplaceLaneInstruction(
    laneIdx: Byte = 0,
) = VectorInstruction.I16x8ReplaceLane(
    laneIdx = laneIdx,
)

fun i32x4ExtractLaneInstruction(
    laneIdx: Byte = 0,
) = VectorInstruction.I32x4ExtractLane(
    laneIdx = laneIdx,
)

fun i32x4ReplaceLaneInstruction(
    laneIdx: Byte = 0,
) = VectorInstruction.I32x4ReplaceLane(
    laneIdx = laneIdx,
)

fun i64x2ExtractLaneInstruction(
    laneIdx: Byte = 0,
) = VectorInstruction.I64x2ExtractLane(
    laneIdx = laneIdx,
)

fun i64x2ReplaceLaneInstruction(
    laneIdx: Byte = 0,
) = VectorInstruction.I64x2ReplaceLane(
    laneIdx = laneIdx,
)

fun f32x4ExtractLaneInstruction(
    laneIdx: Byte = 0,
) = VectorInstruction.F32x4ExtractLane(
    laneIdx = laneIdx,
)

fun f32x4ReplaceLaneInstruction(
    laneIdx: Byte = 0,
) = VectorInstruction.F32x4ReplaceLane(
    laneIdx = laneIdx,
)

fun f64x2ExtractLaneInstruction(
    laneIdx: Byte = 0,
) = VectorInstruction.F64x2ExtractLane(
    laneIdx = laneIdx,
)

fun f64x2ReplaceLaneInstruction(
    laneIdx: Byte = 0,
) = VectorInstruction.F64x2ReplaceLane(
    laneIdx = laneIdx,
)

fun i8x16EqInstruction() = VectorInstruction.I8x16Eq

fun i8x16NeInstruction() = VectorInstruction.I8x16Ne

fun i8x16LtSInstruction() = VectorInstruction.I8x16LtS

fun i8x16LtUInstruction() = VectorInstruction.I8x16LtU

fun i8x16GtSInstruction() = VectorInstruction.I8x16GtS

fun i8x16GtUInstruction() = VectorInstruction.I8x16GtU

fun i8x16LeSInstruction() = VectorInstruction.I8x16LeS

fun i8x16LeUInstruction() = VectorInstruction.I8x16LeU

fun i8x16GeSInstruction() = VectorInstruction.I8x16GeS

fun i8x16GeUInstruction() = VectorInstruction.I8x16GeU

fun i16x8EqInstruction() = VectorInstruction.I16x8Eq

fun i16x8NeInstruction() = VectorInstruction.I16x8Ne

fun i16x8LtSInstruction() = VectorInstruction.I16x8LtS

fun i16x8LtUInstruction() = VectorInstruction.I16x8LtU

fun i16x8GtSInstruction() = VectorInstruction.I16x8GtS

fun i16x8GtUInstruction() = VectorInstruction.I16x8GtU

fun i16x8LeSInstruction() = VectorInstruction.I16x8LeS

fun i16x8LeUInstruction() = VectorInstruction.I16x8LeU

fun i16x8GeSInstruction() = VectorInstruction.I16x8GeS

fun i16x8GeUInstruction() = VectorInstruction.I16x8GeU

fun i32x4EqInstruction() = VectorInstruction.I32x4Eq

fun i32x4NeInstruction() = VectorInstruction.I32x4Ne

fun i32x4LtSInstruction() = VectorInstruction.I32x4LtS

fun i32x4LtUInstruction() = VectorInstruction.I32x4LtU

fun i32x4GtSInstruction() = VectorInstruction.I32x4GtS

fun i32x4GtUInstruction() = VectorInstruction.I32x4GtU

fun i32x4LeSInstruction() = VectorInstruction.I32x4LeS

fun i32x4LeUInstruction() = VectorInstruction.I32x4LeU

fun i32x4GeSInstruction() = VectorInstruction.I32x4GeS

fun i32x4GeUInstruction() = VectorInstruction.I32x4GeU

fun i64x2EqInstruction() = VectorInstruction.I64x2Eq

fun i64x2NeInstruction() = VectorInstruction.I64x2Ne

fun i64x2LtSInstruction() = VectorInstruction.I64x2LtS

fun i64x2GtSInstruction() = VectorInstruction.I64x2GtS

fun i64x2LeSInstruction() = VectorInstruction.I64x2LeS

fun i64x2GeSInstruction() = VectorInstruction.I64x2GeS

fun f32x4EqInstruction() = VectorInstruction.F32x4Eq

fun f32x4NeInstruction() = VectorInstruction.F32x4Ne

fun f32x4LtInstruction() = VectorInstruction.F32x4Lt

fun f32x4GtInstruction() = VectorInstruction.F32x4Gt

fun f32x4LeInstruction() = VectorInstruction.F32x4Le

fun f32x4GeInstruction() = VectorInstruction.F32x4Ge

fun f64x2EqInstruction() = VectorInstruction.F64x2Eq

fun f64x2NeInstruction() = VectorInstruction.F64x2Ne

fun f64x2LtInstruction() = VectorInstruction.F64x2Lt

fun f64x2GtInstruction() = VectorInstruction.F64x2Gt

fun f64x2LeInstruction() = VectorInstruction.F64x2Le

fun f64x2GeInstruction() = VectorInstruction.F64x2Ge

fun v128NotInstruction() = VectorInstruction.V128Not

fun v128AndInstruction() = VectorInstruction.V128And

fun v128AndNotInstruction() = VectorInstruction.V128AndNot

fun v128OrInstruction() = VectorInstruction.V128Or

fun v128XorInstruction() = VectorInstruction.V128Xor

fun v128BitselectInstruction() = VectorInstruction.V128Bitselect

fun v128AnyTrueInstruction() = VectorInstruction.V128AnyTrue

fun i8x16AbsInstruction() = VectorInstruction.I8x16Abs

fun i8x16NegInstruction() = VectorInstruction.I8x16Neg

fun i8x16PopcntInstruction() = VectorInstruction.I8x16Popcnt

fun i8x16AllTrueInstruction() = VectorInstruction.I8x16AllTrue

fun i8x16BitmaskInstruction() = VectorInstruction.I8x16Bitmask

fun i8x16NarrowI16x8SInstruction() = VectorInstruction.I8x16NarrowI16x8S

fun i8x16NarrowI16x8UInstruction() = VectorInstruction.I8x16NarrowI16x8U

fun i8x16ShlInstruction() = VectorInstruction.I8x16Shl

fun i8x16ShrSInstruction() = VectorInstruction.I8x16ShrS

fun i8x16ShrUInstruction() = VectorInstruction.I8x16ShrU

fun i8x16AddInstruction() = VectorInstruction.I8x16Add

fun i8x16AddSatSInstruction() = VectorInstruction.I8x16AddSatS

fun i8x16AddSatUInstruction() = VectorInstruction.I8x16AddSatU

fun i8x16SubInstruction() = VectorInstruction.I8x16Sub

fun i8x16SubSatSInstruction() = VectorInstruction.I8x16SubSatS

fun i8x16SubSatUInstruction() = VectorInstruction.I8x16SubSatU

fun i8x16MinSInstruction() = VectorInstruction.I8x16MinS

fun i8x16MinUInstruction() = VectorInstruction.I8x16MinU

fun i8x16MaxSInstruction() = VectorInstruction.I8x16MaxS

fun i8x16MaxUInstruction() = VectorInstruction.I8x16MaxU

fun i8x16AvgrUInstruction() = VectorInstruction.I8x16AvgrU

fun i16x8ExtaddPairwiseI8x16SInstruction() = VectorInstruction.I16x8ExtaddPairwiseI8x16S

fun i16x8ExtaddPairwiseI8x16UInstruction() = VectorInstruction.I16x8ExtaddPairwiseI8x16U

fun i16x8AbsInstruction() = VectorInstruction.I16x8Abs

fun i16x8NegInstruction() = VectorInstruction.I16x8Neg

fun i16x8Q15mulrSatSInstruction() = VectorInstruction.I16x8Q15mulrSatS

fun i16x8AllTrueInstruction() = VectorInstruction.I16x8AllTrue

fun i16x8BitmaskInstruction() = VectorInstruction.I16x8Bitmask

fun i16x8NarrowI32x4SInstruction() = VectorInstruction.I16x8NarrowI32x4S

fun i16x8NarrowI32x4UInstruction() = VectorInstruction.I16x8NarrowI32x4U

fun i16x8ExtendLowI8x16SInstruction() = VectorInstruction.I16x8ExtendLowI8x16S

fun i16x8ExtendHighI8x16SInstruction() = VectorInstruction.I16x8ExtendHighI8x16S

fun i16x8ExtendLowI8x16UInstruction() = VectorInstruction.I16x8ExtendLowI8x16U

fun i16x8ExtendHighI8x16UInstruction() = VectorInstruction.I16x8ExtendHighI8x16U

fun i16x8ShlInstruction() = VectorInstruction.I16x8Shl

fun i16x8ShrSInstruction() = VectorInstruction.I16x8ShrS

fun i16x8ShrUInstruction() = VectorInstruction.I16x8ShrU

fun i16x8AddInstruction() = VectorInstruction.I16x8Add

fun i16x8AddSatSInstruction() = VectorInstruction.I16x8AddSatS

fun i16x8AddSatUInstruction() = VectorInstruction.I16x8AddSatU

fun i16x8SubInstruction() = VectorInstruction.I16x8Sub

fun i16x8SubSatSInstruction() = VectorInstruction.I16x8SubSatS

fun i16x8SubSatUInstruction() = VectorInstruction.I16x8SubSatU

fun i16x8MulInstruction() = VectorInstruction.I16x8Mul

fun i16x8MinSInstruction() = VectorInstruction.I16x8MinS

fun i16x8MinUInstruction() = VectorInstruction.I16x8MinU

fun i16x8MaxSInstruction() = VectorInstruction.I16x8MaxS

fun i16x8MaxUInstruction() = VectorInstruction.I16x8MaxU

fun i16x8AvgrUInstruction() = VectorInstruction.I16x8AvgrU

fun i16x8ExtmulLowI8x16SInstruction() = VectorInstruction.I16x8ExtmulLowI8x16S

fun i16x8ExtmulHighI8x16SInstruction() = VectorInstruction.I16x8ExtmulHighI8x16S

fun i16x8ExtmulLowI8x16UInstruction() = VectorInstruction.I16x8ExtmulLowI8x16U

fun i16x8ExtmulHighI8x16UInstruction() = VectorInstruction.I16x8ExtmulHighI8x16U

fun i32x4ExtaddPairwiseI16x8SInstruction() = VectorInstruction.I32x4ExtaddPairwiseI16x8S

fun i32x4ExtaddPairwiseI16x8UInstruction() = VectorInstruction.I32x4ExtaddPairwiseI16x8U

fun i32x4AbsInstruction() = VectorInstruction.I32x4Abs

fun i32x4NegInstruction() = VectorInstruction.I32x4Neg

fun i32x4AllTrueInstruction() = VectorInstruction.I32x4AllTrue

fun i32x4BitmaskInstruction() = VectorInstruction.I32x4Bitmask

fun i32x4ExtendLowI16x8SInstruction() = VectorInstruction.I32x4ExtendLowI16x8S

fun i32x4ExtendHighI16x8SInstruction() = VectorInstruction.I32x4ExtendHighI16x8S

fun i32x4ExtendLowI16x8UInstruction() = VectorInstruction.I32x4ExtendLowI16x8U

fun i32x4ExtendHighI16x8UInstruction() = VectorInstruction.I32x4ExtendHighI16x8U

fun i32x4ShlInstruction() = VectorInstruction.I32x4Shl

fun i32x4ShrSInstruction() = VectorInstruction.I32x4ShrS

fun i32x4ShrUInstruction() = VectorInstruction.I32x4ShrU

fun i32x4AddInstruction() = VectorInstruction.I32x4Add

fun i32x4SubInstruction() = VectorInstruction.I32x4Sub

fun i32x4MulInstruction() = VectorInstruction.I32x4Mul

fun i32x4MinSInstruction() = VectorInstruction.I32x4MinS

fun i32x4MinUInstruction() = VectorInstruction.I32x4MinU

fun i32x4MaxSInstruction() = VectorInstruction.I32x4MaxS

fun i32x4MaxUInstruction() = VectorInstruction.I32x4MaxU

fun i32x4DotI16x8SInstruction() = VectorInstruction.I32x4DotI16x8S

fun i32x4ExtmulLowI16x8SInstruction() = VectorInstruction.I32x4ExtmulLowI16x8S

fun i32x4ExtmulHighI16x8SInstruction() = VectorInstruction.I32x4ExtmulHighI16x8S

fun i32x4ExtmulLowI16x8UInstruction() = VectorInstruction.I32x4ExtmulLowI16x8U

fun i32x4ExtmulHighI16x8UInstruction() = VectorInstruction.I32x4ExtmulHighI16x8U

fun i64x2AbsInstruction() = VectorInstruction.I64x2Abs

fun i64x2NegInstruction() = VectorInstruction.I64x2Neg

fun i64x2AllTrueInstruction() = VectorInstruction.I64x2AllTrue

fun i64x2BitmaskInstruction() = VectorInstruction.I64x2Bitmask

fun i64x2ExtendLowI32x4SInstruction() = VectorInstruction.I64x2ExtendLowI32x4S

fun i64x2ExtendHighI32x4SInstruction() = VectorInstruction.I64x2ExtendHighI32x4S

fun i64x2ExtendLowI32x4UInstruction() = VectorInstruction.I64x2ExtendLowI32x4U

fun i64x2ExtendHighI32x4UInstruction() = VectorInstruction.I64x2ExtendHighI32x4U

fun i64x2ShlInstruction() = VectorInstruction.I64x2Shl

fun i64x2ShrSInstruction() = VectorInstruction.I64x2ShrS

fun i64x2ShrUInstruction() = VectorInstruction.I64x2ShrU

fun i64x2AddInstruction() = VectorInstruction.I64x2Add

fun i64x2SubInstruction() = VectorInstruction.I64x2Sub

fun i64x2MulInstruction() = VectorInstruction.I64x2Mul

fun i64x2ExtmulLowI32x4SInstruction() = VectorInstruction.I64x2ExtmulLowI32x4S

fun i64x2ExtmulHighI32x4SInstruction() = VectorInstruction.I64x2ExtmulHighI32x4S

fun i64x2ExtmulLowI32x4UInstruction() = VectorInstruction.I64x2ExtmulLowI32x4U

fun i64x2ExtmulHighI32x4UInstruction() = VectorInstruction.I64x2ExtmulHighI32x4U

fun f32x4CeilInstruction() = VectorInstruction.F32x4Ceil

fun f32x4FloorInstruction() = VectorInstruction.F32x4Floor

fun f32x4TruncInstruction() = VectorInstruction.F32x4Trunc

fun f32x4NearestInstruction() = VectorInstruction.F32x4Nearest

fun f32x4AbsInstruction() = VectorInstruction.F32x4Abs

fun f32x4NegInstruction() = VectorInstruction.F32x4Neg

fun f32x4SqrtInstruction() = VectorInstruction.F32x4Sqrt

fun f32x4AddInstruction() = VectorInstruction.F32x4Add

fun f32x4SubInstruction() = VectorInstruction.F32x4Sub

fun f32x4MulInstruction() = VectorInstruction.F32x4Mul

fun f32x4DivInstruction() = VectorInstruction.F32x4Div

fun f32x4MinInstruction() = VectorInstruction.F32x4Min

fun f32x4MaxInstruction() = VectorInstruction.F32x4Max

fun f32x4PMinInstruction() = VectorInstruction.F32x4PMin

fun f32x4PMaxInstruction() = VectorInstruction.F32x4PMax

fun f64x2CeilInstruction() = VectorInstruction.F64x2Ceil

fun f64x2FloorInstruction() = VectorInstruction.F64x2Floor

fun f64x2TruncInstruction() = VectorInstruction.F64x2Trunc

fun f64x2NearestInstruction() = VectorInstruction.F64x2Nearest

fun f64x2AbsInstruction() = VectorInstruction.F64x2Abs

fun f64x2NegInstruction() = VectorInstruction.F64x2Neg

fun f64x2SqrtInstruction() = VectorInstruction.F64x2Sqrt

fun f64x2AddInstruction() = VectorInstruction.F64x2Add

fun f64x2SubInstruction() = VectorInstruction.F64x2Sub

fun f64x2MulInstruction() = VectorInstruction.F64x2Mul

fun f64x2DivInstruction() = VectorInstruction.F64x2Div

fun f64x2MinInstruction() = VectorInstruction.F64x2Min

fun f64x2MaxInstruction() = VectorInstruction.F64x2Max

fun f64x2PMinInstruction() = VectorInstruction.F64x2PMin

fun f64x2PMaxInstruction() = VectorInstruction.F64x2PMax

fun i32x4TruncSatF32x4SInstruction() = VectorInstruction.I32x4TruncSatF32x4S

fun i32x4TruncSatF32x4UInstruction() = VectorInstruction.I32x4TruncSatF32x4U

fun f32x4ConvertI32x4SInstruction() = VectorInstruction.F32x4ConvertI32x4S

fun f32x4ConvertI32x4UInstruction() = VectorInstruction.F32x4ConvertI32x4U

fun i32x4TruncSatF64x2SZeroInstruction() = VectorInstruction.I32x4TruncSatF64x2SZero

fun i32x4TruncSatF64x2UZeroInstruction() = VectorInstruction.I32x4TruncSatF64x2UZero

fun f64x2ConvertLowI32x4SInstruction() = VectorInstruction.F64x2ConvertLowI32x4S

fun f64x2ConvertLowI32x4UInstruction() = VectorInstruction.F64x2ConvertLowI32x4U

fun f32x4DemoteF64x2ZeroInstruction() = VectorInstruction.F32x4DemoteF64x2Zero

fun f64x2PromoteLowF32x4Instruction() = VectorInstruction.F64x2PromoteLowF32x4
