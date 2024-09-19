@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.memory.write.MemoryInstanceIntWriterImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias I32StoreExecutor = (Store, Stack, MemoryInstruction.I32Store) -> Result<Unit, InvocationError>

internal inline fun I32StoreExecutor(
    store: Store,
    stack: Stack,
    instruction: MemoryInstruction.I32Store,
): Result<Unit, InvocationError> =
    I32StoreExecutor(
        store = store,
        stack = stack,
        instruction = instruction,
        storeNumberValueExecutor = ::StoreNumberValueExecutor,
    )

internal inline fun I32StoreExecutor(
    store: Store,
    stack: Stack,
    instruction: MemoryInstruction.I32Store,
    storeNumberValueExecutor: StoreNumberValueExecutor<Int>,
): Result<Unit, InvocationError> = storeNumberValueExecutor(
    store,
    stack,
    instruction.memoryIndex,
    instruction.memArg,
    Int.SIZE_BYTES,
    Stack::popI32,
    ::MemoryInstanceIntWriterImpl,
)
