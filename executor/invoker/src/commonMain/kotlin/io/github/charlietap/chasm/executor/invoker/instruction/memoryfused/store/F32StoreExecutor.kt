package io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.memory.write.F32Writer
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun F32StoreExecutor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.F32Store,
) =
    F32StoreExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::PessimisticBoundsChecker,
        writer = ::F32Writer,
    )

internal inline fun F32StoreExecutor(
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.F32Store,
    crossinline boundsChecker: BoundsChecker<Unit>,
    crossinline writer: F32Writer,
) {
    val stack = context.vstack
    val memory = instruction.memory

    val valueToStore = Float.fromBits(instruction.value(stack).toInt())
    val baseAddress = instruction.address(stack).toInt()
    val effectiveAddress = baseAddress + instruction.memArg.offset

    boundsChecker(effectiveAddress, Float.SIZE_BYTES, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore)
    }
}
