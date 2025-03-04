package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.memory.write.I32ToI8Writer
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun I32Store8Executor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Store8,
) =
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
) {
    val stack = context.vstack
    val memory = instruction.memory

    val valueToStore = stack.popI32()
    val baseAddress = stack.popI32()
    val effectiveAddress = baseAddress + instruction.memArg.offset

    boundsChecker(effectiveAddress, 1, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore)
    }
}
