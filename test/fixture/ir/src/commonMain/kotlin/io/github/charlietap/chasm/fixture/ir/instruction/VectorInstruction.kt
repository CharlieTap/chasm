package io.github.charlietap.chasm.fixture.ir.instruction

import io.github.charlietap.chasm.ir.instruction.MemArg
import io.github.charlietap.chasm.ir.instruction.VectorInstruction

fun vectorInstruction(): VectorInstruction = v128LoadInstruction()

fun v128LoadInstruction(
    memArg: MemArg = memArg(),
) = VectorInstruction.V128Load(
    memArg = memArg,
)

fun v128Load8SplatInstruction(
    memArg: MemArg = memArg(),
) = VectorInstruction.V128Load8Splat(
    memArg = memArg,
)

fun v128Load16SplatInstruction(
    memArg: MemArg = memArg(),
) = VectorInstruction.V128Load16Splat(
    memArg = memArg,
)

fun v128Load32SplatInstruction(
    memArg: MemArg = memArg(),
) = VectorInstruction.V128Load32Splat(
    memArg = memArg,
)

fun v128Load64SplatInstruction(
    memArg: MemArg = memArg(),
) = VectorInstruction.V128Load64Splat(
    memArg = memArg,
)

fun v128Load8LaneInstruction(
    memArg: MemArg = memArg(),
    laneIdx: Byte = 0,
) = VectorInstruction.V128Load8Lane(
    memArg = memArg,
    laneIdx = laneIdx,
)

fun v128Load16LaneInstruction(
    memArg: MemArg = memArg(),
    laneIdx: Byte = 0,
) = VectorInstruction.V128Load16Lane(
    memArg = memArg,
    laneIdx = laneIdx,
)

fun v128Load32LaneInstruction(
    memArg: MemArg = memArg(),
    laneIdx: Byte = 0,
) = VectorInstruction.V128Load32Lane(
    memArg = memArg,
    laneIdx = laneIdx,
)

fun v128Load64LaneInstruction(
    memArg: MemArg = memArg(),
    laneIdx: Byte = 0,
) = VectorInstruction.V128Load64Lane(
    memArg = memArg,
    laneIdx = laneIdx,
)

fun v128StoreInstruction(
    memArg: MemArg = memArg(),
) = VectorInstruction.V128Store(
    memArg = memArg,
)

fun v128Store8LaneInstruction(
    memArg: MemArg = memArg(),
    laneIdx: Byte = 0,
) = VectorInstruction.V128Store8Lane(
    memArg = memArg,
    laneIdx = laneIdx,
)

fun v128Store16LaneInstruction(
    memArg: MemArg = memArg(),
    laneIdx: Byte = 0,
) = VectorInstruction.V128Store16Lane(
    memArg = memArg,
    laneIdx = laneIdx,
)

fun v128Store32LaneInstruction(
    memArg: MemArg = memArg(),
    laneIdx: Byte = 0,
) = VectorInstruction.V128Store32Lane(
    memArg = memArg,
    laneIdx = laneIdx,
)

fun v128Store64LaneInstruction(
    memArg: MemArg = memArg(),
    laneIdx: Byte = 0,
) = VectorInstruction.V128Store64Lane(
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

fun i8x16SplatInstruction(
    value: Byte = 0,
) = VectorInstruction.I8x16Splat(
    value = value,
)

fun i16x8SplatInstruction(
    value: Short = 0,
) = VectorInstruction.I16x8Splat(
    value = value,
)

fun i32x4SplatInstruction(
    value: Int = 0,
) = VectorInstruction.I32x4Splat(
    value = value,
)

fun i64x2SplatInstruction(
    value: Long = 0L,
) = VectorInstruction.I64x2Splat(
    value = value,
)
