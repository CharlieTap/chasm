@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.memory.write.MemoryInstanceLongWriterImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popI64
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias I64StoreSizedExecutor = (Store, Stack, Index.MemoryIndex, MemArg, Int) -> Result<Unit, InvocationError>

internal inline fun I64StoreSizedExecutor(
    store: Store,
    stack: Stack,
    memoryIndex: Index.MemoryIndex,
    memArg: MemArg,
    sizeInBytes: Int,
): Result<Unit, InvocationError> =
    I64StoreSizedExecutor(
        store = store,
        stack = stack,
        memoryIndex = memoryIndex,
        memArg = memArg,
        sizeInBytes = sizeInBytes,
        storeNumberValueExecutor = ::StoreNumberValueExecutor,
    )

internal inline fun I64StoreSizedExecutor(
    store: Store,
    stack: Stack,
    memoryIndex: Index.MemoryIndex,
    memArg: MemArg,
    sizeInBytes: Int,
    storeNumberValueExecutor: StoreNumberValueExecutor<Long>,
): Result<Unit, InvocationError> = storeNumberValueExecutor(
    store,
    stack,
    memoryIndex,
    memArg,
    sizeInBytes,
    Stack::popI64,
    ::MemoryInstanceLongWriterImpl,
)
