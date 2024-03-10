@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.F32

internal inline fun F32LoadExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: MemoryInstruction.F32Load,
): Result<Unit, InvocationError> =
    F32LoadExecutorImpl(
        store = store,
        stack = stack,
        instruction = instruction,
        loadNumberValueExecutor = ::LoadNumberValueExecutorImpl,
    )

internal inline fun F32LoadExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: MemoryInstruction.F32Load,
    crossinline loadNumberValueExecutor: LoadNumberValueExecutor<Float>,
): Result<Unit, InvocationError> = loadNumberValueExecutor(
    store,
    stack,
    instruction.memArg,
    LinearMemory::readFloat,
    ::F32,
)
