package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceUIntReader
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I32

internal typealias I32SizedUnsignedLoadExecutor = (ExecutionContext, Index.MemoryIndex, MemArg, Int) -> Result<Unit, InvocationError>

internal inline fun I32SizedUnsignedLoadExecutor(
    context: ExecutionContext,
    memoryIndex: Index.MemoryIndex,
    memArg: MemArg,
    sizeInBytes: Int,
): Result<Unit, InvocationError> =
    I32SizedUnsignedLoadExecutor(
        context = context,
        memoryIndex = memoryIndex,
        memArg = memArg,
        sizeInBytes = sizeInBytes,
        loadUnsignedNumberValueExecutor = ::LoadUnsignedNumberValueExecutor,
    )

internal inline fun I32SizedUnsignedLoadExecutor(
    context: ExecutionContext,
    memoryIndex: Index.MemoryIndex,
    memArg: MemArg,
    sizeInBytes: Int,
    crossinline loadUnsignedNumberValueExecutor: LoadUnsignedNumberValueExecutor<Int, UInt>,
): Result<Unit, InvocationError> = loadUnsignedNumberValueExecutor(
    context.store,
    context.stack,
    memoryIndex,
    memArg,
    sizeInBytes,
    ::MemoryInstanceUIntReader,
    UInt::toInt,
    ::I32,
)
