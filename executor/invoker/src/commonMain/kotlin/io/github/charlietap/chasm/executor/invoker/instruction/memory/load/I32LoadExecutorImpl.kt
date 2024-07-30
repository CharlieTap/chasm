@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceIntReaderImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I32

internal inline fun I32LoadExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: MemoryInstruction.I32Load,
): Result<Unit, InvocationError> =
    I32LoadExecutorImpl(
        store = store,
        stack = stack,
        instruction = instruction,
        loadNumberValueExecutor = ::LoadNumberValueExecutorImpl,
    )

internal inline fun I32LoadExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: MemoryInstruction.I32Load,
    crossinline loadNumberValueExecutor: LoadNumberValueExecutor<Int>,
): Result<Unit, InvocationError> = loadNumberValueExecutor(
    store,
    stack,
    instruction.memoryIndex,
    instruction.memArg,
    Int.SIZE_BYTES,
    ::MemoryInstanceIntReaderImpl,
    ::I32,
)
