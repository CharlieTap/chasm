@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.executor.memory.write.MemoryInstanceIntWriterImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.store.Store

internal inline fun I32StoreSizedExecutorImpl(
    store: Store,
    stack: Stack,
    memArg: MemArg,
    sizeInBytes: Int,
): Result<Unit, InvocationError> =
    I32StoreSizedExecutorImpl(
        store = store,
        stack = stack,
        memArg = memArg,
        sizeInBytes = sizeInBytes,
        storeNumberValueExecutor = ::StoreNumberValueExecutorImpl,
    )

internal inline fun I32StoreSizedExecutorImpl(
    store: Store,
    stack: Stack,
    memArg: MemArg,
    sizeInBytes: Int,
    storeNumberValueExecutor: StoreNumberValueExecutor<Int>,
): Result<Unit, InvocationError> = storeNumberValueExecutor(
    store,
    stack,
    memArg,
    sizeInBytes,
    Stack::popI32,
    ::MemoryInstanceIntWriterImpl,
)
