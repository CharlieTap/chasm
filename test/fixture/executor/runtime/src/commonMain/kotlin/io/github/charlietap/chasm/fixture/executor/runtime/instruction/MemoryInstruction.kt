package io.github.charlietap.chasm.fixture.executor.runtime.instruction

import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.runtime.instance.DataInstance
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.fixture.ast.instruction.memArg
import io.github.charlietap.chasm.fixture.ast.module.dataIndex
import io.github.charlietap.chasm.fixture.ast.module.memoryIndex
import io.github.charlietap.chasm.fixture.executor.runtime.instance.dataInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryInstance

fun memoryRuntimeInstruction(): MemoryInstruction = i32LoadRuntimeInstruction()

fun i32LoadRuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Load(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64LoadRuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Load(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun f32LoadRuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.F32Load(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun f64LoadRuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.F64Load(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32Load8SRuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Load8S(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32Load8URuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Load8U(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32Load16SRuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Load16S(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32Load16URuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Load16U(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64Load8SRuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Load8S(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64Load8URuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Load8U(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64Load16SRuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Load16S(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64Load16URuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Load16U(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64Load32SRuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Load32S(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64Load32URuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Load32U(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32StoreRuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Store(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64StoreRuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Store(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun f32StoreRuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.F32Store(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun f64StoreRuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.F64Store(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32Store8RuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Store8(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32Store16RuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I32Store16(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64Store8RuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Store8(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64Store16RuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Store16(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64Store32RuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = MemoryInstruction.I64Store32(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun memorySizeRuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
) = MemoryInstruction.MemorySize(memoryIndex)

fun memoryGrowRuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
) = MemoryInstruction.MemoryGrow(memory)

fun memoryInitRuntimeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    dataIdx: Index.DataIndex = dataIndex(),
) = MemoryInstruction.MemoryInit(
    memoryIndex = memoryIndex,
    dataIndex = dataIdx,
)

fun dataDropRuntimeInstruction(
    data: DataInstance = dataInstance(),
) = MemoryInstruction.DataDrop(
    data = data,
)

fun memoryCopyRuntimeInstruction(
    srcMemory: MemoryInstance = memoryInstance(),
    dstMemory: MemoryInstance = memoryInstance(),
) = MemoryInstruction.MemoryCopy(srcMemory, dstMemory)

fun memoryFillRuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
) = MemoryInstruction.MemoryFill(memory)
