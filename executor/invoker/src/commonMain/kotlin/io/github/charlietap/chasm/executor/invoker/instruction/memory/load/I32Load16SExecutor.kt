package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Err
import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.read.I3216SReader
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.pushI32
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

internal inline fun I32Load16SExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Load16S,
) =
    I32Load16SExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::OptimisticBoundsChecker,
        reader = ::I3216SReader,
    )

internal inline fun I32Load16SExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Load16S,
    crossinline boundsChecker: BoundsChecker<Int>,
    crossinline reader: I3216SReader,
) {
    val stack = context.stack
    val memory = instruction.memory

    val baseAddress = stack.popI32()
    val offset = instruction.memArg.offset
    val effectiveAddress = baseAddress + offset

    if (baseAddress < 0 || offset < 0) {
        Err(InvocationError.MemoryOperationOutOfBounds).bind()
    }

    val result = boundsChecker(effectiveAddress, 2, memory.size) {
        reader(memory.data, effectiveAddress).bind()
    }.bind()

    stack.pushI32(result)
}
