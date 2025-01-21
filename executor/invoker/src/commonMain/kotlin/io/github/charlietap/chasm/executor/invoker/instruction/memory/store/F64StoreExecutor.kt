package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import io.github.charlietap.chasm.executor.memory.BoundsChecker
import io.github.charlietap.chasm.executor.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.executor.memory.write.F64Writer
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popF64
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

fun F64StoreExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.F64Store,
) =
    F64StoreExecutor(
        context = context,
        instruction = instruction,
        boundsChecker = ::PessimisticBoundsChecker,
        writer = ::F64Writer,
    )

internal inline fun F64StoreExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.F64Store,
    crossinline boundsChecker: BoundsChecker<Unit>,
    crossinline writer: F64Writer,
) {
    val stack = context.stack
    val memory = instruction.memory

    val valueToStore = stack.popF64()
    val baseAddress = stack.popI32()
    val effectiveAddress = baseAddress + instruction.memArg.offset

    boundsChecker(effectiveAddress, Double.SIZE_BYTES, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore)
    }
}
