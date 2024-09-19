@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceFloatReaderImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.F32

internal typealias F32LoadExecutor = (Store, Stack, MemoryInstruction.F32Load) -> Result<Unit, InvocationError>

internal inline fun F32LoadExecutor(
    store: Store,
    stack: Stack,
    instruction: MemoryInstruction.F32Load,
): Result<Unit, InvocationError> =
    F32LoadExecutor(
        store = store,
        stack = stack,
        instruction = instruction,
        loadNumberValueExecutor = ::LoadNumberValueExecutor,
    )

internal inline fun F32LoadExecutor(
    store: Store,
    stack: Stack,
    instruction: MemoryInstruction.F32Load,
    crossinline loadNumberValueExecutor: LoadNumberValueExecutor<Float>,
): Result<Unit, InvocationError> = loadNumberValueExecutor(
    store,
    stack,
    instruction.memoryIndex,
    instruction.memArg,
    Float.SIZE_BYTES,
    ::MemoryInstanceFloatReaderImpl,
    ::F32,
)
