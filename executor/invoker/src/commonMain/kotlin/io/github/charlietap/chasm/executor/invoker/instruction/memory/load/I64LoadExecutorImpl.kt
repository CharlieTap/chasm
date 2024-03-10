@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I64

internal inline fun I64LoadExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: MemoryInstruction.I64Load,
): Result<Unit, InvocationError> =
    I64LoadExecutorImpl(
        store = store,
        stack = stack,
        instruction = instruction,
        loadNumberValueExecutor = ::LoadNumberValueExecutorImpl,
    )

internal inline fun I64LoadExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: MemoryInstruction.I64Load,
    crossinline loadNumberValueExecutor: LoadNumberValueExecutor<Long>,
): Result<Unit, InvocationError> = loadNumberValueExecutor(
    store,
    stack,
    instruction.memArg,
    LinearMemory::readLong,
    ::I64,
)
