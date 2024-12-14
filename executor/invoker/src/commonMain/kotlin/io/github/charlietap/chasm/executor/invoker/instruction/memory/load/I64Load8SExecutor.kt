@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.read.I648SReader
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.ext.memoryAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.pushI64
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

internal inline fun I64Load8SExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Load8S,
): Result<Unit, InvocationError> =
    I64Load8SExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::OptimisticBoundsChecker,
        reader = ::I648SReader,
    )

internal inline fun I64Load8SExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Load8S,
    crossinline boundsChecker: BoundsChecker<Long>,
    crossinline reader: I648SReader,
): Result<Unit, InvocationError> = binding {
    val (stack, store) = context
    val frame = stack.peekFrame().bind()
    val memoryAddress = frame.state.module.memoryAddress(instruction.memoryIndex).bind()
    val memory = store.memory(memoryAddress).bind()

    val baseAddress = stack.popI32().bind()
    val offset = instruction.memArg.offset.toInt()
    val effectiveAddress = baseAddress + offset

    if (baseAddress < 0 || offset < 0) {
        Err(InvocationError.MemoryOperationOutOfBounds).bind<Unit>()
    }

    val result = boundsChecker(effectiveAddress, 1, memory.size) {
        reader(memory.data, effectiveAddress).bind()
    }.bind()

    stack.pushI64(result)
}
