@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.memory.fill.MemoryInstanceFiller
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.ext.memoryAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32

internal fun MemoryFillExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFill,
): Result<Unit, InvocationError> =
    MemoryFillExecutor(
        context = context,
        instruction = instruction,
        memoryInstanceFiller = ::MemoryInstanceFiller,
    )

internal inline fun MemoryFillExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryFill,
    crossinline memoryInstanceFiller: MemoryInstanceFiller,
): Result<Unit, InvocationError> = binding {

    val (stack, store) = context
    val bytesToFill = stack.popI32().bind()
    val fillValue = stack.popI32().bind()
    val offset = stack.popI32().bind()

    val frame = stack.peekFrame().bind()
    val memoryAddress = frame.state.module.memoryAddress(instruction.memoryIndex).bind()
    val memory = store.memory(memoryAddress).bind()

    memoryInstanceFiller(memory, offset..(offset + bytesToFill), fillValue.toByte()).bind()
}
