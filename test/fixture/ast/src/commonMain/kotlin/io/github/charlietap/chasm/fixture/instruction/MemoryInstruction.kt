package io.github.charlietap.chasm.fixture.instruction

import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.fixture.module.dataIndex

fun memoryInstruction(): MemoryInstruction = i32LoadInstruction()

fun i32LoadInstruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Load(
    memArg = memArg,
)

fun i64LoadInstruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Load(
    memArg = memArg,
)

fun f32LoadInstruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.F32Load(
    memArg = memArg,
)

fun f64LoadInstruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.F64Load(
    memArg = memArg,
)

fun i32Load8SInstruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Load8S(
    memArg = memArg,
)

fun i32Load8UInstruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Load8U(
    memArg = memArg,
)

fun i32Load16SInstruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Load16S(
    memArg = memArg,
)

fun i32Load16UInstruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Load16U(
    memArg = memArg,
)

fun i64Load8SInstruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Load8S(
    memArg = memArg,
)

fun i64Load8UInstruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Load8U(
    memArg = memArg,
)

fun i64Load16SInstruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Load16S(
    memArg = memArg,
)

fun i64Load16UInstruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Load16U(
    memArg = memArg,
)

fun i64Load32SInstruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Load32S(
    memArg = memArg,
)

fun i64Load32UInstruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Load32U(
    memArg = memArg,
)

fun i32StoreInstruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Store(
    memArg = memArg,
)

fun i64StoreInstruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Store(
    memArg = memArg,
)

fun f32StoreInstruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.F32Store(
    memArg = memArg,
)

fun f64StoreInstruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.F64Store(
    memArg = memArg,
)

fun i32Store8Instruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Store8(
    memArg = memArg,
)

fun i32Store16Instruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Store16(
    memArg = memArg,
)

fun i64Store8Instruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Store8(
    memArg = memArg,
)

fun i64Store16Instruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Store16(
    memArg = memArg,
)

fun i64Store32Instruction(
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Store32(
    memArg = memArg,
)

fun memorySizeInstruction() = MemoryInstruction.MemorySize

fun memoryGrowInstruction() = MemoryInstruction.MemoryGrow

fun memoryInitInstruction(
    dataIdx: Index.DataIndex = dataIndex(),
) = MemoryInstruction.MemoryInit(
    dataIdx = dataIdx,
)

fun dataDropInstruction(
    dataIdx: Index.DataIndex = dataIndex(),
) = MemoryInstruction.DataDrop(
    dataIdx = dataIdx,
)

fun memoryCopyInstruction() = MemoryInstruction.MemoryCopy

fun memoryFillInstruction() = MemoryInstruction.MemoryFill
