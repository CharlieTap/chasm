package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.F32LoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.F32LoadExecutorImpl
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.F64LoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.F64LoadExecutorImpl
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32LoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32LoadExecutorImpl
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32SizedSignedLoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32SizedSignedLoadExecutorImpl
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32SizedUnsignedLoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32SizedUnsignedLoadExecutorImpl
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64LoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64LoadExecutorImpl
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64SizedSignedLoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64SizedSignedLoadExecutorImpl
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64SizedUnsignedLoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64SizedUnsignedLoadExecutorImpl
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.F32StoreExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.F32StoreExecutorImpl
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.F64StoreExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.F64StoreExecutorImpl
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32StoreExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32StoreExecutorImpl
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32StoreSizedExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32StoreSizedExecutorImpl
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I64StoreExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I64StoreExecutorImpl
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I64StoreSizedExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I64StoreSizedExecutorImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store

internal fun MemoryInstructionExecutorImpl(
    instruction: MemoryInstruction,
    store: Store,
    stack: Stack,
): Result<Unit, InvocationError> =
    MemoryInstructionExecutorImpl(
        instruction = instruction,
        store = store,
        stack = stack,
        memoryInitExecutor = ::MemoryInitExecutorImpl,
        memoryGrowExecutor = ::MemoryGrowExecutorImpl,
        memorySizeExecutor = ::MemorySizeExecutorImpl,
        memoryFillExecutor = ::MemoryFillExecutorImpl,
        memoryCopyExecutor = ::MemoryCopyExecutorImpl,
        dataDropExecutor = ::DataDropExecutorImpl,
        i32LoadExecutor = ::I32LoadExecutorImpl,
        i32SizedSignedLoadExecutor = ::I32SizedSignedLoadExecutorImpl,
        i32SizedUnsignedLoadExecutor = ::I32SizedUnsignedLoadExecutorImpl,
        i64LoadExecutor = ::I64LoadExecutorImpl,
        i64SizedSignedLoadExecutor = ::I64SizedSignedLoadExecutorImpl,
        i64SizedUnsignedLoadExecutor = ::I64SizedUnsignedLoadExecutorImpl,
        f32LoadExecutor = ::F32LoadExecutorImpl,
        f64LoadExecutor = ::F64LoadExecutorImpl,
        i32StoreExecutor = ::I32StoreExecutorImpl,
        i32StoreSizedExecutor = ::I32StoreSizedExecutorImpl,
        i64StoreSizedExecutor = ::I64StoreSizedExecutorImpl,
        i64StoreExecutor = ::I64StoreExecutorImpl,
        f32StoreExecutor = ::F32StoreExecutorImpl,
        f64StoreExecutor = ::F64StoreExecutorImpl,
    )

internal fun MemoryInstructionExecutorImpl(
    instruction: MemoryInstruction,
    store: Store,
    stack: Stack,
    memoryInitExecutor: MemoryInitExecutor,
    memoryGrowExecutor: MemoryGrowExecutor,
    memorySizeExecutor: MemorySizeExecutor,
    memoryFillExecutor: MemoryFillExecutor,
    memoryCopyExecutor: MemoryCopyExecutor,
    dataDropExecutor: DataDropExecutor,
    i32LoadExecutor: I32LoadExecutor,
    i32SizedSignedLoadExecutor: I32SizedSignedLoadExecutor,
    i32SizedUnsignedLoadExecutor: I32SizedUnsignedLoadExecutor,
    i64LoadExecutor: I64LoadExecutor,
    i64SizedSignedLoadExecutor: I64SizedSignedLoadExecutor,
    i64SizedUnsignedLoadExecutor: I64SizedUnsignedLoadExecutor,
    f32LoadExecutor: F32LoadExecutor,
    f64LoadExecutor: F64LoadExecutor,
    i32StoreExecutor: I32StoreExecutor,
    i32StoreSizedExecutor: I32StoreSizedExecutor,
    i64StoreSizedExecutor: I64StoreSizedExecutor,
    i64StoreExecutor: I64StoreExecutor,
    f32StoreExecutor: F32StoreExecutor,
    f64StoreExecutor: F64StoreExecutor,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is MemoryInstruction.MemoryInit -> memoryInitExecutor(store, stack, instruction).bind()
        is MemoryInstruction.DataDrop -> dataDropExecutor(store, stack, instruction).bind()
        is MemoryInstruction.MemoryGrow -> memoryGrowExecutor(store, stack).bind()
        is MemoryInstruction.MemoryFill -> memoryFillExecutor(store, stack).bind()
        is MemoryInstruction.MemoryCopy -> memoryCopyExecutor(store, stack).bind()
        is MemoryInstruction.MemorySize -> memorySizeExecutor(store, stack).bind()
        is MemoryInstruction.I32Load -> i32LoadExecutor(store, stack, instruction).bind()
        is MemoryInstruction.I32Load8S -> i32SizedSignedLoadExecutor(store, stack, instruction.memArg, 1).bind()
        is MemoryInstruction.I32Load8U -> i32SizedUnsignedLoadExecutor(store, stack, instruction.memArg, 1).bind()
        is MemoryInstruction.I32Load16S -> i32SizedSignedLoadExecutor(store, stack, instruction.memArg, 2).bind()
        is MemoryInstruction.I32Load16U -> i32SizedUnsignedLoadExecutor(store, stack, instruction.memArg, 2).bind()
        is MemoryInstruction.I32Store -> i32StoreExecutor(store, stack, instruction).bind()
        is MemoryInstruction.I32Store8 -> i32StoreSizedExecutor(store, stack, instruction.memArg, 1).bind()
        is MemoryInstruction.I32Store16 -> i32StoreSizedExecutor(store, stack, instruction.memArg, 2).bind()
        is MemoryInstruction.I64Load -> i64LoadExecutor(store, stack, instruction).bind()
        is MemoryInstruction.I64Load8S -> i64SizedSignedLoadExecutor(store, stack, instruction.memArg, 1).bind()
        is MemoryInstruction.I64Load8U -> i64SizedUnsignedLoadExecutor(store, stack, instruction.memArg, 1).bind()
        is MemoryInstruction.I64Load16S -> i64SizedSignedLoadExecutor(store, stack, instruction.memArg, 2).bind()
        is MemoryInstruction.I64Load16U -> i64SizedUnsignedLoadExecutor(store, stack, instruction.memArg, 2).bind()
        is MemoryInstruction.I64Load32S -> i64SizedSignedLoadExecutor(store, stack, instruction.memArg, 4).bind()
        is MemoryInstruction.I64Load32U -> i64SizedUnsignedLoadExecutor(store, stack, instruction.memArg, 4).bind()
        is MemoryInstruction.I64Store -> i64StoreExecutor(store, stack, instruction).bind()
        is MemoryInstruction.I64Store8 -> i64StoreSizedExecutor(store, stack, instruction.memArg, 1).bind()
        is MemoryInstruction.I64Store16 -> i64StoreSizedExecutor(store, stack, instruction.memArg, 2).bind()
        is MemoryInstruction.I64Store32 -> i64StoreSizedExecutor(store, stack, instruction.memArg, 4).bind()
        is MemoryInstruction.F32Load -> f32LoadExecutor(store, stack, instruction).bind()
        is MemoryInstruction.F32Store -> f32StoreExecutor(store, stack, instruction).bind()
        is MemoryInstruction.F64Load -> f64LoadExecutor(store, stack, instruction).bind()
        is MemoryInstruction.F64Store -> f64StoreExecutor(store, stack, instruction).bind()

        else -> Err(InvocationError.UnimplementedInstruction(instruction)).bind<Unit>()
    }
}
