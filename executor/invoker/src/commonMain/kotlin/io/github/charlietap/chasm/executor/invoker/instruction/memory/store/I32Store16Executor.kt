package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.write.I32ToI16Writer
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

internal inline fun I32Store16Executor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Store16,
) =
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
) {
    val stack = context.vstack
    val memory = instruction.memory

    val valueToStore = stack.popI32()
    val baseAddress = stack.popI32()
    val effectiveAddress = baseAddress + instruction.memArg.offset

    boundsChecker(effectiveAddress, 2, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore)
    }
}
