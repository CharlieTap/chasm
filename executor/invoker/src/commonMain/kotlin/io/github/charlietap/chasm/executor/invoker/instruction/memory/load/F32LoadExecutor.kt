@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceFloatReader
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.F32

internal inline fun F32LoadExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.F32Load,
): Result<Unit, InvocationError> =
    F32LoadExecutor(
        context = context,
        instruction = instruction,
        loadNumberValueExecutor = ::LoadNumberValueExecutor,
    )

internal inline fun F32LoadExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.F32Load,
    crossinline loadNumberValueExecutor: LoadNumberValueExecutor<Float>,
): Result<Unit, InvocationError> = loadNumberValueExecutor(
    context.store,
    context.stack,
    instruction.memoryIndex,
    instruction.memArg,
    Float.SIZE_BYTES,
    ::MemoryInstanceFloatReader,
    ::F32,
)
