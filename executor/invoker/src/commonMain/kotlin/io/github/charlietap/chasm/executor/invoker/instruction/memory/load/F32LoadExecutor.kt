package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.read.F32Reader
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.pushf32
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun F32LoadExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.F32Load,
): Result<Unit, InvocationError> =
    F32LoadExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::OptimisticBoundsChecker,
        reader = ::F32Reader,
    )

internal inline fun F32LoadExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.F32Load,
    crossinline boundsChecker: BoundsChecker<Float>,
    crossinline reader: F32Reader,
): Result<Unit, InvocationError> = binding {
    val stack = context.stack
    val memory = instruction.memory

    val baseAddress = stack.popI32().bind()
    val offset = instruction.memArg.offset.toInt()
    val effectiveAddress = baseAddress + offset

    if (baseAddress < 0 || offset < 0) {
        Err(InvocationError.MemoryOperationOutOfBounds).bind<Unit>()
    }

    val result = boundsChecker(effectiveAddress, Float.SIZE_BYTES, memory.size) {
        reader(memory.data, effectiveAddress).bind()
    }.bind()

    stack.pushf32(result)
}
