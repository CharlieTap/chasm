package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.memory.copy.MemoryInstanceCopier
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.ext.memoryAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32

internal fun MemoryCopyExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopy,
): Result<Unit, InvocationError> =
    MemoryCopyExecutor(
        context = context,
        instruction = instruction,
        memoryInstanceCopier = ::MemoryInstanceCopier,
    )

internal inline fun MemoryCopyExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopy,
    crossinline memoryInstanceCopier: MemoryInstanceCopier,
): Result<Unit, InvocationError> = binding {

    val (stack, store) = context
    val frame = stack.peekFrame().bind()
    val srcMemoryAddress = frame.state.module.memoryAddress(instruction.srcIndex).bind()
    val srcMemory = store.memory(srcMemoryAddress).bind()
    val dstMemoryAddress = frame.state.module.memoryAddress(instruction.dstIndex).bind()
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
