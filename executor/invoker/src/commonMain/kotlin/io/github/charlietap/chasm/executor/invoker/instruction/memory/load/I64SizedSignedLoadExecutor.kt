@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceLongReaderImpl
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I64

internal typealias I64SizedSignedLoadExecutor = (ExecutionContext, Index.MemoryIndex, MemArg, Int) -> Result<Unit, InvocationError>

internal inline fun I64SizedSignedLoadExecutor(
    context: ExecutionContext,
    memoryIndex: Index.MemoryIndex,
    memArg: MemArg,
    sizeInBytes: Int,
): Result<Unit, InvocationError> =
    I64SizedSignedLoadExecutor(
        context = context,
        memoryIndex = memoryIndex,
        memArg = memArg,
        sizeInBytes = sizeInBytes,
        loadNumberValueExecutor = ::LoadNumberValueExecutor,
    )

internal inline fun I64SizedSignedLoadExecutor(
    context: ExecutionContext,
    memoryIndex: Index.MemoryIndex,
    memArg: MemArg,
    sizeInBytes: Int,
    crossinline loadNumberValueExecutor: LoadNumberValueExecutor<Long>,
): Result<Unit, InvocationError> = loadNumberValueExecutor(
    context.store,
    context.stack,
    memoryIndex,
    memArg,
    sizeInBytes,
    ::MemoryInstanceLongReaderImpl,
    ::I64,
)
