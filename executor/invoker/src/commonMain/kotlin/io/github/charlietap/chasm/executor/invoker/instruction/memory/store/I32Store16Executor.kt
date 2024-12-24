package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.write.I32ToI16Writer
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

internal inline fun I32Store16Executor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Store16,
): Result<Unit, InvocationError> =
    I32Store16Executor(
        context = context,
        instruction = instruction,
        boundsChecker = ::PessimisticBoundsChecker,
        writer = ::I32ToI16Writer,
    )

internal inline fun I32Store16Executor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Store16,
    crossinline boundsChecker: BoundsChecker<Unit>,
    crossinline writer: I32ToI16Writer,
): Result<Unit, InvocationError> = binding {
    val stack = context.stack
    val memory = instruction.memory

    val valueToStore = stack.popI32().bind()
    val baseAddress = stack.popI32().bind()
    val effectiveAddress = baseAddress + instruction.memArg.offset.toInt()

    boundsChecker(effectiveAddress, 2, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore).bind()
    }.bind()
}
