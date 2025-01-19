package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Err
import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.read.I32Reader
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.pushI32
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun I32LoadExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Load,
) =
    I32LoadExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::OptimisticBoundsChecker,
        reader = ::I32Reader,
    )

internal inline fun I32LoadExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Load,
    crossinline boundsChecker: BoundsChecker<Int>,
    crossinline reader: I32Reader,
) {
    val stack = context.stack
    val memory = instruction.memory

    val baseAddress = stack.popI32()
    val offset = instruction.memArg.offset
    val effectiveAddress = baseAddress + offset

    if (baseAddress < 0 || offset < 0) {
        Err(InvocationError.MemoryOperationOutOfBounds).bind()
    }

    val result = boundsChecker(effectiveAddress, Int.SIZE_BYTES, memory.size) {
        reader(memory.data, effectiveAddress).bind()
    }.bind()

    stack.pushI32(result)
}
