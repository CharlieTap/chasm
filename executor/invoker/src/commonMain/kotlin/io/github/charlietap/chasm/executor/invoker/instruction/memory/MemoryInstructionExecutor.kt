package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.F32LoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.F64LoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32LoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32SizedSignedLoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32SizedUnsignedLoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64LoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64SizedSignedLoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64SizedUnsignedLoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.F32StoreExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.F64StoreExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32StoreExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32StoreSizedExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I64StoreExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I64StoreSizedExecutor
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal fun MemoryInstructionExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction,
): Result<Unit, InvocationError> =
    MemoryInstructionExecutor(
        context = context,
        instruction = instruction,
        memoryInitExecutor = ::MemoryInitExecutor,
        memoryGrowExecutor = ::MemoryGrowExecutor,
        memorySizeExecutor = ::MemorySizeExecutor,
        memoryFillExecutor = ::MemoryFillExecutor,
        memoryCopyExecutor = ::MemoryCopyExecutor,
        dataDropExecutor = ::DataDropExecutor,
        i32LoadExecutor = ::I32LoadExecutor,
        i32SizedSignedLoadExecutor = ::I32SizedSignedLoadExecutor,
        i32SizedUnsignedLoadExecutor = ::I32SizedUnsignedLoadExecutor,
        i64LoadExecutor = ::I64LoadExecutor,
        i64SizedSignedLoadExecutor = ::I64SizedSignedLoadExecutor,
        i64SizedUnsignedLoadExecutor = ::I64SizedUnsignedLoadExecutor,
        f32LoadExecutor = ::F32LoadExecutor,
        f64LoadExecutor = ::F64LoadExecutor,
        i32StoreExecutor = ::I32StoreExecutor,
        i32StoreSizedExecutor = ::I32StoreSizedExecutor,
        i64StoreSizedExecutor = ::I64StoreSizedExecutor,
        i64StoreExecutor = ::I64StoreExecutor,
        f32StoreExecutor = ::F32StoreExecutor,
        f64StoreExecutor = ::F64StoreExecutor,
    )

internal fun MemoryInstructionExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction,
    memoryInitExecutor: Executor<MemoryInstruction.MemoryInit>,
    memoryGrowExecutor: Executor<MemoryInstruction.MemoryGrow>,
    memorySizeExecutor: Executor<MemoryInstruction.MemorySize>,
    memoryFillExecutor: Executor<MemoryInstruction.MemoryFill>,
    memoryCopyExecutor: Executor<MemoryInstruction.MemoryCopy>,
    dataDropExecutor: Executor<MemoryInstruction.DataDrop>,
    i32LoadExecutor: Executor<MemoryInstruction.I32Load>,
    i32SizedSignedLoadExecutor: I32SizedSignedLoadExecutor,
    i32SizedUnsignedLoadExecutor: I32SizedUnsignedLoadExecutor,
    i64LoadExecutor: Executor<MemoryInstruction.I64Load>,
    i64SizedSignedLoadExecutor: I64SizedSignedLoadExecutor,
    i64SizedUnsignedLoadExecutor: I64SizedUnsignedLoadExecutor,
    f32LoadExecutor: Executor<MemoryInstruction.F32Load>,
    f64LoadExecutor: Executor<MemoryInstruction.F64Load>,
    i32StoreExecutor: Executor<MemoryInstruction.I32Store>,
    i32StoreSizedExecutor: I32StoreSizedExecutor,
    i64StoreSizedExecutor: I64StoreSizedExecutor,
    i64StoreExecutor: Executor<MemoryInstruction.I64Store>,
    f32StoreExecutor: Executor<MemoryInstruction.F32Store>,
    f64StoreExecutor: Executor<MemoryInstruction.F64Store>,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is MemoryInstruction.MemoryInit -> memoryInitExecutor(context, instruction).bind()
        is MemoryInstruction.DataDrop -> dataDropExecutor(context, instruction).bind()
        is MemoryInstruction.MemoryGrow -> memoryGrowExecutor(context, instruction).bind()
        is MemoryInstruction.MemoryFill -> memoryFillExecutor(context, instruction).bind()
        is MemoryInstruction.MemoryCopy -> memoryCopyExecutor(context, instruction).bind()
        is MemoryInstruction.MemorySize -> memorySizeExecutor(context, instruction).bind()
        is MemoryInstruction.I32Load -> i32LoadExecutor(context, instruction).bind()
        is MemoryInstruction.I32Load8S -> i32SizedSignedLoadExecutor(context, instruction.memoryIndex, instruction.memArg, 1).bind()
        is MemoryInstruction.I32Load8U -> i32SizedUnsignedLoadExecutor(context, instruction.memoryIndex, instruction.memArg, 1).bind()
        is MemoryInstruction.I32Load16S -> i32SizedSignedLoadExecutor(context, instruction.memoryIndex, instruction.memArg, 2).bind()
        is MemoryInstruction.I32Load16U -> i32SizedUnsignedLoadExecutor(context, instruction.memoryIndex, instruction.memArg, 2).bind()
        is MemoryInstruction.I32Store -> i32StoreExecutor(context, instruction).bind()
        is MemoryInstruction.I32Store8 -> i32StoreSizedExecutor(context, instruction.memoryIndex, instruction.memArg, 1).bind()
        is MemoryInstruction.I32Store16 -> i32StoreSizedExecutor(context, instruction.memoryIndex, instruction.memArg, 2).bind()
        is MemoryInstruction.I64Load -> i64LoadExecutor(context, instruction).bind()
        is MemoryInstruction.I64Load8S -> i64SizedSignedLoadExecutor(context, instruction.memoryIndex, instruction.memArg, 1).bind()
        is MemoryInstruction.I64Load8U -> i64SizedUnsignedLoadExecutor(context, instruction.memoryIndex, instruction.memArg, 1).bind()
        is MemoryInstruction.I64Load16S -> i64SizedSignedLoadExecutor(context, instruction.memoryIndex, instruction.memArg, 2).bind()
        is MemoryInstruction.I64Load16U -> i64SizedUnsignedLoadExecutor(context, instruction.memoryIndex, instruction.memArg, 2).bind()
        is MemoryInstruction.I64Load32S -> i64SizedSignedLoadExecutor(context, instruction.memoryIndex, instruction.memArg, 4).bind()
        is MemoryInstruction.I64Load32U -> i64SizedUnsignedLoadExecutor(context, instruction.memoryIndex, instruction.memArg, 4).bind()
        is MemoryInstruction.I64Store -> i64StoreExecutor(context, instruction).bind()
        is MemoryInstruction.I64Store8 -> i64StoreSizedExecutor(context, instruction.memoryIndex, instruction.memArg, 1).bind()
        is MemoryInstruction.I64Store16 -> i64StoreSizedExecutor(context, instruction.memoryIndex, instruction.memArg, 2).bind()
        is MemoryInstruction.I64Store32 -> i64StoreSizedExecutor(context, instruction.memoryIndex, instruction.memArg, 4).bind()
        is MemoryInstruction.F32Load -> f32LoadExecutor(context, instruction).bind()
        is MemoryInstruction.F32Store -> f32StoreExecutor(context, instruction).bind()
        is MemoryInstruction.F64Load -> f64LoadExecutor(context, instruction).bind()
        is MemoryInstruction.F64Store -> f64StoreExecutor(context, instruction).bind()
    }
}
