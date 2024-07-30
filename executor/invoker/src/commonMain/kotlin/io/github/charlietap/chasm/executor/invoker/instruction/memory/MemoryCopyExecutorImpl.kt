@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.memory.copy.MemoryInstanceCopier
import io.github.charlietap.chasm.executor.memory.copy.MemoryInstanceCopierImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.ext.memoryAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.store.Store

internal inline fun MemoryCopyExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: MemoryInstruction.MemoryCopy,
): Result<Unit, InvocationError> =
    MemoryCopyExecutorImpl(
        store = store,
        stack = stack,
        instruction = instruction,
        memoryInstanceCopier = ::MemoryInstanceCopierImpl,
    )

internal fun MemoryCopyExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: MemoryInstruction.MemoryCopy,
    memoryInstanceCopier: MemoryInstanceCopier,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrame().bind()
    val srcMemoryAddress = frame.state.module.memoryAddress(instruction.srcIndex.index()).bind()
    val srcMemory = store.memory(srcMemoryAddress).bind()
    val dstMemoryAddress = frame.state.module.memoryAddress(instruction.dstIndex.index()).bind()
    val dstMemory = store.memory(dstMemoryAddress).bind()

    val bytesToCopy = stack.popI32().bind()
    val sourceOffset = stack.popI32().bind()
    val destinationOffset = stack.popI32().bind()

    if (bytesToCopy < 0) {
        Err(InvocationError.MemoryOperationOutOfBounds).bind<Unit>()
    }

    val srcRange = sourceOffset..<(sourceOffset + bytesToCopy)
    val dstRange = destinationOffset..<(destinationOffset + bytesToCopy)

    memoryInstanceCopier(srcMemory, dstMemory, srcRange, dstRange).bind()
}
