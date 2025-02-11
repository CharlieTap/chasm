package io.github.charlietap.chasm.fixture.ir.instruction

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.fixture.ast.module.dataIndex
import io.github.charlietap.chasm.fixture.ast.module.memoryIndex
import io.github.charlietap.chasm.ir.instruction.MemArg
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction

fun memoryInstruction(): MemoryInstruction = i32LoadInstruction()

fun i32LoadInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Load(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64LoadInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Load(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun f32LoadInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.F32Load(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun f64LoadInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.F64Load(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32Load8SInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Load8S(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32Load8UInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Load8U(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32Load16SInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Load16S(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32Load16UInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Load16U(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64Load8SInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Load8S(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64Load8UInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Load8U(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64Load16SInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Load16S(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64Load16UInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Load16U(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64Load32SInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Load32S(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64Load32UInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Load32U(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32StoreInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Store(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64StoreInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Store(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun f32StoreInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.F32Store(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun f64StoreInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.F64Store(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32Store8Instruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Store8(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32Store16Instruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Store16(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64Store8Instruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Store8(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64Store16Instruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Store16(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64Store32Instruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Store32(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun memorySizeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
) = MemoryInstruction.MemorySize(memoryIndex)

fun memoryGrowInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
) = MemoryInstruction.MemoryGrow(memoryIndex)

fun memoryInitInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    dataIdx: Index.DataIndex = dataIndex(),
) = MemoryInstruction.MemoryInit(
    memoryIndex = memoryIndex,
    dataIndex = dataIdx,
)

fun dataDropInstruction(
    dataIdx: Index.DataIndex = dataIndex(),
) = MemoryInstruction.DataDrop(
    dataIdx = dataIdx,
)

fun memoryCopyInstruction(
    srcMemoryIndex: Index.MemoryIndex = memoryIndex(),
    dstMemoryIndex: Index.MemoryIndex = memoryIndex(),
) = MemoryInstruction.MemoryCopy(srcMemoryIndex, dstMemoryIndex)

fun memoryFillInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
) = MemoryInstruction.MemoryFill(memoryIndex)
