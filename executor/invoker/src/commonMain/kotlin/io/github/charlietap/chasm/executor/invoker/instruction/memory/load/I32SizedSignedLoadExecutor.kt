@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceIntReaderImpl
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I32

internal typealias I32SizedSignedLoadExecutor = (ExecutionContext, Index.MemoryIndex, MemArg, Int) -> Result<Unit, InvocationError>

internal inline fun I32SizedSignedLoadExecutor(
    context: ExecutionContext,
    memoryIndex: Index.MemoryIndex,
    memArg: MemArg,
    sizeInBytes: Int,
): Result<Unit, InvocationError> =
    I32SizedSignedLoadExecutor(
        context = context,
        memoryIndex = memoryIndex,
        memArg = memArg,
        sizeInBytes = sizeInBytes,
        loadNumberValueExecutor = ::LoadNumberValueExecutor,
    )

internal inline fun I32SizedSignedLoadExecutor(
    context: ExecutionContext,
    memoryIndex: Index.MemoryIndex,
    memArg: MemArg,
    sizeInBytes: Int,
    crossinline loadNumberValueExecutor: LoadNumberValueExecutor<Int>,
): Result<Unit, InvocationError> = loadNumberValueExecutor(
    context.store,
    context.stack,
    memoryIndex,
    memArg,
    sizeInBytes,
    ::MemoryInstanceIntReaderImpl,
    ::I32,
)
