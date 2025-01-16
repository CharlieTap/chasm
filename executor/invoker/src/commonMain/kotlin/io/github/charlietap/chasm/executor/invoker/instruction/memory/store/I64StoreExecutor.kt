package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.write.I64Writer
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.popI64
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun I64StoreExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store,
) =
    I64StoreExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::PessimisticBoundsChecker,
        writer = ::I64Writer,
    )

internal inline fun I64StoreExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store,
    crossinline boundsChecker: BoundsChecker<Unit>,
    crossinline writer: I64Writer,
) {
    val stack = context.stack
    val memory = instruction.memory

    val valueToStore = stack.popI64()

    val baseAddress = stack.popI32()
    val effectiveAddress = baseAddress + instruction.memArg.offset.toInt()

    boundsChecker(effectiveAddress, Long.SIZE_BYTES, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore).bind()
    }.bind()
}
