package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.memory.write.I64ToI8Writer
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

internal inline fun I64Store8Executor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store8,
) =
    I64Store8Executor(
        context = context,
        instruction = instruction,
        boundsChecker = ::PessimisticBoundsChecker,
        writer = ::I64ToI8Writer,
    )

internal inline fun I64Store8Executor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store8,
    crossinline boundsChecker: BoundsChecker<Unit>,
    crossinline writer: I64ToI8Writer,
) {
    val stack = context.vstack
    val memory = instruction.memory

    val valueToStore = stack.popI64()
    val baseAddress = stack.popI32()
    val effectiveAddress = baseAddress + instruction.memArg.offset

    boundsChecker(effectiveAddress, 1, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore)
    }
}
