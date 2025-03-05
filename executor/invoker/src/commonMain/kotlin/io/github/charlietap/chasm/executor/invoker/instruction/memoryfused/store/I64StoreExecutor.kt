package io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store

import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.write.I64Writer
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun I64StoreExecutor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Store,
) =
    I64StoreExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::PessimisticBoundsChecker,
        writer = ::I64Writer,
    )

internal inline fun I64StoreExecutor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Store,
    crossinline boundsChecker: BoundsChecker<Unit>,
    crossinline writer: I64Writer,
) {
    val stack = context.vstack
    val memory = instruction.memory

    val valueToStore = instruction.value(stack)
    val baseAddress = instruction.address(stack).toInt()
    val effectiveAddress = baseAddress + instruction.memArg.offset

    boundsChecker(effectiveAddress, Long.SIZE_BYTES, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore)
    }
}
