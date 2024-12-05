package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceIntReader
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I32

internal inline fun I32LoadExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Load,
): Result<Unit, InvocationError> =
    I32LoadExecutor(
        context = context,
        instruction = instruction,
        loadNumberValueExecutor = ::LoadNumberValueExecutor,
    )

internal inline fun I32LoadExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Load,
    crossinline loadNumberValueExecutor: LoadNumberValueExecutor<Int>,
): Result<Unit, InvocationError> = loadNumberValueExecutor(
    context.store,
    context.stack,
    instruction.memoryIndex,
    instruction.memArg,
    Int.SIZE_BYTES,
    ::MemoryInstanceIntReader,
    ::I32,
)
