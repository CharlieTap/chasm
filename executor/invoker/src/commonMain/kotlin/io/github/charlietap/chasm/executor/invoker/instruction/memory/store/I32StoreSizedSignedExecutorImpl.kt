@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.executor.invoker.ext.popI32
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.executor.runtime.store.Store

internal inline fun I32StoreSizedSignedExecutorImpl(
    store: Store,
    stack: Stack,
    memArg: MemArg,
    sizeInBytes: Int,
): Result<Unit, InvocationError> =
    I32StoreSizedSignedExecutorImpl(
        store = store,
        stack = stack,
        memArg = memArg,
        sizeInBytes = sizeInBytes,
        storeSizedNumberValueExecutor = ::StoreSizedNumberValueExecutorImpl,
    )

internal inline fun I32StoreSizedSignedExecutorImpl(
    store: Store,
    stack: Stack,
    memArg: MemArg,
    sizeInBytes: Int,
    storeSizedNumberValueExecutor: StoreSizedNumberValueExecutor<Int>,
): Result<Unit, InvocationError> = storeSizedNumberValueExecutor(
    store,
    stack,
    memArg,
    sizeInBytes,
    Stack::popI32,
    LinearMemory::writeInt,
)
