@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
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
): Result<Unit, InvocationError> =
    MemoryCopyExecutorImpl(
        store = store,
        stack = stack,
        memoryInstanceCopier = ::MemoryInstanceCopierImpl,
    )

internal fun MemoryCopyExecutorImpl(
    store: Store,
    stack: Stack,
    memoryInstanceCopier: MemoryInstanceCopier,
): Result<Unit, InvocationError> = binding {

    val bytesToCopy = stack.popI32().bind()
    val sourceOffset = stack.popI32().bind()
    val destinationOffset = stack.popI32().bind()

    if (bytesToCopy == 0) return@binding

    val frame = stack.peekFrame().bind()
    val memoryAddress = frame.state.module.memoryAddress(0).bind()
    val memory = store.memory(memoryAddress).bind()

    val srcRange = sourceOffset..<(sourceOffset + bytesToCopy)
    val dstRange = destinationOffset..<(destinationOffset + bytesToCopy)

    memoryInstanceCopier(memory, memory, srcRange, dstRange).bind()
}
