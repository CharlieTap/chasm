package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.write.I32ToI8Writer
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun I32Store8Executor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Store8,
): Result<Unit, InvocationError> =
    I32Store8Executor(
        context = context,
        instruction = instruction,
        boundsChecker = ::PessimisticBoundsChecker,
        writer = ::I32ToI8Writer,
    )

internal inline fun I32Store8Executor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Store8,
    crossinline boundsChecker: BoundsChecker<Unit>,
    crossinline writer: I32ToI8Writer,
): Result<Unit, InvocationError> = binding {
    val stack = context.stack
    val memory = instruction.memory

    val valueToStore = stack.popI32().bind()
    val baseAddress = stack.popI32().bind()
    val effectiveAddress = baseAddress + instruction.memArg.offset.toInt()

    boundsChecker(effectiveAddress, 1, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore).bind()
    }.bind()
}
