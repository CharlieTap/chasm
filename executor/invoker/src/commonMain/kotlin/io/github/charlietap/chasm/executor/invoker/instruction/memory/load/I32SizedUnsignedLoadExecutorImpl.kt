@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceUIntReaderImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I32

internal inline fun I32SizedUnsignedLoadExecutorImpl(
    store: Store,
    stack: Stack,
    memArg: MemArg,
    sizeInBytes: Int,
): Result<Unit, InvocationError> =
    I32SizedUnsignedLoadExecutorImpl(
        store = store,
        stack = stack,
        memArg = memArg,
        sizeInBytes = sizeInBytes,
        loadUnsignedNumberValueExecutor = ::LoadUnsignedNumberValueExecutorImpl,
    )

internal inline fun I32SizedUnsignedLoadExecutorImpl(
    store: Store,
    stack: Stack,
    memArg: MemArg,
    sizeInBytes: Int,
    crossinline loadUnsignedNumberValueExecutor: LoadUnsignedNumberValueExecutor<Int, UInt>,
): Result<Unit, InvocationError> = loadUnsignedNumberValueExecutor(
    store,
    stack,
    memArg,
    sizeInBytes,
    ::MemoryInstanceUIntReaderImpl,
    UInt::toInt,
    ::I32,
)
