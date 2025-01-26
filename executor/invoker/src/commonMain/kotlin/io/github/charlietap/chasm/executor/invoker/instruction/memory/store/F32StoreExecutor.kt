package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.write.F32Writer
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun F32StoreExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.F32Store,
) =
    F32StoreExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::PessimisticBoundsChecker,
        writer = ::F32Writer,
    )

internal inline fun F32StoreExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.F32Store,
    crossinline boundsChecker: BoundsChecker<Unit>,
    crossinline writer: F32Writer,
) {
    val stack = context.vstack
    val memory = instruction.memory

    val valueToStore = stack.popF32()
    val baseAddress = stack.popI32()
    val effectiveAddress = baseAddress + instruction.memArg.offset

    boundsChecker(effectiveAddress, Float.SIZE_BYTES, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore)
    }
}
