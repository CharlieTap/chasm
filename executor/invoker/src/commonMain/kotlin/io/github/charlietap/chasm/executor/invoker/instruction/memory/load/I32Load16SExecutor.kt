package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.read.I3216SReader
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.ext.memoryAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.pushI32
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

internal inline fun I32Load16SExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Load16S,
): Result<Unit, InvocationError> =
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
): Result<Unit, InvocationError> = binding {
    val (stack, store) = context
    val frame = stack.peekFrame().bind()
    val memoryAddress = frame.instance
        .memoryAddress(instruction.memoryIndex)
        .bind()
    val memory = store.memory(memoryAddress).bind()

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
