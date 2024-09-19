@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceLongReaderImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I64

internal typealias I64LoadExecutor = (Store, Stack, MemoryInstruction.I64Load) -> Result<Unit, InvocationError>

internal inline fun I64LoadExecutor(
    store: Store,
    stack: Stack,
    instruction: MemoryInstruction.I64Load,
): Result<Unit, InvocationError> =
    I64LoadExecutor(
        store = store,
        stack = stack,
        instruction = instruction,
        loadNumberValueExecutor = ::LoadNumberValueExecutor,
    )

internal inline fun I64LoadExecutor(
    store: Store,
    stack: Stack,
    instruction: MemoryInstruction.I64Load,
    crossinline loadNumberValueExecutor: LoadNumberValueExecutor<Long>,
): Result<Unit, InvocationError> = loadNumberValueExecutor(
    store,
    stack,
    instruction.memoryIndex,
    instruction.memArg,
    Long.SIZE_BYTES,
    ::MemoryInstanceLongReaderImpl,
    ::I64,
)
