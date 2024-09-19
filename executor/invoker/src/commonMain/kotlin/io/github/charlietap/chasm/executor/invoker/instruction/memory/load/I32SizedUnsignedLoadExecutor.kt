@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceUIntReaderImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I32

internal typealias I32SizedUnsignedLoadExecutor = (Store, Stack, Index.MemoryIndex, MemArg, Int) -> Result<Unit, InvocationError>

internal inline fun I32SizedUnsignedLoadExecutor(
    store: Store,
    stack: Stack,
    memoryIndex: Index.MemoryIndex,
    memArg: MemArg,
    sizeInBytes: Int,
): Result<Unit, InvocationError> =
    I32SizedUnsignedLoadExecutor(
        store = store,
        stack = stack,
        memoryIndex = memoryIndex,
        memArg = memArg,
        sizeInBytes = sizeInBytes,
        loadUnsignedNumberValueExecutor = ::LoadUnsignedNumberValueExecutor,
    )

internal inline fun I32SizedUnsignedLoadExecutor(
    store: Store,
    stack: Stack,
    memoryIndex: Index.MemoryIndex,
    memArg: MemArg,
    sizeInBytes: Int,
    crossinline loadUnsignedNumberValueExecutor: LoadUnsignedNumberValueExecutor<Int, UInt>,
): Result<Unit, InvocationError> = loadUnsignedNumberValueExecutor(
    store,
    stack,
    memoryIndex,
    memArg,
    sizeInBytes,
    ::MemoryInstanceUIntReaderImpl,
    UInt::toInt,
    ::I32,
)
