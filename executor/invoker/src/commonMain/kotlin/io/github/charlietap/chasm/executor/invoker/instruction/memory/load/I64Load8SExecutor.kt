package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Err
import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.read.I648SReader
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.pushI64
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

internal inline fun I64Load8SExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Load8S,
) =
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
) {
    val stack = context.stack
    val memory = instruction.memory

    val baseAddress = stack.popI32()
    val offset = instruction.memArg.offset
    val effectiveAddress = baseAddress + offset

    if (baseAddress < 0 || offset < 0) {
        Err(InvocationError.MemoryOperationOutOfBounds).bind()
    }

    val result = boundsChecker(effectiveAddress, 1, memory.size) {
        reader(memory.data, effectiveAddress).bind()
    }.bind()

    stack.pushI64(result)
}
