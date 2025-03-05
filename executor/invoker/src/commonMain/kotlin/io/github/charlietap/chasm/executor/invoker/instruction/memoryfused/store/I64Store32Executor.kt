package io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.memory.write.I64ToI32Writer
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun I64Store32Executor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Store32,
) =
    I64Store32Executor(
        context = context,
        instruction = instruction,
        boundsChecker = ::PessimisticBoundsChecker,
        writer = ::I64ToI32Writer,
    )

internal inline fun I64Store32Executor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Store32,
    crossinline boundsChecker: BoundsChecker<Unit>,
    crossinline writer: I64ToI32Writer,
) {
    val stack = context.vstack
    val memory = instruction.memory

    val valueToStore = instruction.value(stack)
    val baseAddress = instruction.address(stack).toInt()
    val effectiveAddress = baseAddress + instruction.memArg.offset

    boundsChecker(effectiveAddress, Int.SIZE_BYTES, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore)
    }
}
