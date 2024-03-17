@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.executor.memory.write.MemoryInstanceLongWriterImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popI64
import io.github.charlietap.chasm.executor.runtime.store.Store

internal inline fun I64StoreSizedExecutorImpl(
    store: Store,
    stack: Stack,
    memArg: MemArg,
    sizeInBytes: Int,
): Result<Unit, InvocationError> =
    I64StoreSizedExecutorImpl(
        store = store,
        stack = stack,
        memArg = memArg,
        sizeInBytes = sizeInBytes,
        storeNumberValueExecutor = ::StoreNumberValueExecutorImpl,
    )

internal inline fun I64StoreSizedExecutorImpl(
    store: Store,
    stack: Stack,
    memArg: MemArg,
    sizeInBytes: Int,
    storeNumberValueExecutor: StoreNumberValueExecutor<Long>,
): Result<Unit, InvocationError> = storeNumberValueExecutor(
    store,
    stack,
    memArg,
    sizeInBytes,
    Stack::popI64,
    ::MemoryInstanceLongWriterImpl,
)
