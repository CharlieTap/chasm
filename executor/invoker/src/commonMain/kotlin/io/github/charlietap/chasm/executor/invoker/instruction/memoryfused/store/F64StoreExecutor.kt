package io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store

import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.write.F64Writer
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun F64StoreExecutor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.F64Store,
) =
    F64StoreExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::PessimisticBoundsChecker,
        writer = ::F64Writer,
    )

internal inline fun F64StoreExecutor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.F64Store,
    crossinline boundsChecker: BoundsChecker<Unit>,
    crossinline writer: F64Writer,
) {
    val stack = context.vstack
    val memory = instruction.memory

    val valueToStore = Double.fromBits(instruction.value(stack))
    val baseAddress = instruction.address(stack).toInt()
    val effectiveAddress = baseAddress + instruction.memArg.offset

    boundsChecker(effectiveAddress, Double.SIZE_BYTES, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore)
    }
}
