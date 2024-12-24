package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.read.I3216UReader
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.pushI32
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

internal inline fun I32Load16UExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Load16U,
): Result<Unit, InvocationError> =
    I32Load16UExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::OptimisticBoundsChecker,
        reader = ::I3216UReader,
    )

internal inline fun I32Load16UExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Load16U,
    crossinline boundsChecker: BoundsChecker<Int>,
    crossinline reader: I3216UReader,
): Result<Unit, InvocationError> = binding {
    val stack = context.stack
    val memory = instruction.memory

    val baseAddress = stack.popI32().bind()
    val offset = instruction.memArg.offset.toInt()
    val effectiveAddress = baseAddress + offset

    if (baseAddress < 0 || offset < 0) {
        Err(InvocationError.MemoryOperationOutOfBounds).bind<Unit>()
    }

    val result = boundsChecker(effectiveAddress, 2, memory.size) {
        reader(memory.data, effectiveAddress).bind()
    }.bind()

    stack.pushI32(result)
}
