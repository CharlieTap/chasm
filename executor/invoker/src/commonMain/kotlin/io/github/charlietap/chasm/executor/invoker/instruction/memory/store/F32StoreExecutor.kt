@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.memory.write.MemoryInstanceFloatWriterImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popF32

internal inline fun F32StoreExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.F32Store,
): Result<Unit, InvocationError> =
    F32StoreExecutor(
        context = context,
        instruction = instruction,
        storeNumberValueExecutor = ::StoreNumberValueExecutor,
    )

internal inline fun F32StoreExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.F32Store,
    storeNumberValueExecutor: StoreNumberValueExecutor<Float>,
): Result<Unit, InvocationError> = storeNumberValueExecutor(
    context.store,
    context.stack,
    instruction.memoryIndex,
    instruction.memArg,
    Float.SIZE_BYTES,
    Stack::popF32,
    ::MemoryInstanceFloatWriterImpl,
)
