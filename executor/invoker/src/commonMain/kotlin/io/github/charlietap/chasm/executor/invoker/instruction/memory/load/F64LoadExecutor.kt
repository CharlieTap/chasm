@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceDoubleReaderImpl
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.F64

internal inline fun F64LoadExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.F64Load,
): Result<Unit, InvocationError> =
    F64LoadExecutor(
        context = context,
        instruction = instruction,
        loadNumberValueExecutor = ::LoadNumberValueExecutor,
    )

internal inline fun F64LoadExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.F64Load,
    crossinline loadNumberValueExecutor: LoadNumberValueExecutor<Double>,
): Result<Unit, InvocationError> = loadNumberValueExecutor(
    context.store,
    context.stack,
    instruction.memoryIndex,
    instruction.memArg,
    Double.SIZE_BYTES,
    ::MemoryInstanceDoubleReaderImpl,
    ::F64,
)
