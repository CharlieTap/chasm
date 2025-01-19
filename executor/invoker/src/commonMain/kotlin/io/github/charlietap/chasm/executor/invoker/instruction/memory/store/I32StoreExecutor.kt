package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.write.I32Writer
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun I32StoreExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Store,
) =
    I32StoreExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::PessimisticBoundsChecker,
        writer = ::I32Writer,
    )

internal inline fun I32StoreExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Store,
    crossinline boundsChecker: BoundsChecker<Unit>,
    crossinline writer: I32Writer,
) {
    val stack = context.stack
    val memory = instruction.memory

    val valueToStore = stack.popI32()
    val baseAddress = stack.popI32()
    val effectiveAddress = baseAddress + instruction.memArg.offset

    boundsChecker(effectiveAddress, Int.SIZE_BYTES, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore).bind()
    }.bind()
}
