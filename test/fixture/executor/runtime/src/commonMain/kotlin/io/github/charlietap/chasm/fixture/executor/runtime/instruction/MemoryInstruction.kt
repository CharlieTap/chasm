package io.github.charlietap.chasm.fixture.executor.runtime.instruction

import io.github.charlietap.chasm.fixture.executor.runtime.instance.dataInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryInstance
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instruction.MemArg
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun memoryRuntimeInstruction(): MemoryInstruction = i32LoadRuntimeInstruction()

fun i32LoadRuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.I32Load(
    memory = memory,
    memArg = memArg,
)

fun i64LoadRuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.I64Load(
    memory = memory,
    memArg = memArg,
)

fun f32LoadRuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.F32Load(
    memory = memory,
    memArg = memArg,
)

fun f64LoadRuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.F64Load(
    memory = memory,
    memArg = memArg,
)

fun i32Load8SRuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.I32Load8S(
    memory = memory,
    memArg = memArg,
)

fun i32Load8URuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.I32Load8U(
    memory = memory,
    memArg = memArg,
)

fun i32Load16SRuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.I32Load16S(
    memory = memory,
    memArg = memArg,
)

fun i32Load16URuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.I32Load16U(
    memory = memory,
    memArg = memArg,
)

fun i64Load8SRuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.I64Load8S(
    memory = memory,
    memArg = memArg,
)

fun i64Load8URuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.I64Load8U(
    memory = memory,
    memArg = memArg,
)

fun i64Load16SRuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.I64Load16S(
    memory = memory,
    memArg = memArg,
)

fun i64Load16URuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.I64Load16U(
    memory = memory,
    memArg = memArg,
)

fun i64Load32SRuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.I64Load32S(
    memory = memory,
    memArg = memArg,
)

fun i64Load32URuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.I64Load32U(
    memory = memory,
    memArg = memArg,
)

fun i32StoreRuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.I32Store(
    memory = memory,
    memArg = memArg,
)

fun i64StoreRuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.I64Store(
    memory = memory,
    memArg = memArg,
)

fun f32StoreRuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.F32Store(
    memory = memory,
    memArg = memArg,
)

fun f64StoreRuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.F64Store(
    memory = memory,
    memArg = memArg,
)

fun i32Store8RuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.I32Store8(
    memory = memory,
    memArg = memArg,
)

fun i32Store16RuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.I32Store16(
    memory = memory,
    memArg = memArg,
)

fun i64Store8RuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.I64Store8(
    memory = memory,
    memArg = memArg,
)

fun i64Store16RuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.I64Store16(
    memory = memory,
    memArg = memArg,
)

fun i64Store32RuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    memArg: MemArg = runtimeMemArg(),
) = MemoryInstruction.I64Store32(
    memory = memory,
    memArg = memArg,
)

fun memorySizeRuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
) = MemoryInstruction.MemorySize(memory)

fun memoryGrowRuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    max: Int = 0,
) = MemoryInstruction.MemoryGrow(memory, max)

fun memoryInitRuntimeInstruction(
    memory: MemoryInstance = memoryInstance(),
    data: DataInstance = dataInstance(),
) = MemoryInstruction.MemoryInit(
    memory = memory,
    data = data,
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
