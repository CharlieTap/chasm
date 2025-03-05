package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.write.I64ToI32Writer
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

internal inline fun I64Store32Executor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store32,
) =
    I64Store32Executor(
        context = context,
        instruction = instruction,
        boundsChecker = ::PessimisticBoundsChecker,
        writer = ::I64ToI32Writer,
    )

internal inline fun I64Store32Executor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store32,
    crossinline boundsChecker: BoundsChecker<Unit>,
    crossinline writer: I64ToI32Writer,
) {
    val stack = context.vstack
    val memory = instruction.memory

    val valueToStore = stack.popI64()
    val baseAddress = stack.popI32()
    val effectiveAddress = baseAddress + instruction.memArg.offset

    boundsChecker(effectiveAddress, 4, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore)
    }
}
