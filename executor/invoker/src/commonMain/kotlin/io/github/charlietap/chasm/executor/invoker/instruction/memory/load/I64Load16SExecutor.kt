package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.read.I6416SReader
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.pushI64
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

internal inline fun I64Load16SExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Load16S,
): Result<Unit, InvocationError> =
    I64Load16SExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::OptimisticBoundsChecker,
        reader = ::I6416SReader,
    )

internal inline fun I64Load16SExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Load16S,
    crossinline boundsChecker: BoundsChecker<Long>,
    crossinline reader: I6416SReader,
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

    stack.pushI64(result)
}
