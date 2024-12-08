package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.F32LoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.F64LoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32Load16SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32Load16UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32Load8SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32Load8UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32LoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64Load16SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64Load16UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64Load32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64Load32UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64Load8SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64Load8UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64LoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.F32StoreExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.F64StoreExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32Store16Executor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32Store8Executor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32StoreExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I64Store16Executor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I64Store32Executor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I64Store8Executor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I64StoreExecutor
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
        i32Load8SExecutor = ::I32Load8SExecutor,
        i32Load8UExecutor = ::I32Load8UExecutor,
        i32Load16SExecutor = ::I32Load16SExecutor,
        i32Load16UExecutor = ::I32Load16UExecutor,
        i64LoadExecutor = ::I64LoadExecutor,
        i64Load8SExecutor = ::I64Load8SExecutor,
        i64Load8UExecutor = ::I64Load8UExecutor,
        i64Load16SExecutor = ::I64Load16SExecutor,
        i64Load16UExecutor = ::I64Load16UExecutor,
        i64Load32SExecutor = ::I64Load32SExecutor,
        i64Load32UExecutor = ::I64Load32UExecutor,
        f32LoadExecutor = ::F32LoadExecutor,
        f64LoadExecutor = ::F64LoadExecutor,
        i32StoreExecutor = ::I32StoreExecutor,
        i32Store8Executor = ::I32Store8Executor,
        i32Store16Executor = ::I32Store16Executor,
        i64StoreExecutor = ::I64StoreExecutor,
        i64Store8Executor = ::I64Store8Executor,
        i64Store16Executor = ::I64Store16Executor,
        i64Store32Executor = ::I64Store32Executor,
        f32StoreExecutor = ::F32StoreExecutor,
        f64StoreExecutor = ::F64StoreExecutor,
    )

internal inline fun MemoryInstructionExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction,
    crossinline memoryInitExecutor: Executor<MemoryInstruction.MemoryInit>,
    crossinline memoryGrowExecutor: Executor<MemoryInstruction.MemoryGrow>,
    crossinline memorySizeExecutor: Executor<MemoryInstruction.MemorySize>,
    crossinline memoryFillExecutor: Executor<MemoryInstruction.MemoryFill>,
    crossinline memoryCopyExecutor: Executor<MemoryInstruction.MemoryCopy>,
    crossinline dataDropExecutor: Executor<MemoryInstruction.DataDrop>,
    crossinline i32LoadExecutor: Executor<MemoryInstruction.I32Load>,
    crossinline i32Load8SExecutor: Executor<MemoryInstruction.I32Load8S>,
    crossinline i32Load8UExecutor: Executor<MemoryInstruction.I32Load8U>,
    crossinline i32Load16SExecutor: Executor<MemoryInstruction.I32Load16S>,
    crossinline i32Load16UExecutor: Executor<MemoryInstruction.I32Load16U>,
    crossinline i64LoadExecutor: Executor<MemoryInstruction.I64Load>,
    crossinline i64Load8SExecutor: Executor<MemoryInstruction.I64Load8S>,
    crossinline i64Load8UExecutor: Executor<MemoryInstruction.I64Load8U>,
    crossinline i64Load16SExecutor: Executor<MemoryInstruction.I64Load16S>,
    crossinline i64Load16UExecutor: Executor<MemoryInstruction.I64Load16U>,
    crossinline i64Load32SExecutor: Executor<MemoryInstruction.I64Load32S>,
    crossinline i64Load32UExecutor: Executor<MemoryInstruction.I64Load32U>,
    crossinline f32LoadExecutor: Executor<MemoryInstruction.F32Load>,
    crossinline f64LoadExecutor: Executor<MemoryInstruction.F64Load>,
    crossinline i32StoreExecutor: Executor<MemoryInstruction.I32Store>,
    crossinline i32Store8Executor: Executor<MemoryInstruction.I32Store8>,
    crossinline i32Store16Executor: Executor<MemoryInstruction.I32Store16>,
    crossinline i64StoreExecutor: Executor<MemoryInstruction.I64Store>,
    crossinline i64Store8Executor: Executor<MemoryInstruction.I64Store8>,
    crossinline i64Store16Executor: Executor<MemoryInstruction.I64Store16>,
    crossinline i64Store32Executor: Executor<MemoryInstruction.I64Store32>,
    crossinline f32StoreExecutor: Executor<MemoryInstruction.F32Store>,
    crossinline f64StoreExecutor: Executor<MemoryInstruction.F64Store>,
): Result<Unit, InvocationError> = binding {
    when (instruction) {
        is MemoryInstruction.MemoryInit -> memoryInitExecutor(context, instruction).bind()
        is MemoryInstruction.DataDrop -> dataDropExecutor(context, instruction).bind()
        is MemoryInstruction.MemoryGrow -> memoryGrowExecutor(context, instruction).bind()
        is MemoryInstruction.MemoryFill -> memoryFillExecutor(context, instruction).bind()
        is MemoryInstruction.MemoryCopy -> memoryCopyExecutor(context, instruction).bind()
        is MemoryInstruction.MemorySize -> memorySizeExecutor(context, instruction).bind()
        is MemoryInstruction.I32Load -> i32LoadExecutor(context, instruction).bind()
        is MemoryInstruction.I32Load8S -> i32Load8SExecutor(context, instruction).bind()
        is MemoryInstruction.I32Load8U -> i32Load8UExecutor(context, instruction).bind()
        is MemoryInstruction.I32Load16S -> i32Load16SExecutor(context, instruction).bind()
        is MemoryInstruction.I32Load16U -> i32Load16UExecutor(context, instruction).bind()
        is MemoryInstruction.I32Store -> i32StoreExecutor(context, instruction).bind()
        is MemoryInstruction.I32Store8 -> i32Store8Executor(context, instruction).bind()
        is MemoryInstruction.I32Store16 -> i32Store16Executor(context, instruction).bind()
        is MemoryInstruction.I64Load -> i64LoadExecutor(context, instruction).bind()
        is MemoryInstruction.I64Load8S -> i64Load8SExecutor(context, instruction).bind()
        is MemoryInstruction.I64Load8U -> i64Load8UExecutor(context, instruction).bind()
        is MemoryInstruction.I64Load16S -> i64Load16SExecutor(context, instruction).bind()
        is MemoryInstruction.I64Load16U -> i64Load16UExecutor(context, instruction).bind()
        is MemoryInstruction.I64Load32S -> i64Load32SExecutor(context, instruction).bind()
        is MemoryInstruction.I64Load32U -> i64Load32UExecutor(context, instruction).bind()
        is MemoryInstruction.I64Store -> i64StoreExecutor(context, instruction).bind()
        is MemoryInstruction.I64Store8 -> i64Store8Executor(context, instruction).bind()
        is MemoryInstruction.I64Store16 -> i64Store16Executor(context, instruction).bind()
        is MemoryInstruction.I64Store32 -> i64Store32Executor(context, instruction).bind()
        is MemoryInstruction.F32Load -> f32LoadExecutor(context, instruction).bind()
        is MemoryInstruction.F32Store -> f32StoreExecutor(context, instruction).bind()
        is MemoryInstruction.F64Load -> f64LoadExecutor(context, instruction).bind()
        is MemoryInstruction.F64Store -> f64StoreExecutor(context, instruction).bind()
    }
}
