@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.memory.write.MemoryInstanceIntWriterImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias I32StoreSizedExecutor = (Store, Stack, Index.MemoryIndex, MemArg, Int) -> Result<Unit, InvocationError>

internal inline fun I32StoreSizedExecutor(
    store: Store,
    stack: Stack,
    memoryIndex: Index.MemoryIndex,
    memArg: MemArg,
    sizeInBytes: Int,
): Result<Unit, InvocationError> =
    I32StoreSizedExecutor(
        store = store,
        stack = stack,
        memoryIndex = memoryIndex,
        memArg = memArg,
        sizeInBytes = sizeInBytes,
        storeNumberValueExecutor = ::StoreNumberValueExecutor,
    )

internal inline fun I32StoreSizedExecutor(
    store: Store,
    stack: Stack,
    memoryIndex: Index.MemoryIndex,
    memArg: MemArg,
    sizeInBytes: Int,
    storeNumberValueExecutor: StoreNumberValueExecutor<Int>,
): Result<Unit, InvocationError> = storeNumberValueExecutor(
    store,
    stack,
    memoryIndex,
    memArg,
    sizeInBytes,
    Stack::popI32,
    ::MemoryInstanceIntWriterImpl,
)
