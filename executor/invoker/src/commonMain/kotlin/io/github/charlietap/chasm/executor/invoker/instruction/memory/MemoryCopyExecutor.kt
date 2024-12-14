package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.memory.copy.LinearMemoryCopier
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.contains
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.ext.memoryAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun MemoryCopyExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopy,
): Result<Unit, InvocationError> =
    MemoryCopyExecutor(
        context = context,
        instruction = instruction,
        copier = ::LinearMemoryCopier,
    )

internal inline fun MemoryCopyExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryCopy,
    crossinline copier: LinearMemoryCopier,
): Result<Unit, InvocationError> = binding {

    val (stack, store) = context
    val frame = stack.peekFrame().bind()
    val srcMemoryAddress = frame.state.module
        .memoryAddress(instruction.srcIndex)
        .bind()
    val srcMemory = store.memory(srcMemoryAddress).bind()
    val dstMemoryAddress = frame.state.module
        .memoryAddress(instruction.dstIndex)
        .bind()
    val dstMemory = store.memory(dstMemoryAddress).bind()

    val bytesToCopy = stack.popI32().bind()
    val sourceOffset = stack.popI32().bind()
    val destinationOffset = stack.popI32().bind()

    if (
        bytesToCopy < 0 ||
        !srcMemory.bounds.contains(sourceOffset..<sourceOffset + bytesToCopy) ||
        !dstMemory.bounds.contains(destinationOffset..<destinationOffset + bytesToCopy)
    ) {
        Err(InvocationError.MemoryOperationOutOfBounds).bind<Unit>()
    }

    if (bytesToCopy == 0) return@binding

    copier(srcMemory.data, dstMemory.data, sourceOffset, destinationOffset, bytesToCopy).bind()
}
